package com.group_7.library_management.service;

import com.group_7.library_management.dto.BorrowOrderResponse;
import com.group_7.library_management.dto.CreateBorrowOrderRequest;
import com.group_7.library_management.dto.CurrentBorrowOrderResponse;
import com.group_7.library_management.entity.Book;
import com.group_7.library_management.entity.BookCopy;
import com.group_7.library_management.entity.BookCopyStatus;
import com.group_7.library_management.entity.BorrowRecord;
import com.group_7.library_management.entity.BorrowStatus;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.exception.ConflictException;
import com.group_7.library_management.exception.ResourceNotFoundException;
import com.group_7.library_management.repository.BookCopyRepository;
import com.group_7.library_management.repository.BookRepository;
import com.group_7.library_management.repository.BorrowRecordRepository;
import com.group_7.library_management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class BorrowOrderService {
    private static final long DEPOSIT_AMOUNT = 150_000L;
    private static final String PICKUP_LOCATION = "Thư viện UTH";
    private static final List<BorrowStatus> ACTIVE_STATUSES = List.of(
            BorrowStatus.REQUESTED,
            BorrowStatus.BORROWED,
            BorrowStatus.OVERDUE
    );

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookAvailabilitySubscriptionService availabilitySubscriptionService;

    public BorrowOrderService(
            BorrowRecordRepository borrowRecordRepository,
            BookCopyRepository bookCopyRepository,
            BookRepository bookRepository,
            UserRepository userRepository,
            BookAvailabilitySubscriptionService availabilitySubscriptionService
    ) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.availabilitySubscriptionService = availabilitySubscriptionService;
    }

    @Transactional
    public BorrowOrderResponse createOrder(Long userId, CreateBorrowOrderRequest request) {
        Book book = bookRepository.findByIdAndActiveTrue(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        if (borrowRecordRepository.existsByUserIdAndBookCopyBookIdAndStatusIn(
                userId,
                book.getId(),
                ACTIVE_STATUSES
        )) {
            throw new ConflictException("Bạn đang có một đơn mượn chưa hoàn tất cho sách này");
        }

        long previousAvailable = bookCopyRepository.countByBookIdAndStatus(
                book.getId(),
                BookCopyStatus.AVAILABLE
        );
        BookCopy copy = bookCopyRepository
                .findFirstByBookIdAndStatusOrderByIdAsc(book.getId(), BookCopyStatus.AVAILABLE)
                .orElseThrow(() -> new ConflictException("Sách hiện đã hết"));

        copy.setStatus(BookCopyStatus.RESERVED);
        Instant now = Instant.now();
        BorrowRecord order = new BorrowRecord(
                createReferenceCode(),
                user,
                copy,
                request.borrowDays(),
                now.plus(request.borrowDays(), ChronoUnit.DAYS),
                PICKUP_LOCATION,
                book.getBorrowFee(),
                DEPOSIT_AMOUNT
        );
        BorrowRecord savedOrder = borrowRecordRepository.saveAndFlush(order);
        bookCopyRepository.flush();
        availabilitySubscriptionService.notifyAvailabilityChanged(
                book,
                previousAvailable,
                Math.max(previousAvailable - 1, 0)
        );
        return BorrowOrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public BorrowOrderResponse getOrder(Long userId, Long orderId) {
        BorrowRecord order = borrowRecordRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn mượn"));
        return BorrowOrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<BorrowOrderResponse> getOrders(Long userId) {
        return borrowRecordRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(BorrowOrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CurrentBorrowOrderResponse getCurrentOrderForBook(Long userId, Long bookId) {
        BorrowOrderResponse order = borrowRecordRepository
                .findFirstByUserIdAndBookCopyBookIdAndStatusInOrderByCreatedAtDesc(
                        userId,
                        bookId,
                        ACTIVE_STATUSES
                )
                .map(BorrowOrderResponse::from)
                .orElse(null);
        return new CurrentBorrowOrderResponse(order);
    }

    @Transactional
    public BorrowOrderResponse getOrderForLibrarian(String referenceCode) {
        return BorrowOrderResponse.from(findOrderForUpdate(referenceCode));
    }

    @Transactional
    public BorrowOrderResponse confirmPickup(String referenceCode) {
        BorrowRecord order = findOrderForUpdate(referenceCode);
        if (order.getStatus() != BorrowStatus.REQUESTED) {
            throw new ConflictException("Chỉ có thể giao sách cho đơn đang chờ lấy");
        }
        if (order.getBookCopy().getStatus() != BookCopyStatus.RESERVED) {
            throw new ConflictException("Bản sách của đơn này không còn ở trạng thái giữ chỗ");
        }

        Instant now = Instant.now();
        order.setStatus(BorrowStatus.BORROWED);
        order.setBorrowedAt(now);
        order.setDueAt(now.plus(order.getBorrowDays(), ChronoUnit.DAYS));
        order.getBookCopy().setStatus(BookCopyStatus.BORROWED);
        return BorrowOrderResponse.from(borrowRecordRepository.saveAndFlush(order));
    }

    @Transactional
    public BorrowOrderResponse confirmReturn(String referenceCode) {
        BorrowRecord order = findOrderForUpdate(referenceCode);
        if (order.getStatus() != BorrowStatus.BORROWED && order.getStatus() != BorrowStatus.OVERDUE) {
            throw new ConflictException("Chỉ có thể trả sách cho đơn đang mượn hoặc đã quá hạn");
        }

        Book book = order.getBookCopy().getBook();
        long previousAvailable = bookCopyRepository.countByBookIdAndStatus(
                book.getId(),
                BookCopyStatus.AVAILABLE
        );
        order.setStatus(BorrowStatus.RETURNED);
        order.setReturnedAt(Instant.now());
        order.getBookCopy().setStatus(BookCopyStatus.AVAILABLE);
        BorrowRecord savedOrder = borrowRecordRepository.saveAndFlush(order);
        bookCopyRepository.flush();
        availabilitySubscriptionService.notifyAvailabilityChanged(
                book,
                previousAvailable,
                previousAvailable + 1
        );
        return BorrowOrderResponse.from(savedOrder);
    }

    private BorrowRecord findOrderForUpdate(String referenceCode) {
        return borrowRecordRepository.findByReferenceCode(referenceCode.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn mượn"));
    }

    private String createReferenceCode() {
        return "Group2-" + UUID.randomUUID().toString().replace("-", "")
                .substring(0, 12).toUpperCase();
    }
}
