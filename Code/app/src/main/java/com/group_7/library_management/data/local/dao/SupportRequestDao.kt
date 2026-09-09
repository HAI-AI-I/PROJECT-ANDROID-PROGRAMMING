package com.group_7.library_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.group_7.library_management.data.local.entity.SupportRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SupportRequestDao {
    @Query("SELECT * FROM support_requests")
    fun getAllRequests(): Flow<List<SupportRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: SupportRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<SupportRequestEntity>)

    @Query("SELECT * FROM support_requests WHERE isSynced = 0")
    suspend fun getUnsyncedRequests(): List<SupportRequestEntity>
}
