package com.group_7.library_management.data.remote.api

import com.group_7.library_management.data.remote.dto.AuthResponseDto
import com.group_7.library_management.data.remote.dto.LoginRequestDto
import com.group_7.library_management.data.remote.dto.PasswordChangeCodeRequestDto
import com.group_7.library_management.data.remote.dto.PasswordCodeRequestDto
import com.group_7.library_management.data.remote.dto.PasswordCodeResponseDto
import com.group_7.library_management.data.remote.dto.PasswordResendRequestDto
import com.group_7.library_management.data.remote.dto.PasswordResetRequestDto
import com.group_7.library_management.data.remote.dto.PasswordVerificationRequestDto
import com.group_7.library_management.data.remote.dto.PasswordVerificationResponseDto
import com.group_7.library_management.data.remote.dto.RegisterRequestDto
import com.group_7.library_management.data.remote.dto.RegistrationCodeResponseDto
import com.group_7.library_management.data.remote.dto.ResendRegistrationCodeRequestDto
import com.group_7.library_management.data.remote.dto.UserResponseDto
import com.group_7.library_management.data.remote.dto.UpdateProfileRequestDto
import com.group_7.library_management.data.remote.dto.VerifyRegistrationCodeRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.Response

interface AuthApi {
    @POST("api/v1/auth/register/email/code")
    suspend fun sendRegistrationEmailCode(
        @Body request: RegisterRequestDto
    ): RegistrationCodeResponseDto

    @POST("api/v1/auth/register/email/resend")
    suspend fun resendRegistrationEmailCode(
        @Body request: ResendRegistrationCodeRequestDto
    ): RegistrationCodeResponseDto

    @POST("api/v1/auth/register/email/verify")
    suspend fun verifyRegistrationEmailCode(
        @Body request: VerifyRegistrationCodeRequestDto
    ): UserResponseDto

    @POST("api/v1/auth/register/phone/code")
    suspend fun sendRegistrationSmsCode(
        @Body request: RegisterRequestDto
    ): RegistrationCodeResponseDto

    @POST("api/v1/auth/register/phone/resend")
    suspend fun resendRegistrationSmsCode(
        @Body request: ResendRegistrationCodeRequestDto
    ): RegistrationCodeResponseDto

    @POST("api/v1/auth/register/phone/verify")
    suspend fun verifyRegistrationSmsCode(
        @Body request: VerifyRegistrationCodeRequestDto
    ): UserResponseDto

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @GET("api/v1/auth/me")
    suspend fun getCurrentUser(): UserResponseDto

    @PATCH("api/v1/auth/me")
    suspend fun updateCurrentUser(
        @Body request: UpdateProfileRequestDto
    ): UserResponseDto

    @POST("api/v1/auth/password/forgot/code")
    suspend fun sendForgotPasswordCode(
        @Body request: PasswordCodeRequestDto
    ): PasswordCodeResponseDto

    @POST("api/v1/auth/password/change/code")
    suspend fun sendChangePasswordCode(
        @Body request: PasswordChangeCodeRequestDto
    ): PasswordCodeResponseDto

    @POST("api/v1/auth/password/code/resend")
    suspend fun resendPasswordCode(
        @Body request: PasswordResendRequestDto
    ): PasswordCodeResponseDto

    @POST("api/v1/auth/password/code/verify")
    suspend fun verifyPasswordCode(
        @Body request: PasswordVerificationRequestDto
    ): PasswordVerificationResponseDto

    @POST("api/v1/auth/password/reset")
    suspend fun resetPassword(
        @Body request: PasswordResetRequestDto
    ): Response<Unit>

    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<Unit>
}
