package com.group_7.library_management.dto;

import com.group_7.library_management.entity.BookReview;

import java.time.Instant;

public record BookReviewResponse(
        Long id,
        Long userId,
        String userName,
        int rating,
        String comment,
        Instant createdAt,
        Instant updatedAt
) {
    public static BookReviewResponse from(BookReview review) {
        return new BookReviewResponse(
                review.getId(),
                review.getUser().getId(),
                review.getUser().getFullName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
