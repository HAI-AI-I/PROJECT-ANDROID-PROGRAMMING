package com.group_7.library_management.dto;

public record PopularBookResponse(
        BookResponse book,
        long borrowCount,
        long favoriteCount,
        long notificationClickCount,
        long popularityScore
) {
}
