package com.group_7.library_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.group_7.library_management.data.local.entity.BorrowOrderEntity

@Dao
interface BorrowOrderDao {
    @Query("SELECT * FROM borrow_orders WHERE userId = :userId ORDER BY requestedAt DESC")
    suspend fun getOrders(userId: Long): List<BorrowOrderEntity>

    @Query("SELECT * FROM borrow_orders WHERE userId = :userId AND id = :orderId LIMIT 1")
    suspend fun findOrder(userId: Long, orderId: Long): BorrowOrderEntity?

    @Query(
        """
        SELECT * FROM borrow_orders
        WHERE userId = :userId
          AND bookId = :bookId
          AND status IN ('PENDING_PAYMENT', 'REQUESTED', 'BORROWED', 'OVERDUE')
        ORDER BY requestedAt DESC
        LIMIT 1
        """
    )
    suspend fun findCurrentOrder(userId: Long, bookId: Long): BorrowOrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(order: BorrowOrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(orders: List<BorrowOrderEntity>)

    @Query("DELETE FROM borrow_orders WHERE userId = :userId")
    suspend fun deleteForUser(userId: Long)

    @Query(
        """
        DELETE FROM borrow_orders
        WHERE userId = :userId
          AND bookId = :bookId
          AND status IN ('PENDING_PAYMENT', 'REQUESTED', 'BORROWED', 'OVERDUE')
        """
    )
    suspend fun deleteCurrentOrder(userId: Long, bookId: Long)

    @Query(
        """
        UPDATE borrow_orders
        SET paidAmount = :paidAmount,
            paymentStatus = :paymentStatus,
            paymentCode = :paymentCode,
            paidAt = :paidAt
        WHERE userId = :userId AND id = :orderId
        """
    )
    suspend fun updatePayment(
        userId: Long,
        orderId: Long,
        paidAmount: Long,
        paymentStatus: String,
        paymentCode: String,
        paidAt: String?
    )

    @Transaction
    suspend fun replaceForUser(userId: Long, orders: List<BorrowOrderEntity>) {
        deleteForUser(userId)
        if (orders.isNotEmpty()) upsertAll(orders)
    }
}
