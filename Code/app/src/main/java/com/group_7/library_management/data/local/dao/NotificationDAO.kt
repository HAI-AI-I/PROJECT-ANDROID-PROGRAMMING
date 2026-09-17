package com.group_7.library_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.group_7.library_management.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDAO {
    @Query("SELECT * FROM notifications WHERE userId = :userId AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllNotifications(userId: Long): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isDeleted = 0 ORDER BY createdAt DESC")
    suspend fun getAllNotificationsOnce(userId: Long): List<NotificationEntity>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isDeleted = 0 ORDER BY createdAt DESC LIMIT :amount")
    fun getNotifications(userId: Long, amount: Int): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun deleteNotificationsForUser(userId: Long)

    @Transaction
    suspend fun replaceNotificationsForUser(
        userId: Long,
        notifications: List<NotificationEntity>
    ) {
        deleteNotificationsForUser(userId)
        insertNotifications(notifications)
    }

    @Query("""
        DELETE FROM notifications
        WHERE id NOT IN (
              SELECT id FROM notifications
              ORDER BY createdAt DESC
              LIMIT :maxCount
          )
    """)
    suspend fun trimNotifications(maxCount: Int)

    @Transaction
    suspend fun cacheNotificationPage(
        userId: Long,
        page: Int,
        notifications: List<NotificationEntity>,
        maxCount: Int
    ) {
        if (page == 0) deleteNotificationsForUser(userId)
        if (notifications.isNotEmpty()) insertNotifications(notifications)
        trimNotifications(maxCount)
    }

    @Transaction
    suspend fun cacheNotification(
        userId: Long,
        notification: NotificationEntity,
        maxCount: Int
    ) {
        insertNotifications(listOf(notification))
        trimNotifications(maxCount)
    }

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId AND isDeleted = 0")
    suspend fun markAllAsRead(userId: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId AND id = :id")
    suspend fun markAsRead(userId: Long, id: Long)

    @Query("UPDATE notifications SET isDeleted = 1 WHERE userId = :userId AND id = :id")
    suspend fun deleteNotification(userId: Long, id: Long)
}
