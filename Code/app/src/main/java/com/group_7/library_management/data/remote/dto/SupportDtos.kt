package com.group_7.library_management.data.remote.dto

data class CreateSupportRequestDto(
    val bookId: Long?,
    val subject: String,
    val message: String
)

data class SupportRequestResponseDto(
    val id: Long,
    val userId: Long,
    val userFullName: String,
    val userEmail: String,
    val bookId: Long?,
    val bookTitle: String?,
    val subject: String,
    val message: String,
    val status: String,
    val adminReply: String?,
    val repliedAt: String?,
    val createdAt: String,
    val updatedAt: String
)
