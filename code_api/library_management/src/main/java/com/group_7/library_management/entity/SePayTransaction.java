package com.group_7.library_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "sepay_transactions", indexes = {
        @Index(name = "idx_sepay_transaction_id", columnList = "sepay_transaction_id", unique = true),
        @Index(name = "idx_sepay_borrow_record", columnList = "borrow_record_id")
})
public class SePayTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sepay_transaction_id", nullable = false, unique = true)
    private Long sePayTransactionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "borrow_record_id", nullable = false)
    private BorrowRecord borrowRecord;

    @Column(nullable = false, length = 50)
    private String gateway;

    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;

    @Column(name = "transfer_amount", nullable = false)
    private long transferAmount;

    @Column(name = "transaction_date", length = 40)
    private String transactionDate;

    @Column(name = "bank_reference_code", length = 100)
    private String bankReferenceCode;

    @Column(name = "raw_payload", nullable = false, columnDefinition = "LONGTEXT")
    private String rawPayload;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected SePayTransaction() {
    }

    public SePayTransaction(
            Long sePayTransactionId,
            BorrowRecord borrowRecord,
            String gateway,
            String accountNumber,
            long transferAmount,
            String transactionDate,
            String bankReferenceCode,
            String rawPayload
    ) {
        this.sePayTransactionId = sePayTransactionId;
        this.borrowRecord = borrowRecord;
        this.gateway = gateway;
        this.accountNumber = accountNumber;
        this.transferAmount = transferAmount;
        this.transactionDate = transactionDate;
        this.bankReferenceCode = bankReferenceCode;
        this.rawPayload = rawPayload;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
