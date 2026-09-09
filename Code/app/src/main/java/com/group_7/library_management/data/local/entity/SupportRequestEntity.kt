package com.group_7.library_management.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "support_requests")
data class SupportRequestEntity(
    @PrimaryKey
    val id: String,
    val bookTitle: String,
    val problemType: String,
    val description: String,
    val date: String,
    val status: String,
    val isSynced: Boolean = true
)
