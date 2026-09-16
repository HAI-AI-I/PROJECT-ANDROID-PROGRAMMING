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
@Table(name = "auth_tokens", indexes = {
        @Index(name = "idx_auth_tokens_token_hash", columnList = "token_hash", unique = true),
        @Index(name = "idx_auth_tokens_user_id", columnList = "user_id")
})
public class AuthToken {

    private static final Instant NO_EXPIRATION = Instant.parse("9999-12-31T23:59:59Z");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public AuthToken() {
    }

    public AuthToken(String tokenHash, User user) {
        this.tokenHash = tokenHash;
        this.user = user;
        this.expiresAt = NO_EXPIRATION;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public User getUser() {
        return user;
    }

}
