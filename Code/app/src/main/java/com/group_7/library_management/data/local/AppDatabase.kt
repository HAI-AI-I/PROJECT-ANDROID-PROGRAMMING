package com.group_7.library_management.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.group_7.library_management.data.local.dao.BookDAO
import com.group_7.library_management.data.local.dao.NotificationDAO
import com.group_7.library_management.data.local.dao.UserDAO
import com.group_7.library_management.data.local.dao.SupportRequestDao
import com.group_7.library_management.data.local.entity.BookEntity
import com.group_7.library_management.data.local.entity.NotificationEntity
import com.group_7.library_management.data.local.entity.UserEntity
import com.group_7.library_management.data.local.entity.SupportRequestEntity

@Database(
    entities = [BookEntity::class, NotificationEntity::class, UserEntity::class, SupportRequestEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getBookDao(): BookDAO
    abstract fun getNotificationDao(): NotificationDAO
    abstract fun getUserDao(): UserDAO
    abstract fun getSupportRequestDao(): SupportRequestDao
}
