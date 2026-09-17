package com.group_7.library_management.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.repository.NotificationRepository
import com.group_7.library_management.components.AppSnackbarController
import com.group_7.library_management.data.network.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class NotificationUiState(
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val networkMonitor: NetworkMonitor,
    private val snackbarController: AppSnackbarController
) : ViewModel() {
    companion object {
        private const val PAGE_SIZE = 20
    }

    private var nextPage = 0
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val _notificationsFlow = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notificationsFlow: StateFlow<List<NotificationItem>> = _notificationsFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _notificationsFlow.value = notificationRepository.getCachedNotifications()
            refreshNotifications()
        }
    }

    fun refreshNotifications() {
        if (_uiState.value.isRefreshing || _uiState.value.isLoadingMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            runCatching {
                notificationRepository.loadNotificationPage(page = 0, limit = PAGE_SIZE)
            }.onSuccess { notifications ->
                _notificationsFlow.value = notifications
                nextPage = 1
                _uiState.update { it.copy(hasMore = notifications.size == PAGE_SIZE) }
            }.onFailure(::showError)
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isRefreshing || state.isLoadingMore || !state.hasMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true, errorMessage = null) }
            runCatching {
                notificationRepository.loadNotificationPage(nextPage, PAGE_SIZE)
            }.onSuccess { notifications ->
                val knownIds = _notificationsFlow.value.asSequence().map { it.id }.toHashSet()
                _notificationsFlow.update { current ->
                    current + notifications.filterNot { it.id in knownIds }
                }
                nextPage += 1
                _uiState.update { it.copy(hasMore = notifications.size == PAGE_SIZE) }
            }.onFailure(::showError)
            _uiState.update { it.copy(isLoadingMore = false) }
        }
    }

    fun markAllAsRead() {
        if (!ensureNetwork()) return
        viewModelScope.launch {
            runCatching { notificationRepository.markAllAsRead() }
                .onSuccess {
                    _notificationsFlow.update { notifications ->
                        notifications.map { it.copy(isRead = true) }
                    }
                }
                .onFailure(::showCrudError)
        }
    }

    fun markAsClicked(id: String) {
        if (!ensureNetwork()) return
        viewModelScope.launch {
            runCatching { notificationRepository.markAsClicked(id) }
                .onSuccess { updatedNotification ->
                    _notificationsFlow.update { notifications ->
                        notifications.map {
                            if (it.id == updatedNotification.id) updatedNotification else it
                        }
                    }
                }
                .onFailure(::showCrudError)
        }
    }

    fun deleteNotification(id: String) {
        if (!ensureNetwork()) return
        viewModelScope.launch {
            runCatching { notificationRepository.deleteNotification(id) }
                .onSuccess {
                    _notificationsFlow.update { notifications ->
                        notifications.filterNot { it.id == id }
                    }
                }
                .onFailure(::showCrudError)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun showError(throwable: Throwable) {
        val message = when (throwable) {
            is HttpException -> if (throwable.code() == 401 || throwable.code() == 403) {
                "Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại."
            } else {
                "Máy chủ trả về lỗi HTTP ${throwable.code()}."
            }
            is IOException -> "Không thể kết nối đến máy chủ..."
            else -> throwable.message ?: "Không thể xử lý thông báo."
        }
        _uiState.update { it.copy(errorMessage = message) }
    }

    private fun ensureNetwork(): Boolean {
        if (networkMonitor.isConnected.value) return true
        showOfflineSnackbar()
        return false
    }

    private fun showCrudError(throwable: Throwable) {
        if (throwable is IOException) {
            showOfflineSnackbar()
        } else {
            showError(throwable)
        }
    }

    private fun showOfflineSnackbar() {
        snackbarController.show("Không có kết nối mạng.")
    }
}
