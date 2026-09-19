package com.group_7.library_management.models

data class UserBorrowSummary(
    val pendingPickupCount: Long = 0,
    val borrowingCount: Long = 0,
    val dueSoonCount: Long = 0,
    val overdueCount: Long = 0,
    val favoriteCount: Long = 0,
    val returnedCount: Long = 0,
    val allBorrowCount: Long = 0
)
