package com.group_7.library_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.group_7.library_management.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()

    @Transaction
    suspend fun replaceBooks(books: List<BookEntity>) {
        deleteAllBooks()
        insertBooks(books)
    }

    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books")
    suspend fun getAllBooksOnce(): List<BookEntity>

    @Query("SELECT * FROM books WHERE id IN (:ids)")
    suspend fun getBooksByIds(ids: List<String>): List<BookEntity>

    @Query("UPDATE books SET popularityScore = 0")
    suspend fun resetPopularityScores()

    @Query("SELECT * FROM books ORDER BY createdAt DESC LIMIT :limit")
    fun getNewestBooks(limit: Int): Flow<List<BookEntity>>

    @Query("SELECT * FROM books ORDER BY popularityScore DESC, createdAt DESC LIMIT :limit")
    fun getPopularBooks(limit: Int): Flow<List<BookEntity>>

    @Query("SELECT * FROM books ORDER BY rating DESC LIMIT :limit")
    fun getRecommendedBooks(limit: Int): Flow<List<BookEntity>>
}
