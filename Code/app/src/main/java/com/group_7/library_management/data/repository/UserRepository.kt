package com.group_7.library_management.data.repository

import com.group_7.library_management.data.local.dao.UserDAO
import com.group_7.library_management.data.local.entity.UserEntity
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.data.network.NetworkMonitor
import com.group_7.library_management.data.remote.api.AuthApi
import com.group_7.library_management.data.remote.dto.LoginRequestDto
import com.group_7.library_management.data.remote.dto.PasswordChangeCodeRequestDto
import com.group_7.library_management.data.remote.dto.PasswordCodeRequestDto
import com.group_7.library_management.data.remote.dto.PasswordCodeResponseDto
import com.group_7.library_management.data.remote.dto.PasswordResendRequestDto
import com.group_7.library_management.data.remote.dto.PasswordResetRequestDto
import com.group_7.library_management.data.remote.dto.PasswordVerificationRequestDto
import com.group_7.library_management.data.remote.dto.RegisterRequestDto
import com.group_7.library_management.data.remote.dto.RegistrationCodeResponseDto
import com.group_7.library_management.data.remote.dto.RegistrationVerificationMethod
import com.group_7.library_management.data.remote.dto.ResendRegistrationCodeRequestDto
import com.group_7.library_management.data.remote.dto.UserResponseDto
import com.group_7.library_management.data.remote.dto.UpdateProfileRequestDto
import com.group_7.library_management.data.remote.dto.VerifyRegistrationCodeRequestDto
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class UserRepository @Inject constructor(
    private val userDao: UserDAO,
    private val authApi: AuthApi,
    private val checkLogin: CheckLogin,
    private val networkMonitor: NetworkMonitor
) {
    suspend fun sendRegistrationCode    (
        name: String,
        email: String,
        phone: String,
        password: String,
        method: RegistrationVerificationMethod
    ): Result<RegistrationCodeResponseDto> {
        return try {
            val request = RegisterRequestDto(
                fullName = name.trim(),
                email = email.trim(),
                phone = phone.trim(),
                password = password
            )
            val response = when (method) {
                RegistrationVerificationMethod.EMAIL ->
                    authApi.sendRegistrationEmailCode(request)
                RegistrationVerificationMethod.SMS ->
                    authApi.sendRegistrationSmsCode(request)
            }
            Result.success(response)
        } catch (exception: Exception) {
            Result.failure(toReadableException(exception))
        }
    }

    suspend fun verifyRegistrationCode(
        registrationId: String,
        code: String,
        method: RegistrationVerificationMethod
    ): Result<UserResponseDto> {
        return try {
            val request = VerifyRegistrationCodeRequestDto(registrationId, code)
            val response = when (method) {
                RegistrationVerificationMethod.EMAIL ->
                    authApi.verifyRegistrationEmailCode(request)
                RegistrationVerificationMethod.SMS ->
                    authApi.verifyRegistrationSmsCode(request)
            }
            Result.success(response)
        } catch (exception: Exception) {
            Result.failure(toReadableException(exception))
        }
    }

    suspend fun resendRegistrationCode(
        registrationId: String,
        method: RegistrationVerificationMethod
    ): Result<RegistrationCodeResponseDto> {
        return try {
            val request = ResendRegistrationCodeRequestDto(registrationId)
            val response = when (method) {
                RegistrationVerificationMethod.EMAIL ->
                    authApi.resendRegistrationEmailCode(request)
                RegistrationVerificationMethod.SMS ->
                    authApi.resendRegistrationSmsCode(request)
            }
            Result.success(response)
        } catch (exception: Exception) {
            Result.failure(toReadableException(exception))
        }
    }

    suspend fun loginUser(
        emailOrPhone: String,
        password: String
    ): Result<UserResponseDto> {
        return try {
            val response = authApi.login(
                LoginRequestDto(
                    identifier = emailOrPhone.trim(),
                    password = password
                )
            )

            userDao.insertUser(response.user.toUserEntity())
            checkLogin.saveAccessToken(response.accessToken)
            Result.success(response.user)
        } catch (exception: Exception) {
            Result.failure(toReadableException(exception))
        }
    }

    suspend fun sendForgotPasswordCode(
        identifier: String,
        method: RegistrationVerificationMethod
    ): Result<PasswordCodeResponseDto> = apiResult {
        authApi.sendForgotPasswordCode(
            PasswordCodeRequestDto(identifier.trim(), method.name)
        )
    }

    suspend fun sendChangePasswordCode(
        method: RegistrationVerificationMethod
    ): Result<PasswordCodeResponseDto> = apiResult {
        authApi.sendChangePasswordCode(PasswordChangeCodeRequestDto(method.name))
    }

    suspend fun resendPasswordCode(
        requestId: String
    ): Result<PasswordCodeResponseDto> = apiResult {
        authApi.resendPasswordCode(PasswordResendRequestDto(requestId))
    }

    suspend fun verifyPasswordCode(
        requestId: String,
        code: String
    ): Result<String> = apiResult {
        authApi.verifyPasswordCode(
            PasswordVerificationRequestDto(requestId, code)
        ).resetToken
    }

    suspend fun resetPassword(
        resetToken: String,
        newPassword: String
    ): Result<Unit> = apiResult {
        val response = authApi.resetPassword(
            PasswordResetRequestDto(resetToken, newPassword)
        )
        if (!response.isSuccessful) throw HttpException(response)
        checkLogin.clearLogin()
    }

    suspend fun getUserById(id: Long): UserEntity? = userDao.getUserById(id)

    fun observeUserById(id: Long): Flow<UserEntity?> = userDao.observeUserById(id)

    suspend fun getLatestUser(): UserEntity? = userDao.getLatestUser()

    suspend fun getCurrentUserProfile(): Result<UserEntity> {
        val savedUserId = checkLogin.getSavedUserId()?.toLongOrNull()
            ?: return Result.failure(Exception("Không tìm thấy người dùng đang đăng nhập."))

        if (!networkMonitor.isConnected.value) {
            val cachedUser = userDao.getUserById(savedUserId)
            return if (cachedUser != null) {
                Result.success(cachedUser)
            } else {
                Result.failure(Exception("Không có dữ liệu hồ sơ đã lưu trên thiết bị."))
            }
        }

        return apiResult {
            val currentUser = authApi.getCurrentUser().toUserEntity()
            userDao.insertUser(currentUser)
            currentUser
        }
    }

    suspend fun updateCurrentUserProfile(
        fullName: String,
        email: String,
        phone: String
    ): Result<UserEntity> {
        if (!networkMonitor.isConnected.value) {
            return Result.failure(
                Exception("Không có kết nối mạng. Không thể cập nhật hồ sơ.")
            )
        }

        return apiResult {
            val updatedUser = authApi.updateCurrentUser(
                UpdateProfileRequestDto(
                    fullName = fullName.trim(),
                    email = email.trim(),
                    phone = phone.trim()
                )
            ).toUserEntity()
            userDao.insertUser(updatedUser)
            updatedUser
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            val response = authApi.logout()
            if (!response.isSuccessful && response.code() !in listOf(401, 403)) {
                throw HttpException(response)
            }

            checkLogin.clearLogin()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(toReadableException(exception))
        }
    }

    private fun UserResponseDto.toUserEntity(): UserEntity {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val joinDate = runCatching {
            OffsetDateTime.parse(createdAt).format(dateFormatter)
        }.getOrDefault(createdAt)

        return UserEntity(
            id = id,
            name = fullName,
            email = email,
            phone = phone,
            password = "",
            joinDate = joinDate,
            isActive = active,
            isDeleted = false,
            updateAt = joinDate
        )
    }

    private suspend fun <T> apiResult(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (exception: Exception) {
            Result.failure(toReadableException(exception))
        }
    }

    private fun toReadableException(exception: Exception): Exception {
        if (exception is SocketTimeoutException) {
            return Exception(
                "Máy chủ gửi mã xác nhận quá lâu. Vui lòng kiểm tra email hoặc tin nhắn trước khi thử lại."
            )
        }

        if (exception is IOException) {
            return Exception(
                "Không thể kết nối tới máy chủ. Hãy kiểm tra Spring Boot đang chạy ở cổng 8386."
            )
        }

        if (exception is HttpException) {
            val responseMessage = runCatching {
                val body = exception.response()?.errorBody()?.string().orEmpty()
                JSONObject(body).optString("message")
            }.getOrNull()

            if (!responseMessage.isNullOrBlank()) {
                return Exception(responseMessage)
            }

            return Exception("Máy chủ trả về lỗi HTTP ${exception.code()}.")
        }

        return Exception(exception.message ?: "Đã xảy ra lỗi khi kết nối tới máy chủ.")
    }
}
