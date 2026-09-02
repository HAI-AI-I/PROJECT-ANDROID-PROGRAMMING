package com.group_7.library_management.data.local.dao

//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.group_7.library_management.data.local.entity.UserEntity
//import kotlinx.coroutines.flow.Flow
//
//@Dao
//interface UserDao {
//    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
//    fun getUserById(userId: String): Flow<UserEntity?>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertUser(user: UserEntity)
//
//    @Query("DELETE FROM users")
//    suspend fun clearAll()
//}