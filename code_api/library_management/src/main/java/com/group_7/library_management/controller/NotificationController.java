package com.group_7.library_management.controller;

import com.group_7.library_management.dto.NotificationReadAllResponse;
import com.group_7.library_management.dto.NotificationResponse;
import com.group_7.library_management.dto.NotificationUnreadCountResponse;
import com.group_7.library_management.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationResponse> getNotifications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return notificationService.getNotifications(
                userId,
                Math.max(page, 0),
                normalizeLimit(limit)
        );
    }

    @GetMapping("/unread-count")
    public NotificationUnreadCountResponse getUnreadCount(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return new NotificationUnreadCountResponse(
                notificationService.getUnreadCount(userId)
        );
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationResponse markAsRead(
            Authentication authentication,
            @PathVariable Long notificationId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return notificationService.markAsRead(userId, notificationId);
    }

    @PatchMapping("/read-all")
    public NotificationReadAllResponse markAllAsRead(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        int updatedCount = notificationService.markAllAsRead(userId);
        return new NotificationReadAllResponse(updatedCount);
    }

    @PatchMapping("/{notificationId}/click")
    public NotificationResponse markAsClicked(
            Authentication authentication,
            @PathVariable Long notificationId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return notificationService.markAsClicked(userId, notificationId);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            Authentication authentication,
            @PathVariable Long notificationId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        notificationService.deleteNotification(userId, notificationId);
        return ResponseEntity.noContent().build();
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) return DEFAULT_LIMIT;
        return Math.min(limit, MAX_LIMIT);
    }
}
