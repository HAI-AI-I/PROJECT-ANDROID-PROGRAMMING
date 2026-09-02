package com.group_7.library_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.group_7.library_management.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books ORDER BY createdAt DESC LIMIT :limit")
    fun getNewestBooks(limit: Int): Flow<List<BookEntity>>

    @Query("SELECT * FROM books ORDER BY viewCount DESC LIMIT :limit")
    fun getPopularBooks(limit: Int): Flow<List<BookEntity>>

    @Query("SELECT * FROM books ORDER BY rating DESC LIMIT :limit")
    fun getRecommendedBooks(limit: Int): Flow<List<BookEntity>>
}