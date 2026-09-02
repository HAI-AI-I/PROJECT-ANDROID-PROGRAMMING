package com.group_7.library_management.data.repository

//import com.group_7.library_management.data.local.dao.UserDao
//import com.group_7.library_management.data.local.entity.UserEntity
//import kotlinx.coroutines.flow.Flow
//
//class UserRepository(private val userDao: UserDao) {
//
//    fun getUser(userId: String): Flow<UserEntity?> {
//        return userDao.getUserById(userId)
//    }
//
//    suspend fun saveSampleUser() {
//        val sample = UserEntity(
//            name = "Nguyễn Văn An",
//            email = "nguyenvanan.uth@gmail.com",
//            phone = "0987654321",
//            joinDate = "15/08/2024",
//            borrowedBooksCount = 2,
//            totalBooksRead = 12
//        )
//        userDao.insertUser(sample)
//    }
//}
