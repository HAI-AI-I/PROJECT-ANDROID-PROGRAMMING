package com.group_7.library_management.dto;

import com.group_7.library_management.entity.User;

import java.time.Instant;

public record AdminReaderResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        boolean active,
        long activeBorrowingCount,
        Instant createdAt
) {
    public static AdminReaderResponse from(User user, long activeBorrowingCount) {
        return new AdminReaderResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.isActive(),
                activeBorrowingCount,
                user.getCreatedAt()
        );
    }
}
