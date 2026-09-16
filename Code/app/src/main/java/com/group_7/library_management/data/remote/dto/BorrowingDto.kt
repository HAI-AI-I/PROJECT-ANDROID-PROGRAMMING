package com.group_7.library_management.data.remote.dto

data class CreateBorrowingRequestDto(
    val readerId: Long,
    val bookId: Long,
    val borrowDate: String,
    val dueDate: String,
    val note: String? = null
)

data class BorrowingResponseDto(
    val id: Long,
    val readerId: Long,
    val bookId: Long,
    val borrowDate: String,
    val dueDate: String,
    val status: String
)
