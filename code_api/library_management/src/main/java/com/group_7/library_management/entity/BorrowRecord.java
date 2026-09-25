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
@Table(name = "borrow_records", indexes = {
        @Index(name = "idx_borrow_user_status", columnList = "user_id,status"),
        @Index(name = "idx_borrow_copy_time", columnList = "book_copy_id,borrowed_at"),
        @Index(name = "idx_borrow_reference", columnList = "reference_code", unique = true)
})
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_code", nullable = false, length = 80, unique = true)
    private String referenceCode;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BorrowStatus status;

    @Column(name = "borrowed_at")
    private Instant borrowedAt;

    @Column(name = "due_at")
    private Instant dueAt;

    @Column(name = "returned_at")
    private Instant returnedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "deposit_refunded_at")
    private Instant depositRefundedAt;

    @Column(name = "borrow_days", nullable = false)
    private int borrowDays;

    @Column(name = "pickup_location", nullable = false, length = 150)
    private String pickupLocation = "Thư viện UTH";

    @Column(name = "borrow_fee", nullable = false)
    private long borrowFee;

    @Column(name = "deposit_amount", nullable = false)
    private long depositAmount;

    @Column(name = "total_amount", nullable = false)
    private long totalAmount;

    @Column(name = "payment_code", unique = true, length = 40)
    private String paymentCode;

    @Column(name = "paid_amount", nullable = false)
    private long paidAmount;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected BorrowRecord() {}

    public BorrowRecord(String referenceCode, User user, BookCopy bookCopy, Instant borrowedAt) {
        this.referenceCode = referenceCode;
        this.user = user;
        this.bookCopy = bookCopy;
        this.borrowedAt = borrowedAt;
        this.status = BorrowStatus.BORROWED;
    }

    public BorrowRecord(
            String referenceCode,
            User user,
            BookCopy bookCopy,
            int borrowDays,
            Instant dueAt,
            String pickupLocation,
            long borrowFee,
            long depositAmount
    ) {
        this.referenceCode = referenceCode;
        this.user = user;
        this.bookCopy = bookCopy;
        this.status = BorrowStatus.PENDING_PAYMENT;
        this.borrowDays = borrowDays;
        this.dueAt = dueAt;
        this.pickupLocation = pickupLocation;
        this.borrowFee = borrowFee;
        this.depositAmount = depositAmount;
        this.totalAmount = Math.addExact(borrowFee, depositAmount);
        this.paymentCode = "UTH" + referenceCode.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public String getReferenceCode() { return referenceCode; }
    public User getUser() { return user; }
    public BookCopy getBookCopy() { return bookCopy; }
    public BorrowStatus getStatus() { return status; }
    public void setStatus(BorrowStatus status) { this.status = status; }
    public Instant getBorrowedAt() { return borrowedAt; }
    public void setBorrowedAt(Instant borrowedAt) { this.borrowedAt = borrowedAt; }
    public Instant getDueAt() { return dueAt; }
    public void setDueAt(Instant dueAt) { this.dueAt = dueAt; }
    public Instant getReturnedAt() { return returnedAt; }
    public void setReturnedAt(Instant returnedAt) { this.returnedAt = returnedAt; }
    public Instant getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(Instant cancelledAt) { this.cancelledAt = cancelledAt; }
    public Instant getDepositRefundedAt() { return depositRefundedAt; }
    public void setDepositRefundedAt(Instant depositRefundedAt) { this.depositRefundedAt = depositRefundedAt; }
    public int getBorrowDays() { return borrowDays; }
    public String getPickupLocation() { return pickupLocation; }
    public long getBorrowFee() { return borrowFee; }
    public long getDepositAmount() { return depositAmount; }
    public long getTotalAmount() { return totalAmount; }
    public String getPaymentCode() { return paymentCode; }
    public void setPaymentCode(String paymentCode) { this.paymentCode = paymentCode; }
    public long getPaidAmount() { return paidAmount; }
    public Instant getPaidAt() { return paidAt; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void markPaid(long amount, Instant paidAt, PaymentMethod paymentMethod) {
        this.paidAmount = amount;
        this.paidAt = paidAt;
        this.paymentMethod = paymentMethod;
    }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
