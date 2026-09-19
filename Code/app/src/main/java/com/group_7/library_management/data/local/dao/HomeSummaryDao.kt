package com.group_7.library_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.group_7.library_management.data.local.entity.HomeSummaryEntity

@Dao
interface HomeSummaryDao {
    @Query("SELECT * FROM home_summaries WHERE userId = :userId LIMIT 1")
    suspend fun findByUserId(userId: Long): HomeSummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(summary: HomeSummaryEntity)
}
