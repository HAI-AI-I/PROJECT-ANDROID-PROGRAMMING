package com.group_7.library_management.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_summaries")
data class HomeSummaryEntity(
    @PrimaryKey
    val userId: Long,
    val pendingPickupCount: Long,
    val borrowingCount: Long,
    val dueSoonCount: Long,
    val overdueCount: Long,
    val favoriteCount: Long,
    val returnedCount: Long,
    val allBorrowCount: Long,
    val updatedAt: Long = System.currentTimeMillis()
)
