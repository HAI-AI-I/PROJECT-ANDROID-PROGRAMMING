package com.group_7.library_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_reset_verifications", indexes = {
        @Index(name = "idx_password_reset_user_id", columnList = "user_id")
})
public class PasswordResetVerification {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_channel", nullable = false, length = 10)
    private VerificationChannel channel;

    @Column(name = "code_hash", nullable = false, length = 100)
    private String codeHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    @Column(nullable = false)
    private boolean verified;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected PasswordResetVerification() {
    }

    public PasswordResetVerification(
            User user,
            VerificationChannel channel,
            String codeHash,
            Instant expiresAt
    ) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.channel = channel;
        this.codeHash = codeHash;
        this.expiresAt = expiresAt;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public User getUser() { return user; }
    public VerificationChannel getChannel() { return channel; }
    public String getCodeHash() { return codeHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public int getFailedAttempts() { return failedAttempts; }
    public boolean isVerified() { return verified; }

    public void renewCode(String newCodeHash, Instant newExpiresAt) {
        codeHash = newCodeHash;
        expiresAt = newExpiresAt;
        failedAttempts = 0;
        verified = false;
    }

    public void recordFailedAttempt() {
        failedAttempts++;
    }

    public void markVerified(Instant resetExpiresAt) {
        verified = true;
        expiresAt = resetExpiresAt;
        failedAttempts = 0;
    }
}
