package com.group_7.library_management.data.repository

import com.group_7.library_management.data.local.dao.NotificationDAO
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.data.mapper.toNotificationEntity
import com.group_7.library_management.data.mapper.toNotificationItem
import com.group_7.library_management.data.remote.api.NotificationApi
import com.group_7.library_management.ui.home.NotificationItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDAO,
    private val notificationApi: NotificationApi,
    private val checkLogin: CheckLogin
) {
    companion object {
        private const val MAX_CACHED_NOTIFICATIONS = 40
    }

    fun getNotifications(): Flow<List<NotificationItem>> {
        val userId = currentUserId() ?: return flowOf(emptyList())
        return notificationDao.getAllNotifications(userId).map { entities ->
            entities.map { it.toNotificationItem() }
        }
    }

    suspend fun getCachedNotifications(): List<NotificationItem> {
        val userId = currentUserId() ?: return emptyList()
        return notificationDao.getAllNotificationsOnce(userId)
            .map { it.toNotificationItem() }
    }

    suspend fun getUnreadCount(): Long {
        requireCurrentUserId()
        return notificationApi.getUnreadCount().unreadCount
    }

    suspend fun loadNotificationPage(page: Int, limit: Int = 20): List<NotificationItem> {
        val userId = requireCurrentUserId()
        val notifications = notificationApi.getNotifications(page, limit)
            .map { it.toNotificationEntity(userId) }
        notificationDao.cacheNotificationPage(
            userId = userId,
            page = page,
            notifications = notifications,
            maxCount = MAX_CACHED_NOTIFICATIONS
        )
        return notifications.map { it.toNotificationItem() }
    }

    suspend fun markAllAsRead() {
        val userId = requireCurrentUserId()
        notificationApi.markAllAsRead()
        notificationDao.markAllAsRead(userId)
    }

    suspend fun markAsRead(id: String): NotificationItem {
        val userId = requireCurrentUserId()
        val notification = notificationApi.markAsRead(requireNotificationId(id))
            .toNotificationEntity(userId)
        notificationDao.cacheNotification(userId, notification, MAX_CACHED_NOTIFICATIONS)
        return notification.toNotificationItem()
    }

    suspend fun markAsClicked(id: String): NotificationItem {
        val userId = requireCurrentUserId()
        val notification = notificationApi.markAsClicked(requireNotificationId(id))
            .toNotificationEntity(userId)
        notificationDao.cacheNotification(userId, notification, MAX_CACHED_NOTIFICATIONS)
        return notification.toNotificationItem()
    }

    suspend fun deleteNotification(id: String) {
        val userId = requireCurrentUserId()
        val notificationId = requireNotificationId(id)
        val response = notificationApi.deleteNotification(notificationId)
        if (!response.isSuccessful) throw HttpException(response)
        notificationDao.deleteNotification(userId, notificationId)
    }

    private fun currentUserId(): Long? = checkLogin.getSavedUserId()?.toLongOrNull()

    private fun requireCurrentUserId(): Long =
        requireNotNull(currentUserId()) { "Không tìm thấy người dùng đang đăng nhập." }

    private fun requireNotificationId(id: String): Long =
        requireNotNull(id.toLongOrNull()) { "Mã thông báo không hợp lệ." }
}
