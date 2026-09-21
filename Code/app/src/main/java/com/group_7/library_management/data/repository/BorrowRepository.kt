package com.group_7.library_management.data.repository

import com.group_7.library_management.data.local.dao.HomeSummaryDao
import com.group_7.library_management.data.local.entity.HomeSummaryEntity
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.data.remote.api.BorrowApi
import com.group_7.library_management.data.remote.dto.BorrowOrderResponseDto
import com.group_7.library_management.data.remote.dto.CreateBorrowOrderRequestDto
import com.group_7.library_management.data.remote.dto.HomeSummaryResponseDto
import com.group_7.library_management.models.BorrowOrder
import com.group_7.library_management.models.UserBorrowSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class BorrowRepository @Inject constructor(
    private val borrowApi: BorrowApi,
    private val homeSummaryDao: HomeSummaryDao,
    private val checkLogin: CheckLogin
) {
    data class CancelResult(
        val order: BorrowOrder,
        val remainingCancellations: Int
    )

    suspend fun getBorrowOrders(): List<BorrowOrder> {
        return borrowApi.getBorrowOrders().map { it.toModel() }
    }

    suspend fun createBorrowOrder(bookId: Long, borrowDays: Int): BorrowOrder {
        return borrowApi.createBorrowOrder(
            CreateBorrowOrderRequestDto(bookId, borrowDays)
        ).toModel()
    }

    suspend fun getBorrowOrder(orderId: Long): BorrowOrder {
        return borrowApi.getBorrowOrder(orderId).toModel()
    }

    suspend fun cancelBorrowOrder(orderId: Long): CancelResult {
        val response = borrowApi.cancelBorrowOrder(orderId)
        return CancelResult(
            order = response.order.toModel(),
            remainingCancellations = response.remainingCancellations
        )
    }

    suspend fun getCurrentBorrowOrder(bookId: Long): BorrowOrder? {
        return borrowApi.getCurrentBorrowOrder(bookId).order?.toModel()
    }

    fun getBorrowSummary(): Flow<UserBorrowSummary> = flow {
        val userId = requireNotNull(checkLogin.getSavedUserId()?.toLongOrNull()) {
            "Không tìm thấy người dùng đang đăng nhập."
        }
        val refreshFailure = try {
            val remoteSummary = borrowApi.getHomeSummary()
            homeSummaryDao.upsert(remoteSummary.toEntity(userId))
            null
        } catch (error: Throwable) {
            if (!error.canReadFromCache()) throw error
            error
        }

        val cachedSummary = homeSummaryDao.findByUserId(userId)
        when {
            cachedSummary != null -> emit(cachedSummary.toModel())
            refreshFailure != null -> throw refreshFailure
            else -> emit(UserBorrowSummary())
        }
    }

    private fun HomeSummaryResponseDto.toEntity(userId: Long) = HomeSummaryEntity(
        userId = userId,
        pendingPickupCount = pendingPickupCount,
        borrowingCount = borrowingCount,
        dueSoonCount = dueSoonCount,
        overdueCount = overdueCount,
        favoriteCount = favoriteCount,
        returnedCount = returnedCount,
        allBorrowCount = allBorrowCount
    )

    private fun HomeSummaryEntity.toModel() = UserBorrowSummary(
        pendingPickupCount = pendingPickupCount,
        borrowingCount = borrowingCount,
        dueSoonCount = dueSoonCount,
        overdueCount = overdueCount,
        favoriteCount = favoriteCount,
        returnedCount = returnedCount,
        allBorrowCount = allBorrowCount
    )

    private fun Throwable.canReadFromCache(): Boolean =
        this is IOException || (this is HttpException && code() >= 500)

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
        totalAmount = totalAmount,
        paidAmount = paidAmount,
        depositRefunded = depositRefunded,
        remainingRefundAmount = remainingRefundAmount
    )
}
