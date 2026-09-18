package com.group_7.library_management.models

data class BookReview(
    val id: Long,
    val userId: Long,
    val userName: String,
    val rating: Int,
    val comment: String?,
    val createdAt: String,
    val updatedAt: String
)
