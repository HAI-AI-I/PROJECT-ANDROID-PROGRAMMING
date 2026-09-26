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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "support_requests", indexes = {
        @Index(name = "idx_support_request_user_time", columnList = "user_id,created_at"),
        @Index(name = "idx_support_request_status_time", columnList = "status,created_at")
})
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(nullable = false, length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SupportRequestStatus status = SupportRequestStatus.OPEN;

    @Column(name = "admin_reply", length = 2000)
    private String adminReply;

    @Column(name = "replied_at")
    private Instant repliedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected SupportRequest() {
    }

    public SupportRequest(User user, Book book, String subject, String message) {
        this.user = user;
        this.book = book;
        this.subject = subject;
        this.message = message;
    }

    public SupportRequest(User user, String subject, String message) {
        this(user, null, subject, message);
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Book getBook() { return book; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
    public SupportRequestStatus getStatus() { return status; }
    public String getAdminReply() { return adminReply; }
    public Instant getRepliedAt() { return repliedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void reply(String reply) {
        adminReply = reply;
        repliedAt = Instant.now();
        status = SupportRequestStatus.IN_PROGRESS;
    }

    public void resolve() {
        status = SupportRequestStatus.RESOLVED;
    }

    public void close() {
        status = SupportRequestStatus.CLOSED;
    }
}
