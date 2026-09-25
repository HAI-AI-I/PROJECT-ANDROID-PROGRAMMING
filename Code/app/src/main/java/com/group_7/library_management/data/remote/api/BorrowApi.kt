package com.group_7.library_management.data.remote.api

import com.group_7.library_management.data.remote.dto.BorrowOrderResponseDto
import com.group_7.library_management.data.remote.dto.CreateBorrowOrderRequestDto
import com.group_7.library_management.data.remote.dto.CurrentBorrowOrderResponseDto
import com.group_7.library_management.data.remote.dto.HomeSummaryResponseDto
import com.group_7.library_management.data.remote.dto.CancelBorrowOrderResponseDto
import com.group_7.library_management.data.remote.dto.BorrowPaymentResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BorrowApi {
    @GET("api/v1/home/summary")
    suspend fun getHomeSummary(): HomeSummaryResponseDto

    @GET("api/v1/borrow-orders")
    suspend fun getBorrowOrders(): List<BorrowOrderResponseDto>

    @POST("api/v1/borrow-orders")
    suspend fun createBorrowOrder(
        @Body request: CreateBorrowOrderRequestDto
    ): BorrowOrderResponseDto

    @GET("api/v1/borrow-orders/{orderId}")
    suspend fun getBorrowOrder(
        @Path("orderId") orderId: Long
    ): BorrowOrderResponseDto

    @POST("api/v1/borrow-orders/{orderId}/cancel")
    suspend fun cancelBorrowOrder(
        @Path("orderId") orderId: Long
    ): CancelBorrowOrderResponseDto

    @GET("api/v1/borrow-orders/books/{bookId}/current")
    suspend fun getCurrentBorrowOrder(
        @Path("bookId") bookId: Long
    ): CurrentBorrowOrderResponseDto

    @GET("api/v1/payments/borrow-orders/{orderId}")
    suspend fun getBorrowPayment(
        @Path("orderId") orderId: Long
    ): BorrowPaymentResponseDto
}
