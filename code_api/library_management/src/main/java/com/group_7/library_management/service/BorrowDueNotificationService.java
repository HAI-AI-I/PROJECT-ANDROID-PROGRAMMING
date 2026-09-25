package com.group_7.library_management.service;

import com.group_7.library_management.entity.BorrowRecord;
import com.group_7.library_management.entity.BorrowStatus;
import com.group_7.library_management.entity.Notification;
import com.group_7.library_management.entity.NotificationActionType;
import com.group_7.library_management.entity.NotificationType;
import com.group_7.library_management.repository.BorrowRecordRepository;
import com.group_7.library_management.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BorrowDueNotificationService {
    private static final long DUE_SOON_HOURS = 24L;

    private final BorrowRecordRepository borrowRecordRepository;
    private final NotificationRepository notificationRepository;

    public BorrowDueNotificationService(
            BorrowRecordRepository borrowRecordRepository,
            NotificationRepository notificationRepository
    ) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.notificationRepository = notificationRepository;
    }

    @Scheduled(fixedDelayString = "${app.borrow-notification-check-interval-ms:60000}")
    @Transactional
    public void checkDueDates() {
        Instant now = Instant.now();
        notifyDueSoon(now);
        notifyOverdue(now);
    }

    private void notifyDueSoon(Instant now) {
        Instant dueSoonLimit = now.plus(DUE_SOON_HOURS, ChronoUnit.HOURS);
        borrowRecordRepository.findAllByStatusAndDueAtBetween(
                BorrowStatus.BORROWED,
                now,
                dueSoonLimit
        ).forEach(order -> createNotificationIfAbsent(
                "borrow:DUE_SOON:" + order.getId(),
                order,
                "Sắp đến hạn trả sách",
                "Sách \"" + order.getBookCopy().getBook().getTitle()
                        + "\" còn không quá 24 giờ đến hạn trả.",
                NotificationType.WARNING
        ));
    }

    private void notifyOverdue(Instant now) {
        List<BorrowRecord> overdueOrders = borrowRecordRepository
                .findAllByStatusInAndDueAtBefore(
                        List.of(BorrowStatus.BORROWED, BorrowStatus.OVERDUE),
                        now
                );
        overdueOrders.forEach(order -> {
            if (order.getStatus() == BorrowStatus.BORROWED) {
                order.setStatus(BorrowStatus.OVERDUE);
            }
            createNotificationIfAbsent(
                    "borrow:OVERDUE:" + order.getId(),
                    order,
                    "Đơn mượn đã quá hạn",
                    "Sách \"" + order.getBookCopy().getBook().getTitle()
                            + "\" đã quá hạn trả. Vui lòng trả sách sớm nhất có thể.",
                    NotificationType.ERROR
            );
        });
    }

    private void createNotificationIfAbsent(
            String notificationKey,
            BorrowRecord order,
            String title,
            String message,
            NotificationType type
    ) {
        if (notificationRepository.existsByNotificationKey(notificationKey)) {
            return;
        }
        notificationRepository.save(new Notification(
                notificationKey,
                order.getUser(),
                order.getBookCopy().getBook(),
                title,
                message,
                type,
                NotificationActionType.BORROW_ORDER_DETAIL,
                order.getId()
        ));
    }
}
