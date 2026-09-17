package com.group_7.library_management.data.remote.dto

data class NotificationResponseDto(
    val id: Long,
    val title: String,
    val message: String,
    val type: String,
    val bookId: Long?,
    val isRead: Boolean,
    val isClicked: Boolean,
    val createdAt: String,
    val readAt: String?,
    val clickedAt: String?
)

data class NotificationReadAllResponseDto(
    val updatedCount: Int
)

data class NotificationUnreadCountResponseDto(
    val unreadCount: Long
)
