package com.group_7.library_management.models

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val category: String,
    val isbn: String? = null,
    val publisher: String? = null,
    val publishYear: Int? = null,
    val totalCopies: Int = 0,
    val coverImageUrl: String? = null,
    val description: String? = null,
    val borrowFee: Long = 0L,
    val availableCopies: Int = 0,
    val rating: Double = 0.0,
    val createdAt: Long = 0,
    val ratingCount: Int = 0,
    val popularityScore: Long = 0,
)
