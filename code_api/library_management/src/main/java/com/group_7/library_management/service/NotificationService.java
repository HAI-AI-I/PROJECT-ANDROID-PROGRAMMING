package com.group_7.library_management.service;

import com.group_7.library_management.dto.NotificationResponse;
import com.group_7.library_management.entity.Notification;
import com.group_7.library_management.exception.ResourceNotFoundException;
import com.group_7.library_management.repository.NotificationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId, int page, int limit) {
        return notificationRepository
                .findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                        userId,
                        PageRequest.of(page, limit)
                )
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository
                .countByUserIdAndReadAtIsNullAndDeletedAtIsNull(userId);
    }

    @Transactional
    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository
                .findByIdAndUserIdAndDeletedAtIsNull(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));

        notification.markAsRead();
        return NotificationResponse.from(notification);
    }

    @Transactional
    public int markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndReadAtIsNullAndDeletedAtIsNull(userId);

        unreadNotifications.forEach(Notification::markAsRead);
        return unreadNotifications.size();
    }

    @Transactional
    public NotificationResponse markAsClicked(Long userId, Long notificationId) {
        Notification notification = findOwnedNotification(userId, notificationId);
        notification.markAsClicked();
        return NotificationResponse.from(notification);
    }

    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        Notification notification = findOwnedNotification(userId, notificationId);
        notification.softDelete();
    }

    private Notification findOwnedNotification(Long userId, Long notificationId) {
        return notificationRepository
                .findByIdAndUserIdAndDeletedAtIsNull(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));
    }
}
