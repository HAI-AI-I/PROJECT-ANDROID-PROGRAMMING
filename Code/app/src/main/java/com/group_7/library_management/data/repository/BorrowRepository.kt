package com.group_7.library_management.data.repository

import com.group_7.library_management.models.UserBorrowSummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BorrowRepository @Inject constructor() {
    fun getBorrowSummary(userId:String="default_user"): Flow<UserBorrowSummary> = flow {
        delay(500)
        emit(
            UserBorrowSummary(
                pendingPickupCount = 1,
                borrowingCount = 3,
                dueSoonCount = 1,
                overdueCount = 0,
                favoriteCount = 5,
                returnedCount = 10
            )
        )
    }
}