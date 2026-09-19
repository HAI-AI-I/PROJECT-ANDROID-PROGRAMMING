package com.group_7.library_management.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "favorite_books",
    primaryKeys = ["userId", "bookId"]
)
data class FavoriteBookEntity(
    val userId: String,
    val bookId: String,
    val isbn: String?,
    val title: String,
    val author: String,
    val category: String,
    val publisher: String?,
    val publishYear: Int?,
    val totalCopies: Int,
    val coverImageUrl: String?,
    val description: String?,
    val borrowFee: Long,
    val availableCopies: Int,
    val rating: Double,
    val createdAt: Long,
    val ratingCount: Int,
    val popularityScore: Long,
    val favoritedAt: Long
)
