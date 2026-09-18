package com.group_7.library_management.data.remote.api

import com.group_7.library_management.data.remote.dto.BookResponseDto
import com.group_7.library_management.data.remote.dto.PopularBookResponseDto
import com.group_7.library_management.data.remote.dto.BookAvailabilitySubscriptionResponseDto
import com.group_7.library_management.data.remote.dto.BookReviewRequestDto
import com.group_7.library_management.data.remote.dto.BookReviewResponseDto
import com.group_7.library_management.data.remote.dto.PagedResponseDto
import com.group_7.library_management.data.remote.dto.MyBookReviewResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface BookApi {
    @GET("api/v1/books/{id}")
    suspend fun getBook(@Path("id") id: String): BookResponseDto

    @GET("api/v1/books/{id}/related")
    suspend fun getRelatedBooks(
        @Path("id") id: String,
        @Query("limit") limit: Int
    ): List<PopularBookResponseDto>

    @GET("api/v1/books/{id}/reviews")
    suspend fun getBookReviews(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("sort") sort: String = "newest"
    ): PagedResponseDto<BookReviewResponseDto>

    @GET("api/v1/books/{id}/reviews/mine")
    suspend fun getMyBookReview(
        @Path("id") id: String
    ): MyBookReviewResponseDto

    @POST("api/v1/books/{id}/reviews")
    suspend fun createBookReview(
        @Path("id") id: String,
        @Body request: BookReviewRequestDto
    ): BookReviewResponseDto

    @PATCH("api/v1/books/{id}/reviews/{reviewId}")
    suspend fun updateBookReview(
        @Path("id") id: String,
        @Path("reviewId") reviewId: Long,
        @Body request: BookReviewRequestDto
    ): BookReviewResponseDto

    @DELETE("api/v1/books/{id}/reviews/{reviewId}")
    suspend fun deleteBookReview(
        @Path("id") id: String,
        @Path("reviewId") reviewId: Long
    )

    @GET("api/v1/notification-subscriptions/books/{id}")
    suspend fun getAvailabilitySubscription(
        @Path("id") id: String
    ): BookAvailabilitySubscriptionResponseDto

    @PUT("api/v1/notification-subscriptions/books/{id}")
    suspend fun subscribeToAvailability(
        @Path("id") id: String
    ): BookAvailabilitySubscriptionResponseDto

    @DELETE("api/v1/notification-subscriptions/books/{id}")
    suspend fun unsubscribeFromAvailability(
        @Path("id") id: String
    ): BookAvailabilitySubscriptionResponseDto

    @GET("api/v1/books/latest")
    suspend fun getLatestBooks(
        @Query("limit") limit: Int
    ): List<BookResponseDto>

    @GET("api/v1/books/popular")
    suspend fun getPopularBooks(
        @Query("limit") limit: Int
    ): List<PopularBookResponseDto>
}
