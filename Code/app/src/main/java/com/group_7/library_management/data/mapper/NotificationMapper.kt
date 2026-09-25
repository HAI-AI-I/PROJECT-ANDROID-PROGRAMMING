package com.group_7.library_management.data.mapper

import com.group_7.library_management.data.local.entity.NotificationEntity
import com.group_7.library_management.data.remote.dto.NotificationResponseDto
import com.group_7.library_management.ui.home.NotificationActionType
import com.group_7.library_management.ui.home.NotificationItem
import com.group_7.library_management.ui.home.NotificationType
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun NotificationEntity.toNotificationItem(): NotificationItem {
    val notificationType = try {
        NotificationType.valueOf(type)
    } catch (e: Exception) {
        NotificationType.INFO
    }
    val notificationActionType = actionType.toNotificationActionType(bookId)

    return NotificationItem(
        id = id.toString(),
        title = title,
        message = message,
        time = time,
        date = date,
        type = notificationType,
        actionType = notificationActionType,
        targetId = targetId ?: bookId,
        isRead = isRead
    )
}

fun NotificationItem.toNotificationEntity(
    userId: Long,
    createdAt: Long = System.currentTimeMillis()
): NotificationEntity {
    return NotificationEntity(
        id = requireNotNull(id.toLongOrNull()),
        userId = userId,
        actionType = actionType.name,
        targetId = targetId,
        title = title,
        message = message,
        time = time,
        date = date,
        type = type.name,
        isRead = isRead,
        createdAt = createdAt
    )
}

fun NotificationResponseDto.toNotificationEntity(userId: Long): NotificationEntity {
    val dateTime = runCatching { OffsetDateTime.parse(createdAt) }.getOrNull()
    val localDateTime = dateTime?.atZoneSameInstant(ZoneId.systemDefault())

    return NotificationEntity(
        id = id,
        userId = userId,
        bookId = bookId,
        actionType = actionType.toNotificationActionType(bookId).name,
        targetId = targetId ?: bookId,
        title = title,
        message = message,
        time = localDateTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "",
        date = localDateTime?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: createdAt,
        type = type,
        isRead = isRead,
        isDeleted = false,
        createdAt = dateTime?.toInstant()?.toEpochMilli() ?: System.currentTimeMillis()
    )
}

private fun String?.toNotificationActionType(bookId: Long?): NotificationActionType {
    return runCatching {
        NotificationActionType.valueOf(this ?: "")
    }.getOrElse {
        if (bookId != null) NotificationActionType.BOOK_DETAIL else NotificationActionType.NONE
    }
}
