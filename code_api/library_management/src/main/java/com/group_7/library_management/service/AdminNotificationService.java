package com.group_7.library_management.service;

import com.group_7.library_management.dto.AdminBroadcastNotificationRequest;
import com.group_7.library_management.dto.AdminBroadcastNotificationResponse;
import com.group_7.library_management.entity.Notification;
import com.group_7.library_management.entity.NotificationActionType;
import com.group_7.library_management.entity.NotificationType;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.entity.UserRole;
import com.group_7.library_management.exception.BadRequestException;
import com.group_7.library_management.repository.NotificationRepository;
import com.group_7.library_management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AdminNotificationService {

    private static final Set<NotificationType> BROADCAST_TYPES = Set.of(
            NotificationType.INFO,
            NotificationType.WARNING,
            NotificationType.SUCCESS,
            NotificationType.ERROR
    );

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public AdminNotificationService(
            UserRepository userRepository,
            NotificationRepository notificationRepository
    ) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public AdminBroadcastNotificationResponse broadcast(AdminBroadcastNotificationRequest request) {
        if (!BROADCAST_TYPES.contains(request.type())) {
            throw new BadRequestException("Loại thông báo phát hành không hợp lệ");
        }

        List<User> recipients = userRepository.findAllByRoleAndActiveTrue(UserRole.USER);
        String broadcastId = UUID.randomUUID().toString();
        String title = request.title().strip();
        String message = request.message().strip();

        List<Notification> notifications = recipients.stream()
                .map(user -> new Notification(
                        "broadcast:" + broadcastId + ":user:" + user.getId(),
                        user,
                        null,
                        title,
                        message,
                        request.type(),
                        NotificationActionType.NONE,
                        null
                ))
                .toList();
        notificationRepository.saveAll(notifications);
        return new AdminBroadcastNotificationResponse(notifications.size());
    }
}
