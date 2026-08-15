package com.group_7.library_management.models

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val imageUrl: String? = null,
    val isAvailable: Boolean = true,
    val remainingDays: Int? = null, // Used for borrowed books
    val isOverdue: Boolean = false
)
