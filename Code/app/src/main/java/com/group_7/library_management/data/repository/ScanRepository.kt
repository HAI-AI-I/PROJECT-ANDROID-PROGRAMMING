package com.group_7.library_management.data.repository

import com.group_7.library_management.data.remote.api.ScanApi
import javax.inject.Inject

enum class ScanDestinationType {
    BOOK,
    BORROW_ORDER
}

data class ScanDestination(
    val type: ScanDestinationType,
    val targetId: Long
)

class ScanRepository @Inject constructor(
    private val scanApi: ScanApi
) {
    suspend fun resolve(rawCode: String): ScanDestination {
        val response = scanApi.resolve(rawCode.trim())
        return ScanDestination(
            type = ScanDestinationType.valueOf(response.type),
            targetId = response.targetId
        )
    }
}
