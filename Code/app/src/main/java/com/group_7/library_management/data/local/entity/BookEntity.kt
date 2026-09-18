package com.group_7.library_management.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: String,
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
    val popularityScore: Long
)
