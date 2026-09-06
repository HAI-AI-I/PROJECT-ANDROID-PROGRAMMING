package com.group_7.library_management.data.repository

import com.group_7.library_management.data.local.dao.UserDAO
import com.group_7.library_management.data.local.entity.UserEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDAO
) {
    suspend fun registerUser(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<UserEntity> {
        return try {
            val existingEmail = userDao.getUserByEmail(email)
            if (existingEmail != null) {
                return Result.failure(Exception("Email đã được sử dụng"))
            }

            val existingPhone = userDao.getUserByPhone(phone)
            if (existingPhone != null) {
                return Result.failure(Exception("Số điện thoại đã được sử dụng"))
            }

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val currentDate = dateFormat.format(Date())

            val newUser = UserEntity(
                name = name,
                email = email,
                phone = phone,
                password = password,
                joinDate = currentDate,
                updateAt = currentDate
            )

            val id = userDao.insertUser(newUser)
            Result.success(newUser.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(
        emailOrPhone: String,
        password: String
    ): Result<UserEntity> {
        return try {
            val user = userDao.getUserByEmailOrPhone(emailOrPhone)
            if (user == null) {
                return Result.failure(Exception("Tài khoản không tồn tại"))
            }
            if (user.password != password) {
                return Result.failure(Exception("Mật khẩu không đúng"))
            }
            if (!user.isActive) {
                return Result.failure(Exception("Tài khoản đã bị khóa"))
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(id: Long): UserEntity? {
        return userDao.getUserById(id)
    }
}
