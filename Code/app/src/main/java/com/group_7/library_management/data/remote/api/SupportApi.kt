package com.group_7.library_management.data.remote.api

import com.group_7.library_management.data.remote.dto.CreateSupportRequestDto
import com.group_7.library_management.data.remote.dto.SupportRequestResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SupportApi {
    @GET("api/v1/support-requests")
    suspend fun getMyRequests(): List<SupportRequestResponseDto>

    @POST("api/v1/support-requests")
    suspend fun createRequest(
        @Body request: CreateSupportRequestDto
    ): SupportRequestResponseDto
}
