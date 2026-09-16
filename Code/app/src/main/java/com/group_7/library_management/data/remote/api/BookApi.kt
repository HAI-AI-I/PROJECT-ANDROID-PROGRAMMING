package com.group_7.library_management.data.remote.api

import com.group_7.library_management.data.remote.dto.BookResponseDto
import com.group_7.library_management.data.remote.dto.PopularBookResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface BookApi {
    @GET("api/v1/books/latest")
    suspend fun getLatestBooks(
        @Query("limit") limit: Int
    ): List<BookResponseDto>

    @GET("api/v1/books/popular")
    suspend fun getPopularBooks(
        @Query("limit") limit: Int
    ): List<PopularBookResponseDto>
}
