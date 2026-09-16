package com.group_7.library_management.data.repository

import com.group_7.library_management.data.local.dao.BookDAO
import com.group_7.library_management.data.local.dao.BorrowReceiptDAO
import com.group_7.library_management.data.local.AppDatabase
import com.group_7.library_management.data.local.entity.BorrowReceiptEntity
import com.group_7.library_management.data.remote.api.BorrowingApi
import com.group_7.library_management.data.remote.dto.CreateBorrowingRequestDto
import com.group_7.library_management.models.UserBorrowSummary
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class BorrowRepository @Inject constructor(
    private val borrowReceiptDao: BorrowReceiptDAO,
    private val bookDao: BookDAO,
    private val database: AppDatabase,
    private val borrowingApi: BorrowingApi
) {
    suspend fun borrowBook(
        userId: Long,
        bookId: String,
        durationDays: Int = 14
    ): Result<BorrowReceiptEntity> {
        return try {
            val numericBookId = bookId.toLongOrNull()
                ?: return Result.failure(IllegalArgumentException("Mã sách không hợp lệ"))
            val serverBorrowDate = LocalDate.now()
            val serverDueDate = serverBorrowDate.plusDays(durationDays.toLong())
            val remoteBorrowing = borrowingApi.borrow(
                CreateBorrowingRequestDto(
                    readerId = userId,
                    bookId = numericBookId,
                    borrowDate = serverBorrowDate.toString(),
                    dueDate = serverDueDate.toString(),
                    note = "Borrowed from Android app"
                )
            )

            val receipt = database.withTransaction {
                // The server is authoritative and has already reserved one copy atomically.
                // Keep the Room cache in sync when the book is present locally.
                bookDao.decreaseAvailableCopies(bookId)

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val calendar = Calendar.getInstance()
                val borrowDate = dateFormat.format(calendar.time)
                calendar.add(Calendar.DAY_OF_YEAR, durationDays)
                val dueDate = dateFormat.format(calendar.time)

                val newReceipt = BorrowReceiptEntity(
                    userId = userId,
                    bookId = bookId,
                    borrowDate = borrowDate,
                    dueDate = dueDate,
                    status = "BORROWED"
                )
                val receiptId = borrowReceiptDao.insertReceipt(newReceipt)
                newReceipt.copy(receiptId = receiptId)
            }
            Result.success(receipt.copy(receiptId = remoteBorrowing.id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun returnBook(
        receiptId: Long,
        bookId: String,
        fineAmount: Long = 0
    ): Result<Boolean> {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val returnDate = dateFormat.format(Date())

            borrowReceiptDao.returnBook(receiptId, returnDate, fineAmount)
            bookDao.increaseAvailableCopies(bookId)
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getBorrowedBooksByUserId(userId: Long): Flow<List<BorrowReceiptEntity>> {
        return borrowReceiptDao.getBorrowedBooksByUserId(userId)
    }

    fun getHistoryByUserId(userId: Long): Flow<List<BorrowReceiptEntity>> {
        return borrowReceiptDao.getHistoryByUserId(userId)
    }

    suspend fun getBorrowedCount(userId: Long): Int {
        return borrowReceiptDao.getBorrowedCount(userId)
    }

    suspend fun getTotalBooksReadCount(userId: Long): Int {
        return borrowReceiptDao.getTotalBooksReadCount(userId)
    }

    fun getBorrowSummary(userIdString: String = "0"): Flow<UserBorrowSummary> = flow {
        val userId = userIdString.toLongOrNull() ?: 0L
        val borrowing = borrowReceiptDao.getBorrowedCount(userId)
        val totalRead = borrowReceiptDao.getTotalBooksReadCount(userId)
        // Các giá trị khác có thể giả lập hoặc lấy thêm từ DAO nếu có
        emit(
            UserBorrowSummary(
                pendingPickupCount = 0,
                borrowingCount = borrowing,
                dueSoonCount = 0,
                overdueCount = 0,
                favoriteCount = 0,
                returnedCount = totalRead
            )
        )
    }
}
