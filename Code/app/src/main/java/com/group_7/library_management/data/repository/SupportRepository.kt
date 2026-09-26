package com.group_7.library_management.data.repository

import com.group_7.library_management.data.local.dao.SupportRequestDao
import com.group_7.library_management.data.local.entity.SupportRequestEntity
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.data.remote.api.SupportApi
import com.group_7.library_management.data.remote.dto.CreateSupportRequestDto
import com.group_7.library_management.data.remote.dto.SupportRequestResponseDto
import com.group_7.library_management.ui.support.SupportRequestItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupportRepository @Inject constructor(
    private val supportRequestDao: SupportRequestDao,
    private val supportApi: SupportApi,
    private val checkLogin: CheckLogin
) {
    fun observeRequests(): Flow<List<SupportRequestItem>> {
        val userId = currentUserId() ?: return flowOf(emptyList())
        return supportRequestDao.observeRequests(userId).map { requests ->
            requests.map { it.toItem() }
        }
    }

    suspend fun refreshRequests() {
        val userId = requireCurrentUserId()
        val requests = supportApi.getMyRequests().map { it.toEntity(userId) }
        supportRequestDao.replaceForUser(userId, requests)
    }

    suspend fun createRequest(
        bookId: Long?,
        subject: String,
        message: String
    ): SupportRequestItem {
        val userId = requireCurrentUserId()
        val response = supportApi.createRequest(
            CreateSupportRequestDto(
                bookId = bookId,
                subject = subject,
                message = message
            )
        )
        val entity = response.toEntity(userId)
        supportRequestDao.upsert(entity)
        return entity.toItem()
    }

    private fun SupportRequestResponseDto.toEntity(currentUserId: Long) = SupportRequestEntity(
        id = id,
        userId = currentUserId,
        bookId = bookId,
        bookTitle = bookTitle,
        subject = subject,
        message = message,
        status = status,
        adminReply = adminReply,
        repliedAt = repliedAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun SupportRequestEntity.toItem() = SupportRequestItem(
        id = id.toString(),
        bookTitle = bookTitle,
        problemType = subject,
        description = message,
        date = createdAt.toDisplayDate(),
        status = status,
        adminReply = adminReply,
        repliedDate = repliedAt?.toDisplayDate()
    )

    private fun String.toDisplayDate(): String {
        val dateTime = runCatching { OffsetDateTime.parse(this) }.getOrNull()
        return dateTime
            ?.atZoneSameInstant(ZoneId.systemDefault())
            ?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            ?: this
    }

    private fun currentUserId(): Long? = checkLogin.getSavedUserId()?.toLongOrNull()

    private fun requireCurrentUserId(): Long = requireNotNull(currentUserId()) {
        "Không tìm thấy người dùng đang đăng nhập."
    }
}
