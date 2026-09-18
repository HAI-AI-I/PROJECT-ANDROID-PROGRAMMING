package com.group_7.library_management.dto;

public record MyBookReviewResponse(
        boolean reviewed,
        BookReviewResponse review
) {
    public static MyBookReviewResponse notReviewed() {
        return new MyBookReviewResponse(false, null);
    }

    public static MyBookReviewResponse reviewed(BookReviewResponse review) {
        return new MyBookReviewResponse(true, review);
    }
}
