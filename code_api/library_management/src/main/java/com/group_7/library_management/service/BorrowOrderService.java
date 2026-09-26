package com.group_7.library_management.service;

import com.group_7.library_management.dto.BorrowOrderResponse;
import com.group_7.library_management.dto.CancelBorrowOrderResponse;
import com.group_7.library_management.dto.CreateBorrowOrderRequest;
import com.group_7.library_management.dto.CurrentBorrowOrderResponse;
import com.group_7.library_management.entity.Book;
import com.group_7.library_management.entity.BookCopy;
import com.group_7.library_management.entity.BookCopyStatus;
import com.group_7.library_management.entity.BorrowRecord;
import com.group_7.library_management.entity.BorrowStatus;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.entity.PaymentMethod;
import com.group_7.library_management.exception.ConflictException;
import com.group_7.library_management.exception.ResourceNotFoundException;
import com.group_7.library_management.repository.BookCopyRepository;
import com.group_7.library_management.repository.BookRepository;
import com.group_7.library_management.repository.BorrowRecordRepository;
import com.group_7.library_management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.Locale;

@Service
public class BorrowOrderService {
    private static final long DEPOSIT_AMOUNT = 150_000L;
    private static final String PICKUP_LOCATION = "Thư viện UTH";
    private static final int MONTHLY_CANCELLATION_LIMIT = 5;
    private static final long CANCELLATION_WINDOW_HOURS = 24L;
    private static final ZoneId CANCELLATION_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final List<BorrowStatus> ACTIVE_STATUSES = List.of(
            BorrowStatus.PENDING_PAYMENT,
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
    public List<BorrowOrderResponse> getBookHistory(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Không tìm thấy sách");
        }
        return borrowRecordRepository.findAllByBookCopyBookIdOrderByCreatedAtDesc(bookId).stream()
                .map(BorrowOrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<BorrowOrderResponse> getOrdersForAdmin(
            String search,
            String status,
            Pageable pageable
    ) {
        String normalizedSearch = search == null || search.isBlank() ? null : search.strip();
        BorrowStatus normalizedStatus = parseAdminStatus(status);
        return borrowRecordRepository.searchForAdmin(
                        normalizedSearch,
                        normalizedStatus,
                        Instant.now(),
                        pageable
                )
                .map(BorrowOrderResponse::from);
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
    public CancelBorrowOrderResponse cancelOrder(Long userId, Long orderId) {
        userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        BorrowRecord order = borrowRecordRepository.findForCancellation(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn mượn"));

        if (order.getStatus() != BorrowStatus.PENDING_PAYMENT
                && order.getStatus() != BorrowStatus.REQUESTED) {
            throw new ConflictException("Chỉ có thể hủy đơn đang chờ thanh toán hoặc chờ nhận sách");
        }
        if (order.getPaidAmount() > 0L) {
            throw new ConflictException("Đơn đã thanh toán, vui lòng liên hệ thư viện để được hỗ trợ hủy và hoàn tiền");
        }

        Instant now = Instant.now();
        if (now.isAfter(order.getCreatedAt().plus(CANCELLATION_WINDOW_HOURS, ChronoUnit.HOURS))) {
            throw new ConflictException("Đơn mượn đã quá thời hạn hủy 24 giờ");
        }

        var localNow = now.atZone(CANCELLATION_ZONE);
        Instant monthStart = localNow.withDayOfMonth(1)
                .toLocalDate()
                .atStartOfDay(CANCELLATION_ZONE)
                .toInstant();
        Instant nextMonthStart = localNow.withDayOfMonth(1)
                .plusMonths(1)
                .toLocalDate()
                .atStartOfDay(CANCELLATION_ZONE)
                .toInstant();
        long cancellationsThisMonth = borrowRecordRepository
                .countByUserIdAndStatusAndCancelledAtGreaterThanEqualAndCancelledAtLessThan(
                        userId,
                        BorrowStatus.CANCELLED,
                        monthStart,
                        nextMonthStart
                );
        if (cancellationsThisMonth >= MONTHLY_CANCELLATION_LIMIT) {
            throw new ConflictException("Bạn đã sử dụng hết 5 lượt hủy đơn trong tháng này");
        }

        Book book = order.getBookCopy().getBook();
        long previousAvailable = bookCopyRepository.countByBookIdAndStatus(
                book.getId(),
                BookCopyStatus.AVAILABLE
        );
        order.setStatus(BorrowStatus.CANCELLED);
        order.setCancelledAt(now);
        order.getBookCopy().setStatus(BookCopyStatus.AVAILABLE);
        BorrowRecord cancelledOrder = borrowRecordRepository.saveAndFlush(order);
        bookCopyRepository.flush();
        availabilitySubscriptionService.notifyAvailabilityChanged(
                book,
                previousAvailable,
                previousAvailable + 1
        );

        int remainingCancellations = Math.toIntExact(
                MONTHLY_CANCELLATION_LIMIT - cancellationsThisMonth - 1
        );
        return new CancelBorrowOrderResponse(
                BorrowOrderResponse.from(cancelledOrder),
                remainingCancellations
        );
    }

    @Transactional
    public BorrowOrderResponse getOrderForLibrarian(String referenceCode) {
        return BorrowOrderResponse.from(findOrderForUpdate(referenceCode));
    }

    @Transactional(readOnly = true)
    public BorrowOrderResponse findOrderForReturn(String query) {
        String normalizedQuery = query == null ? "" : query.strip();
        if (normalizedQuery.isEmpty()) {
            throw new com.group_7.library_management.exception.BadRequestException(
                    "Vui lòng nhập mã đơn hoặc mã bản sách"
            );
        }
        BorrowRecord order = borrowRecordRepository
                .findFirstByReferenceCodeIgnoreCaseOrBookCopyBarcodeIgnoreCaseOrderByCreatedAtDesc(
                        normalizedQuery,
                        normalizedQuery
                )
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn mượn"));
        BorrowOrderResponse response = BorrowOrderResponse.from(order);
        if (response.status() != BorrowStatus.BORROWED
                && response.status() != BorrowStatus.OVERDUE
                && response.status() != BorrowStatus.RETURNED) {
            throw new ConflictException("Đơn này chưa ở trạng thái có thể trả hoặc hoàn cọc");
        }
        return response;
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
        if (order.getPaidAmount() < order.getTotalAmount()) {
            order.markPaid(order.getTotalAmount(), now, PaymentMethod.CASH);
        }
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

    @Transactional
    public BorrowOrderResponse confirmDepositRefund(String referenceCode) {
        BorrowRecord order = findOrderForUpdate(referenceCode);
        if (order.getStatus() != BorrowStatus.RETURNED) {
            throw new ConflictException("Chỉ có thể hoàn tiền cọc sau khi sách đã được trả");
        }
        if (order.getDepositRefundedAt() == null) {
            order.setDepositRefundedAt(Instant.now());
        }
        return BorrowOrderResponse.from(borrowRecordRepository.saveAndFlush(order));
    }

    private BorrowRecord findOrderForUpdate(String referenceCode) {
        return borrowRecordRepository.findByReferenceCode(referenceCode.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn mượn"));
    }

    private BorrowStatus parseAdminStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        String normalized = status.strip().toUpperCase(Locale.ROOT);
        if ("BORROWING".equals(normalized)) {
            normalized = "BORROWED";
        }
        if ("PENDING".equals(normalized)) {
            normalized = "REQUESTED";
        }
        try {
            return BorrowStatus.valueOf(normalized);
        } catch (IllegalArgumentException exception) {
            throw new com.group_7.library_management.exception.BadRequestException(
                    "Trạng thái đơn mượn không hợp lệ"
            );
        }
    }

    private String createReferenceCode() {
        return "Group2-" + UUID.randomUUID().toString().replace("-", "")
                .substring(0, 12).toUpperCase();
    }
}
