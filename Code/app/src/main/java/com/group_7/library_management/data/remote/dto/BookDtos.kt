package com.group_7.library_management.data.remote.dto

data class BookResponseDto(
    val id: Long,
    val title: String,
    val author: String,
    val category: String,
    val cover: String?,
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
