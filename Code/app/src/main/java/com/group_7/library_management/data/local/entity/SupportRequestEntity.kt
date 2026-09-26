package com.group_7.library_management.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "support_requests")
data class SupportRequestEntity(
    @PrimaryKey
    val id: Long,
    val userId: Long,
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
