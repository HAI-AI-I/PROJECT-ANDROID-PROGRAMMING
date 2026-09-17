package com.group_7.library_management.data.remote.api

import com.group_7.library_management.data.remote.dto.AuthResponseDto
import com.group_7.library_management.data.remote.dto.LoginRequestDto
import com.group_7.library_management.data.remote.dto.RegisterRequestDto
import com.group_7.library_management.data.remote.dto.RegistrationCodeResponseDto
import com.group_7.library_management.data.remote.dto.ResendRegistrationCodeRequestDto
import com.group_7.library_management.data.remote.dto.UserResponseDto
import com.group_7.library_management.data.remote.dto.VerifyRegistrationCodeRequestDto
import retrofit2.http.Body
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

    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<Unit>
}
