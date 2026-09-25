package com.group_7.library_management.models

data class BorrowOrder(
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

data class BorrowPayment(
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
