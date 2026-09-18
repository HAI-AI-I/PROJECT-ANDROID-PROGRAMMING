package com.group_7.library_management.service;

import com.group_7.library_management.dto.BookAvailabilitySubscriptionResponse;
import com.group_7.library_management.entity.Book;
import com.group_7.library_management.entity.BookAvailabilitySubscription;
import com.group_7.library_management.entity.Notification;
import com.group_7.library_management.entity.NotificationType;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.exception.ResourceNotFoundException;
import com.group_7.library_management.repository.BookAvailabilitySubscriptionRepository;
import com.group_7.library_management.repository.BookCopyRepository;
import com.group_7.library_management.repository.BookRepository;
import com.group_7.library_management.repository.NotificationRepository;
import com.group_7.library_management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookAvailabilitySubscriptionService {
    private final BookAvailabilitySubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final NotificationRepository notificationRepository;
    private final BookCopyRepository bookCopyRepository;

    public BookAvailabilitySubscriptionService(
            BookAvailabilitySubscriptionRepository subscriptionRepository,
            UserRepository userRepository,
            BookRepository bookRepository,
            NotificationRepository notificationRepository,
            BookCopyRepository bookCopyRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.notificationRepository = notificationRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    @Transactional(readOnly = true)
    public BookAvailabilitySubscriptionResponse getStatus(Long userId, Long bookId) {
        requireBook(bookId);
        return response(
                bookId,
                subscriptionRepository.findByUserIdAndBookId(userId, bookId).isPresent()
        );
    }

    @Transactional
    public BookAvailabilitySubscriptionResponse subscribe(Long userId, Long bookId) {
        if (subscriptionRepository.findByUserIdAndBookId(userId, bookId).isEmpty()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
            Book book = requireBook(bookId);
            long availableQuantity = bookCopyRepository.countByBookIdAndStatus(
                    bookId,
                    com.group_7.library_management.entity.BookCopyStatus.AVAILABLE
            );
            subscriptionRepository.save(
                    new BookAvailabilitySubscription(user, book, availableQuantity)
            );
        }
        return response(bookId, true);
    }

    @Transactional
    public BookAvailabilitySubscriptionResponse unsubscribe(Long userId, Long bookId) {
        subscriptionRepository.findByUserIdAndBookId(userId, bookId)
                .ifPresent(subscriptionRepository::delete);
        return response(bookId, false);
    }

    @Transactional
    public void notifyAvailabilityChanged(Book book, long previousAvailable, long currentAvailable) {
        AvailabilityEvent event = resolveEvent(previousAvailable, currentAvailable);
        List<BookAvailabilitySubscription> subscriptions =
                subscriptionRepository.findAllByBookId(book.getId());
        if (event != null) {
            notificationRepository.saveAll(
                    subscriptions.stream()
                            .map(subscription -> createNotification(
                                    subscription,
                                    book,
                                    event,
                                    currentAvailable
                            ))
                            .toList()
            );
        }
        subscriptions.forEach(subscription ->
                subscription.setLastAvailableQuantity(currentAvailable));
    }

    @Scheduled(fixedDelayString = "${app.book-availability-check-interval-ms:15000}")
    @Transactional
    public void checkAvailabilityChanges() {
        for (BookAvailabilitySubscription subscription : subscriptionRepository.findAll()) {
            long currentAvailable = bookCopyRepository.countByBookIdAndStatus(
                    subscription.getBook().getId(),
                    com.group_7.library_management.entity.BookCopyStatus.AVAILABLE
            );
            AvailabilityEvent event = resolveEvent(
                    subscription.getLastAvailableQuantity(),
                    currentAvailable
            );
            if (event != null) {
                notificationRepository.save(createNotification(
                        subscription,
                        subscription.getBook(),
                        event,
                        currentAvailable
                ));
            }
            subscription.setLastAvailableQuantity(currentAvailable);
        }
    }

    private Notification createNotification(
            BookAvailabilitySubscription subscription,
            Book book,
            AvailabilityEvent event,
            long currentAvailable
    ) {
        String title;
        String message;
        if (event == AvailabilityEvent.AVAILABLE_AGAIN) {
            title = "Sách đã có lại";
            message = "Sách \"" + book.getTitle() + "\" đã có thể mượn trở lại ("
                    + currentAvailable + " bản đang có sẵn).";
        } else {
            title = "Sách chỉ còn 1 bản";
            message = "Sách \"" + book.getTitle() + "\" chỉ còn 1 bản có thể mượn.";
        }

        String notificationKey = "availability:"
                + event.name() + ":"
                + book.getId() + ":"
                + subscription.getUser().getId() + ":"
                + UUID.randomUUID();
        return new Notification(
                notificationKey,
                subscription.getUser(),
                book,
                title,
                message,
                NotificationType.BOOK
        );
    }

    private AvailabilityEvent resolveEvent(long previousAvailable, long currentAvailable) {
        if (previousAvailable == 0 && currentAvailable > 0) {
            return AvailabilityEvent.AVAILABLE_AGAIN;
        }
        if (previousAvailable > 1 && currentAvailable == 1) {
            return AvailabilityEvent.LAST_COPY;
        }
        return null;
    }

    private Book requireBook(Long bookId) {
        return bookRepository.findByIdAndActiveTrue(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));
    }

    private BookAvailabilitySubscriptionResponse response(Long bookId, boolean subscribed) {
        return new BookAvailabilitySubscriptionResponse(bookId, subscribed);
    }

    private enum AvailabilityEvent {
        LAST_COPY,
        AVAILABLE_AGAIN
    }
}
