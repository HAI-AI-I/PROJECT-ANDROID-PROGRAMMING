package com.group_7.library_management.data.mapper

import com.group_7.library_management.data.local.entity.NotificationEntity
import com.group_7.library_management.ui.home.NotificationItem
import com.group_7.library_management.ui.home.NotificationType

fun NotificationEntity.toNotificationItem(): NotificationItem {
    val notificationType = try {
        NotificationType.valueOf(type)
    } catch (e: Exception) {
        NotificationType.INFO
    }

    return NotificationItem(
        id = id.toString(),
        title = title,
        message = message,
        time = time,
        date = date,
        type = notificationType,
        isRead = isRead
    )
}

fun NotificationItem.toNotificationEntity(
    createdAt: Long = System.currentTimeMillis()
): NotificationEntity {
    return NotificationEntity(
        id = id.toLongOrNull(),
        title = title,
        message = message,
        time = time,
        date = date,
        type = type.name,
        isRead = isRead,
        createdAt = createdAt
    )
}
