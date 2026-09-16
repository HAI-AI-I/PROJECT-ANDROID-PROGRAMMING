package com.group_7.library_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "registration_verifications", indexes = {
        @Index(name = "idx_registration_verifications_email", columnList = "email"),
        @Index(name = "idx_registration_verifications_phone", columnList = "phone")
})
public class RegistrationVerification {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "code_hash", nullable = false, length = 100)
    private String codeHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_channel", length = 10)
    private VerificationChannel verificationChannel;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected RegistrationVerification() {
    }

    public RegistrationVerification(
            String fullName,
            String email,
            String phone,
            String passwordHash,
            String codeHash,
            Instant expiresAt,
            VerificationChannel verificationChannel
    ) {
        this.id = UUID.randomUUID().toString();
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.codeHash = codeHash;
        this.expiresAt = expiresAt;
        this.verificationChannel = verificationChannel;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPasswordHash() { return passwordHash; }
    public String getCodeHash() { return codeHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public int getFailedAttempts() { return failedAttempts; }
    public VerificationChannel getVerificationChannel() {
        return verificationChannel == null ? VerificationChannel.EMAIL : verificationChannel;
    }

    public void renewCode(String codeHash, Instant expiresAt) {
        this.codeHash = codeHash;
        this.expiresAt = expiresAt;
        this.failedAttempts = 0;
    }

    public void recordFailedAttempt() {
        failedAttempts++;
    }
}
