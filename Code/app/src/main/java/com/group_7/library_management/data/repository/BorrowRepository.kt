package com.group_7.library_management.data.repository

import com.group_7.library_management.data.local.dao.HomeSummaryDao
import com.group_7.library_management.data.local.dao.BorrowOrderDao
import com.group_7.library_management.data.local.entity.BorrowOrderEntity
import com.group_7.library_management.data.local.entity.HomeSummaryEntity
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.data.network.NetworkMonitor
import com.group_7.library_management.data.remote.api.BorrowApi
import com.group_7.library_management.data.remote.dto.BorrowOrderResponseDto
import com.group_7.library_management.data.remote.dto.CreateBorrowOrderRequestDto
import com.group_7.library_management.data.remote.dto.HomeSummaryResponseDto
import com.group_7.library_management.models.BorrowOrder
import com.group_7.library_management.models.BorrowPayment
import com.group_7.library_management.models.UserBorrowSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

class BorrowRepository @Inject constructor(
    private val borrowApi: BorrowApi,
    private val borrowOrderDao: BorrowOrderDao,
    private val homeSummaryDao: HomeSummaryDao,
    private val checkLogin: CheckLogin,
    private val networkMonitor: NetworkMonitor
) {
    data class CancelResult(
        val order: BorrowOrder,
        val remainingCancellations: Int
    )

    data class BorrowOrderPage(
        val items: List<BorrowOrder>,
        val page: Int,
        val totalPages: Int
    )

    suspend fun getBorrowOrders(
        status: String? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): BorrowOrderPage {
        val userId = requireCurrentUserId()
        if (!networkMonitor.isConnected.value) {
            return getCachedBorrowOrders(userId, status, page, pageSize)
        }

        return try {
            val response = borrowApi.getBorrowOrders(status, page, pageSize)
            val orders = response.items.map { it.toModel() }
            val entities = orders.map { it.toEntity(userId) }
            if (page == 1 && status.isNullOrBlank()) {
                borrowOrderDao.replaceForUser(userId, entities)
            } else {
                borrowOrderDao.upsertAll(entities)
            }
            BorrowOrderPage(
                items = orders,
                page = response.page,
                totalPages = response.totalPages
            )
        } catch (error: Throwable) {
            val cachedPage = getCachedBorrowOrders(userId, status, page, pageSize)
            if (error.canReadFromCache() && cachedPage.items.isNotEmpty()) cachedPage else throw error
        }
    }

    private suspend fun getCachedBorrowOrders(
        userId: Long,
        status: String?,
        page: Int,
        pageSize: Int
    ): BorrowOrderPage {
        val filtered = borrowOrderDao.getOrders(userId)
            .map { it.toModel() }
            .filter { it.matchesStatus(status) }
        val safePage = page.coerceAtLeast(1)
        val safePageSize = pageSize.coerceAtLeast(1)
        val fromIndex = ((safePage - 1) * safePageSize).coerceAtMost(filtered.size)
        val toIndex = (fromIndex + safePageSize).coerceAtMost(filtered.size)
        val totalPages = if (filtered.isEmpty()) 0 else {
            (filtered.size + safePageSize - 1) / safePageSize
        }
        return BorrowOrderPage(
            items = filtered.subList(fromIndex, toIndex),
            page = safePage,
            totalPages = totalPages
        )
    }

    private fun BorrowOrder.matchesStatus(filter: String?): Boolean {
        val now = Instant.now()
        val due = runCatching { Instant.parse(dueAt) }.getOrNull()
        val remaining = due?.let { Duration.between(now, it) }
        val isDueSoon = status == "BORROWED" && remaining != null &&
            !remaining.isNegative && remaining <= Duration.ofDays(1)
        return when (filter?.uppercase()) {
            null, "", "ALL" -> true
            "PENDING_PAYMENT" -> status == "PENDING_PAYMENT"
            "PENDING", "REQUESTED" -> status == "REQUESTED"
            "BORROWING", "BORROWED" -> status == "BORROWED" && !isDueSoon
            "DUE_SOON" -> isDueSoon
            "OVERDUE" -> status == "OVERDUE" || (status == "BORROWED" && due?.isBefore(now) == true)
            "RETURNED" -> status == "RETURNED"
            "CANCELLED" -> status == "CANCELLED"
            else -> false
        }
    }

    suspend fun createBorrowOrder(bookId: Long, borrowDays: Int): BorrowOrder {
        requireNetwork()
        val userId = requireCurrentUserId()
        val order = borrowApi.createBorrowOrder(
            CreateBorrowOrderRequestDto(bookId, borrowDays)
        ).toModel()
        borrowOrderDao.upsert(order.toEntity(userId))
        return order
    }

    suspend fun getBorrowOrder(orderId: Long): BorrowOrder {
        val userId = requireCurrentUserId()
        if (!networkMonitor.isConnected.value) {
            return requireNotNull(borrowOrderDao.findOrder(userId, orderId)) {
                "Không có dữ liệu đơn mượn đã lưu trên thiết bị."
            }.toModel()
        }

        return try {
            val order = borrowApi.getBorrowOrder(orderId).toModel()
            borrowOrderDao.upsert(order.toEntity(userId))
            order
        } catch (error: Throwable) {
            val cachedOrder = borrowOrderDao.findOrder(userId, orderId)
            if (error.canReadFromCache() && cachedOrder != null) cachedOrder.toModel() else throw error
        }
    }

    suspend fun cancelBorrowOrder(orderId: Long): CancelResult {
        requireNetwork()
        val userId = requireCurrentUserId()
        val response = borrowApi.cancelBorrowOrder(orderId)
        val order = response.order.toModel()
        borrowOrderDao.upsert(order.toEntity(userId))
        return CancelResult(
            order = order,
            remainingCancellations = response.remainingCancellations
        )
    }

    suspend fun getCurrentBorrowOrder(bookId: Long): BorrowOrder? {
        val userId = requireCurrentUserId()
        if (!networkMonitor.isConnected.value) {
            return borrowOrderDao.findCurrentOrder(userId, bookId)?.toModel()
        }

        return try {
            val order = borrowApi.getCurrentBorrowOrder(bookId).order?.toModel()
            if (order == null) {
                borrowOrderDao.deleteCurrentOrder(userId, bookId)
            } else {
                borrowOrderDao.upsert(order.toEntity(userId))
            }
            order
        } catch (error: Throwable) {
            val cachedOrder = borrowOrderDao.findCurrentOrder(userId, bookId)
            if (error.canReadFromCache()) cachedOrder?.toModel() else throw error
        }
    }

    suspend fun getBorrowPayment(orderId: Long): BorrowPayment {
        requireNetwork()
        val userId = requireCurrentUserId()
        val response = borrowApi.getBorrowPayment(orderId)
        val payment = BorrowPayment(
            orderId = response.orderId,
            referenceCode = response.referenceCode,
            paymentCode = response.paymentCode,
            amount = response.amount,
            paidAmount = response.paidAmount,
            paymentStatus = response.paymentStatus,
            bankCode = response.bankCode,
            accountNumber = response.accountNumber,
            accountName = response.accountName,
            qrUrl = response.qrUrl,
            paidAt = response.paidAt
        )
        borrowOrderDao.updatePayment(
            userId = userId,
            orderId = payment.orderId,
            paidAmount = payment.paidAmount,
            paymentStatus = payment.paymentStatus,
            paymentCode = payment.paymentCode,
            paidAt = payment.paidAt
        )
        return payment
    }

    fun getBorrowSummary(): Flow<UserBorrowSummary> = flow {
        val userId = requireNotNull(checkLogin.getSavedUserId()?.toLongOrNull()) {
            "Không tìm thấy người dùng đang đăng nhập."
        }
        val refreshFailure = if (networkMonitor.isConnected.value) {
            try {
                val remoteSummary = borrowApi.getHomeSummary()
                homeSummaryDao.upsert(remoteSummary.toEntity(userId))
                null
            } catch (error: Throwable) {
                if (!error.canReadFromCache()) throw error
                error
            }
        } else {
            null
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

    private fun requireCurrentUserId(): Long =
        requireNotNull(checkLogin.getSavedUserId()?.toLongOrNull()) {
            "Không tìm thấy người dùng đang đăng nhập."
        }

    private fun requireNetwork() {
        if (!networkMonitor.isConnected.value) {
            throw IOException("Không có kết nối mạng.")
        }
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
        totalAmount = totalAmount,
        paidAmount = paidAmount,
        paymentCode = paymentCode,
        paymentStatus = paymentStatus,
        paymentMethod = paymentMethod,
        paidAt = paidAt,
        depositRefunded = depositRefunded,
        remainingRefundAmount = remainingRefundAmount
    )

    private fun BorrowOrder.toEntity(userId: Long) = BorrowOrderEntity(
        userId = userId,
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
        paymentCode = paymentCode,
        paymentStatus = paymentStatus,
        paymentMethod = paymentMethod,
        paidAt = paidAt,
        depositRefunded = depositRefunded,
        remainingRefundAmount = remainingRefundAmount
    )

    private fun BorrowOrderEntity.toModel() = BorrowOrder(
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
        paymentCode = paymentCode,
        paymentStatus = paymentStatus,
        paymentMethod = paymentMethod,
        paidAt = paidAt,
        depositRefunded = depositRefunded,
        remainingRefundAmount = remainingRefundAmount
    )
}
