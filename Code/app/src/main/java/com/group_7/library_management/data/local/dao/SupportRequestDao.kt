package com.group_7.library_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.group_7.library_management.data.local.entity.SupportRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SupportRequestDao {
    @Query("SELECT * FROM support_requests WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeRequests(userId: Long): Flow<List<SupportRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(request: SupportRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(requests: List<SupportRequestEntity>)

    @Query("DELETE FROM support_requests WHERE userId = :userId")
    suspend fun deleteForUser(userId: Long)

    @Transaction
    suspend fun replaceForUser(userId: Long, requests: List<SupportRequestEntity>) {
        deleteForUser(userId)
        if (requests.isNotEmpty()) upsertAll(requests)
    }
}
