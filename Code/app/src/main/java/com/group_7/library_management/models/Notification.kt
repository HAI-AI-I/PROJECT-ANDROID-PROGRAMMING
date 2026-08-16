package com.group_7.library_management.models

enum class NotificationType {
    WARNING, SUCCESS, ERROR, INFO
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType
)
