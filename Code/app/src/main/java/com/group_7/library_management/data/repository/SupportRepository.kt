package com.group_7.library_management.data.repository

import com.group_7.library_management.data.local.dao.SupportRequestDao
import com.group_7.library_management.data.local.entity.SupportRequestEntity
import com.group_7.library_management.ui.support.SupportRequestItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupportRepository @Inject constructor(
    private val supportRequestDao: SupportRequestDao
) {
    fun getAllRequests(): Flow<List<SupportRequestItem>> {
        return supportRequestDao.getAllRequests().map { entities ->
            entities.map { entity ->
                SupportRequestItem(
                    id = entity.id,
                    bookTitle = entity.bookTitle,
                    problemType = entity.problemType,
                    description = entity.description,
                    date = entity.date,
                    status = entity.status
                )
            }
        }
    }

    suspend fun insertRequests(requests: List<SupportRequestEntity>) {
        supportRequestDao.insertRequests(requests)
    }

    suspend fun createRequest(item: SupportRequestItem, isOnline: Boolean = true) {
        val entity = SupportRequestEntity(
            id = item.id,
            bookTitle = item.bookTitle,
            problemType = item.problemType,
            description = item.description,
            date = item.date,
            status = item.status,
            isSynced = isOnline
        )
        // 1. Save to Room / SQLite (Offline-first / Local backup cache)
        supportRequestDao.insertRequest(entity)

        // 2. If online, push to RestAPI server backend
        if (isOnline) {
            // TODO: apiService.sendSupportRequest(entity)
        }
    }
}
