package com.group_7.library_management.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "borrow_orders",
    primaryKeys = ["userId", "id"]
)
data class BorrowOrderEntity(
    val userId: Long,
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
