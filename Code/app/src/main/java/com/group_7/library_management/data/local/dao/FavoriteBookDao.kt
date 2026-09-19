package com.group_7.library_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.group_7.library_management.data.local.entity.FavoriteBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteBookDao {
    @Query("SELECT * FROM favorite_books WHERE userId = :userId ORDER BY favoritedAt DESC")
    fun observeByUser(userId: String): Flow<List<FavoriteBookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(book: FavoriteBookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(books: List<FavoriteBookEntity>)

    @Query("DELETE FROM favorite_books WHERE userId = :userId AND bookId = :bookId")
    suspend fun delete(userId: String, bookId: String)

    @Query("DELETE FROM favorite_books WHERE userId = :userId")
    suspend fun deleteByUser(userId: String)

    @Transaction
    suspend fun replaceForUser(userId: String, books: List<FavoriteBookEntity>) {
        deleteByUser(userId)
        upsertAll(books)
    }
}
