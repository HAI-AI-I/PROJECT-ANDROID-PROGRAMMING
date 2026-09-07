package com.group_7.library_management.data.repository

import com.group_7.library_management.data.local.dao.NotificationDAO
import com.group_7.library_management.data.local.entity.NotificationEntity
import com.group_7.library_management.data.mapper.toNotificationItem
import com.group_7.library_management.ui.home.NotificationItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDAO
) {
    fun getNotifications(): Flow<List<NotificationItem>> {
        return notificationDao.getAllNotifications().map { entities ->
            entities.map { it.toNotificationItem() }
        }
    }

    suspend fun insertNotifications(notifications: List<NotificationEntity>) {
        notificationDao.insertNotifications(notifications)
    }

    suspend fun markAllAsRead() {
        notificationDao.markAllAsRead()
    }

    suspend fun markAsRead(id: String) {
        notificationDao.markAsRead(id)
    }

    suspend fun deleteNotification(id: String) {
        notificationDao.deleteNotification(id)
    }
}
