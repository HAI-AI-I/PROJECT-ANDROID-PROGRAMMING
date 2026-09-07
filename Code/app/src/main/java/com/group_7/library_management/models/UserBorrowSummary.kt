package com.group_7.library_management.models

data class UserBorrowSummary(
    val pendingPickupCount: Int = 0,
    val borrowingCount: Int = 0,
    val dueSoonCount: Int = 0,
    val overdueCount: Int = 0,
    val favoriteCount: Int = 0,
    val returnedCount: Int = 0
)