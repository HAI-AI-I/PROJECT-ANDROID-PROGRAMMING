package com.group_7.library_management.data.remote.dto

data class CreateBorrowOrderRequestDto(
    val bookId: Long,
    val borrowDays: Int
)

data class BorrowOrderResponseDto(
    val id: Long,
    val referenceCode: String,
    val status: String,
    val bookId: Long,
    val bookTitle: String,
    val bookAuthor: String,
    val coverImageUrl: String?,
    val copyBarcode: String,
    val borrowerId: Long,
    val borrowerName: String,
    val pickupLocation: String,
    val borrowDays: Int,
    val requestedAt: String,
    val borrowedAt: String?,
    val dueAt: String,
    val returnedAt: String?,
    val borrowFee: Long,
    val depositAmount: Long,
    val totalAmount: Long,
    val paidAmount: Long,
    val depositRefunded: Boolean,
    val remainingRefundAmount: Long
)

data class CurrentBorrowOrderResponseDto(
    val order: BorrowOrderResponseDto?
)

data class HomeSummaryResponseDto(
    val pendingPickupCount: Long,
    val borrowingCount: Long,
    val dueSoonCount: Long,
    val overdueCount: Long,
    val favoriteCount: Long,
    val returnedCount: Long,
    val allBorrowCount: Long
)
