package com.group_7.library_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.group_7.library_management.data.local.entity.UserEntity

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email AND isDeleted = 0 LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone AND isDeleted = 0 LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE (email = :input OR phone = :input) AND isDeleted = 0 LIMIT 1")
    suspend fun getUserByEmailOrPhone(input: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id AND isDeleted = 0 LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?
}
