package com.group_7.library_management.data.repository

import com.group_7.library_management.data.remote.api.BorrowApi
import com.group_7.library_management.data.remote.dto.BorrowOrderResponseDto
import com.group_7.library_management.data.remote.dto.CreateBorrowOrderRequestDto
import com.group_7.library_management.models.BorrowOrder
import com.group_7.library_management.models.UserBorrowSummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BorrowRepository @Inject constructor(
    private val borrowApi: BorrowApi
) {
    suspend fun createBorrowOrder(bookId: Long, borrowDays: Int): BorrowOrder {
        return borrowApi.createBorrowOrder(
            CreateBorrowOrderRequestDto(bookId, borrowDays)
        ).toModel()
    }

    suspend fun getBorrowOrder(orderId: Long): BorrowOrder {
        return borrowApi.getBorrowOrder(orderId).toModel()
    }

    suspend fun getCurrentBorrowOrder(bookId: Long): BorrowOrder? {
        return borrowApi.getCurrentBorrowOrder(bookId).order?.toModel()
    }

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

    private fun BorrowOrderResponseDto.toModel() = BorrowOrder(
        id = id,
        referenceCode = referenceCode,
        status = status,
        bookId = bookId,
        bookTitle = bookTitle,
        bookAuthor = bookAuthor,
        coverImageUrl = coverImageUrl,
        copyBarcode = copyBarcode,
        borrowerId = borrowerId,
        borrowerName = borrowerName,
        pickupLocation = pickupLocation,
        borrowDays = borrowDays,
        requestedAt = requestedAt,
        borrowedAt = borrowedAt,
        dueAt = dueAt,
        returnedAt = returnedAt,
        borrowFee = borrowFee,
        depositAmount = depositAmount,
        totalAmount = totalAmount
    )
}
