package com.group_7.library_management.dto;

import com.group_7.library_management.entity.Notification;
import com.group_7.library_management.entity.NotificationActionType;
import com.group_7.library_management.entity.NotificationType;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        NotificationType type,
        Long bookId,
        NotificationActionType actionType,
        Long targetId,
        boolean isRead,
        boolean isClicked,
        Instant createdAt,
        Instant readAt,
        Instant clickedAt
) {
    public static NotificationResponse from(Notification notification) {
        Long bookId = notification.getBook() == null
                ? null
                : notification.getBook().getId();

        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                bookId,
                notification.getActionType(),
                notification.getTargetId(),
                notification.isRead(),
                notification.getClickedAt() != null,
                notification.getCreatedAt(),
                notification.getReadAt(),
                notification.getClickedAt()
        );
    }
}
