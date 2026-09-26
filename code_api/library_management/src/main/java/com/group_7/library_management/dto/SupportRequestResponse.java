package com.group_7.library_management.dto;

import com.group_7.library_management.entity.SupportRequest;
import com.group_7.library_management.entity.SupportRequestStatus;

import java.time.Instant;

public record SupportRequestResponse(
        Long id,
        Long userId,
        String userFullName,
        String userEmail,
        Long bookId,
        String bookTitle,
        String subject,
        String message,
        SupportRequestStatus status,
        String adminReply,
        Instant repliedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static SupportRequestResponse from(SupportRequest request) {
        return new SupportRequestResponse(
                request.getId(),
                request.getUser().getId(),
                request.getUser().getFullName(),
                request.getUser().getEmail(),
                request.getBook() == null ? null : request.getBook().getId(),
                request.getBook() == null ? null : request.getBook().getTitle(),
                request.getSubject(),
                request.getMessage(),
                request.getStatus(),
                request.getAdminReply(),
                request.getRepliedAt(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}
