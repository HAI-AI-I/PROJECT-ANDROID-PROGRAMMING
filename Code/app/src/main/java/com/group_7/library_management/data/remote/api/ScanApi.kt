package com.group_7.library_management.data.remote.api

import com.group_7.library_management.data.remote.dto.ScanResolveResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ScanApi {
    @GET("api/v1/scan/resolve")
    suspend fun resolve(@Query("code") code: String): ScanResolveResponseDto
}
