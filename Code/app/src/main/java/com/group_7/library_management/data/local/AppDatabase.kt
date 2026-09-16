package com.group_7.library_management.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.group_7.library_management.data.local.dao.BookDAO
import com.group_7.library_management.data.local.dao.NotificationDAO
import com.group_7.library_management.data.local.dao.BorrowReceiptDAO
import com.group_7.library_management.data.local.dao.UserDAO
import com.group_7.library_management.data.local.entity.BookEntity
import com.group_7.library_management.data.local.entity.NotificationEntity
import com.group_7.library_management.data.local.entity.BorrowReceiptEntity
import com.group_7.library_management.data.local.entity.UserEntity

@Database(
    entities = [BookEntity::class, NotificationEntity::class, UserEntity::class, BorrowReceiptEntity::class],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getBookDao(): BookDAO
    abstract fun getNotificationDao(): NotificationDAO
    abstract fun getUserDao(): UserDAO
    abstract fun getBorrowReceiptDao(): BorrowReceiptDAO
}
