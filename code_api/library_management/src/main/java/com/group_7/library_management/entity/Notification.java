package com.group_7.library_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_user_time", columnList = "user_id,created_at"),
        @Index(name = "idx_notification_book_click", columnList = "book_id,clicked_at"),
        @Index(name = "idx_notification_key", columnList = "notification_key", unique = true)
})
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_key", nullable = false, length = 100, unique = true)
    private String notificationKey;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", length = 40)
    private NotificationActionType actionType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "clicked_at")
    private Instant clickedAt;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Notification() {}

    public Notification(
            String notificationKey, User user, Book book,
            String title, String message, NotificationType type
    ) {
        this.notificationKey = notificationKey;
        this.user = user;
        this.book = book;
        this.title = title;
        this.message = message;
        this.type = type;
        this.actionType = book == null
                ? NotificationActionType.NONE
                : NotificationActionType.BOOK_DETAIL;
        this.targetId = book == null ? null : book.getId();
    }

    public Notification(
            String notificationKey, User user, Book book,
            String title, String message, NotificationType type,
            NotificationActionType actionType, Long targetId
    ) {
        this.notificationKey = notificationKey;
        this.user = user;
        this.book = book;
        this.title = title;
        this.message = message;
        this.type = type;
        this.actionType = actionType;
        this.targetId = targetId;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getNotificationKey() { return notificationKey; }
    public User getUser() { return user; }
    public Book getBook() { return book; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public NotificationType getType() { return type; }
    public NotificationActionType getActionType() {
        if (actionType != null) return actionType;
        return book == null ? NotificationActionType.NONE : NotificationActionType.BOOK_DETAIL;
    }
    public Long getTargetId() {
        if (targetId != null) return targetId;
        return book == null ? null : book.getId();
    }
    public Instant getClickedAt() { return clickedAt; }
    public void setClickedAt(Instant clickedAt) { this.clickedAt = clickedAt; }
    public Instant getReadAt() { return readAt; }
    public Instant getDeletedAt() { return deletedAt; }
    public Instant getCreatedAt() { return createdAt; }

    public boolean isRead() {
        return readAt != null;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void markAsRead() {
        if (readAt == null) readAt = Instant.now();
    }

    public void markAsClicked() {
        Instant now = Instant.now();
        if (clickedAt == null) clickedAt = now;
        if (readAt == null) readAt = now;
    }

    public void softDelete() {
        if (deletedAt == null) deletedAt = Instant.now();
    }
}
