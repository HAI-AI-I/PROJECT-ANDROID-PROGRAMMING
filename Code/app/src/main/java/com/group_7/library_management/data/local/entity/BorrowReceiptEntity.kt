package com.group_7.library_management.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "borrow_receipts")
data class BorrowReceiptEntity(
    @PrimaryKey(autoGenerate = true)
    val receiptId: Long = 0,
    val userId: Long,
    val bookId: String,
    val borrowDate: String, // định dạng dd/MM/yyyy
    val dueDate: String,    // định dạng dd/MM/yyyy
    val returnDate: String? = null, // định dạng dd/MM/yyyy, null nếu chưa trả
    val status: String,     // "BORROWED", "RETURNED", "OVERDUE"
    val fineAmount: Long = 0
)
