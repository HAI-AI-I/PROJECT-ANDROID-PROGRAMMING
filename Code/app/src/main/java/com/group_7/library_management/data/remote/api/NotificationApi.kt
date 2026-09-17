package com.group_7.library_management.data.remote.api

import com.group_7.library_management.data.remote.dto.NotificationReadAllResponseDto
import com.group_7.library_management.data.remote.dto.NotificationResponseDto
import com.group_7.library_management.data.remote.dto.NotificationUnreadCountResponseDto
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApi {
    @GET("api/v1/notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 20
    ): List<NotificationResponseDto>

    @GET("api/v1/notifications/unread-count")
    suspend fun getUnreadCount(): NotificationUnreadCountResponseDto

    @PATCH("api/v1/notifications/{notificationId}/read")
    suspend fun markAsRead(
        @Path("notificationId") notificationId: Long
    ): NotificationResponseDto

    @PATCH("api/v1/notifications/read-all")
    suspend fun markAllAsRead(): NotificationReadAllResponseDto

    @PATCH("api/v1/notifications/{notificationId}/click")
    suspend fun markAsClicked(
        @Path("notificationId") notificationId: Long
    ): NotificationResponseDto

    @DELETE("api/v1/notifications/{notificationId}")
    suspend fun deleteNotification(
        @Path("notificationId") notificationId: Long
    ): Response<Unit>
}
