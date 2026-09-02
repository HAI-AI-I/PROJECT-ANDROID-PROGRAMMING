package com.group_7.library_management.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.group_7.library_management.data.local.dao.BookDAO
import com.group_7.library_management.data.local.entity.BookEntity
import com.group_7.library_management.models.Book

@Database(entities = [BookEntity::class]
    ,version = 1
    ,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getBookDao(): BookDAO
}