package com.group_7.library_management.data.remote.dto

data class BookResponseDto(
    val id: Long,
    val isbn: String?,
    val title: String,
    val author: String,
    val category: String,
    val publisher: String?,
    val publishYear: Int?,
    val quantity: Long,
    val cover: String?,
    val description: String?,
    val borrowFee: Long,
    val availableQuantity: Long,
    val rating: Double,
    val ratingCount: Int,
    val createdAt: String
)

data class PopularBookResponseDto(
    val book: BookResponseDto,
    val borrowCount: Long,
    val favoriteCount: Long,
    val notificationClickCount: Long,
    val popularityScore: Long
)

data class BookAvailabilitySubscriptionResponseDto(
    val bookId: Long,
    val subscribed: Boolean
)

data class BookReviewRequestDto(
    val rating: Int,
    val comment: String?
)

data class BookReviewResponseDto(
    val id: Long,
    val userId: Long,
    val userName: String,
    val rating: Int,
    val comment: String?,
    val createdAt: String,
    val updatedAt: String
)

data class MyBookReviewResponseDto(
    val reviewed: Boolean,
    val review: BookReviewResponseDto?
)

data class PagedResponseDto<T>(
    val items: List<T>,
    val total: Long,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)
