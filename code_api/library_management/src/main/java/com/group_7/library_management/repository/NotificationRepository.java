package com.group_7.library_management.repository;

import com.group_7.library_management.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    boolean existsByNotificationKey(String notificationKey);

    List<Notification> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    Optional<Notification> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);

    List<Notification> findByUserIdAndReadAtIsNullAndDeletedAtIsNull(Long userId);

    long countByUserIdAndReadAtIsNullAndDeletedAtIsNull(Long userId);
}
