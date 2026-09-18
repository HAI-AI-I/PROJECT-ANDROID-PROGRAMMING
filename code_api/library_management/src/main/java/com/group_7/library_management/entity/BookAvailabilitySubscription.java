package com.group_7.library_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
        name = "book_availability_subscriptions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_book_availability_subscription_user_book",
                columnNames = {"user_id", "book_id"}
        ),
        indexes = @Index(name = "idx_book_availability_subscription_book", columnList = "book_id")
)
public class BookAvailabilitySubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_available_quantity", nullable = false)
    private long lastAvailableQuantity;

    protected BookAvailabilitySubscription() {}

    public BookAvailabilitySubscription(User user, Book book, long lastAvailableQuantity) {
        this.user = user;
        this.book = book;
        this.lastAvailableQuantity = lastAvailableQuantity;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Book getBook() { return book; }
    public Instant getCreatedAt() { return createdAt; }
    public long getLastAvailableQuantity() { return lastAvailableQuantity; }
    public void setLastAvailableQuantity(long lastAvailableQuantity) {
        this.lastAvailableQuantity = lastAvailableQuantity;
    }
}
