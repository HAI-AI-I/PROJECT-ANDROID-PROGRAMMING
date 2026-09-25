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
    val paymentCode: String?,
    val paymentStatus: String,
    val paymentMethod: String?,
    val paidAt: String?,
    val depositRefunded: Boolean,
    val remainingRefundAmount: Long
)

data class BorrowPaymentResponseDto(
    val orderId: Long,
    val referenceCode: String,
    val paymentCode: String,
    val amount: Long,
    val paidAmount: Long,
    val paymentStatus: String,
    val bankCode: String,
    val accountNumber: String,
    val accountName: String,
    val qrUrl: String,
    val paidAt: String?
)

data class CurrentBorrowOrderResponseDto(
    val order: BorrowOrderResponseDto?
)

data class CancelBorrowOrderResponseDto(
    val order: BorrowOrderResponseDto,
    val remainingCancellations: Int
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
