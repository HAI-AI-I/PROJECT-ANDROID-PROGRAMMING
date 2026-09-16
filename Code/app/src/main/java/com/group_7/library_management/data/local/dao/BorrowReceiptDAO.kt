package com.group_7.library_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.group_7.library_management.data.local.entity.BorrowReceiptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BorrowReceiptDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: BorrowReceiptEntity): Long

    @Query("UPDATE borrow_receipts SET returnDate = :returnDate, status = 'RETURNED', fineAmount = :fineAmount WHERE receiptId = :receiptId")
    suspend fun returnBook(receiptId: Long, returnDate: String, fineAmount: Long)

    @Query("SELECT * FROM borrow_receipts WHERE userId = :userId AND status = 'BORROWED' ORDER BY receiptId DESC")
    fun getBorrowedBooksByUserId(userId: Long): Flow<List<BorrowReceiptEntity>>

    @Query("SELECT * FROM borrow_receipts WHERE userId = :userId ORDER BY receiptId DESC")
    fun getHistoryByUserId(userId: Long): Flow<List<BorrowReceiptEntity>>

    @Query("SELECT COUNT(*) FROM borrow_receipts WHERE userId = :userId AND status = 'BORROWED'")
    suspend fun getBorrowedCount(userId: Long): Int

    @Query("SELECT COUNT(*) FROM borrow_receipts WHERE userId = :userId")
    suspend fun getTotalBooksReadCount(userId: Long): Int

    @Query("SELECT * FROM borrow_receipts WHERE receiptId = :receiptId LIMIT 1")
    suspend fun getReceiptById(receiptId: Long): BorrowReceiptEntity?
}
