package com.group_7.library_management.data.remote.api

import com.group_7.library_management.data.remote.dto.CreateBorrowingRequestDto
import com.group_7.library_management.data.remote.dto.BorrowingResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface BorrowingApi {
    @POST("api/borrowings")
    suspend fun borrow(@Body request: CreateBorrowingRequestDto): BorrowingResponseDto
}
