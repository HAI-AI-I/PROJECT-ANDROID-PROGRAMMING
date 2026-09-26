package com.group_7.library_management.data.remote.dto

data class RegisterRequestDto(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String
)

data class LoginRequestDto(
    val identifier: String,
    val password: String
)

data class UpdateProfileRequestDto(
    val fullName: String,
    val email: String,
    val phone: String
)

data class UserResponseDto(
    val id: Long,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: String,
    val active: Boolean,
    val createdAt: String
)

data class AuthResponseDto(
    val accessToken: String,
    val tokenType: String,
    val user: UserResponseDto
)

data class RegistrationCodeResponseDto(
    val registrationId: String,
    val destination: String,
    val expiresInSeconds: Long
)

data class VerifyRegistrationCodeRequestDto(
    val registrationId: String,
    val code: String
)

data class ResendRegistrationCodeRequestDto(
    val registrationId: String
)

data class PasswordCodeRequestDto(
    val identifier: String,
    val channel: String
)

data class PasswordChangeCodeRequestDto(
    val channel: String
)

data class PasswordCodeResponseDto(
    val requestId: String,
    val destination: String,
    val expiresInSeconds: Long
)

data class PasswordResendRequestDto(
    val requestId: String
)

data class PasswordVerificationRequestDto(
    val requestId: String,
    val code: String
)

data class PasswordVerificationResponseDto(
    val resetToken: String
)

data class PasswordResetRequestDto(
    val resetToken: String,
    val newPassword: String
)

enum class RegistrationVerificationMethod {
    EMAIL,
    SMS
}
