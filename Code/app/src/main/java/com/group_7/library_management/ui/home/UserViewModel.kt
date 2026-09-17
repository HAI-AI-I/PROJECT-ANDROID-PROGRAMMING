package com.group_7.library_management.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.components.AppSnackbarController
import com.group_7.library_management.data.network.NetworkMonitor
import com.group_7.library_management.data.repository.NotificationRepository
import com.group_7.library_management.data.repository.UserRepository
import com.group_7.library_management.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserRootUiState(
    val currentUser: User? = null,
    val unreadNotificationCount: Int = 0,
    val isBiometricEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isLoggingOut: Boolean = false
)

@HiltViewModel
class UserRootViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val networkMonitor: NetworkMonitor,
    val snackbarController: AppSnackbarController,
    @ApplicationContext context: Context
) : ViewModel() {
    private val checkLogin = CheckLogin(context)
    private val _uiState = MutableStateFlow(UserRootUiState())
    val uiState: StateFlow<UserRootUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(isBiometricEnabled = checkLogin.isBiometricEnabled()) }
        loadCurrentUserInfo()
        observeUnreadNotifications()
        observeNetworkConnection()
    }

    fun toggleBiometricSetting(enabled: Boolean) {
        checkLogin.setBiometricEnabled(enabled)
        _uiState.update { it.copy(isBiometricEnabled = enabled) }
    }

    fun logout(onSuccess: () -> Unit) {
        if (_uiState.value.isLoggingOut) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoggingOut = true)
            }
            userRepository.logout()
                .onSuccess { onSuccess() }
                .onFailure { exception ->
                    snackbarController.show(exception.message ?: "Không thể đăng xuất.")
                }
            _uiState.update { it.copy(isLoggingOut = false) }
        }
    }

    private fun loadCurrentUserInfo() {
        viewModelScope.launch {
            val savedUserId = checkLogin.getSavedUserId()?.toLongOrNull()
            val userEntity = if (savedUserId != null) {
                userRepository.getUserById(savedUserId) ?: userRepository.getLatestUser()
            } else {
                userRepository.getLatestUser()
            }

            if (userEntity != null) {
                _uiState.update {
                    it.copy(
                        currentUser = User(
                            id = userEntity.id.toString(),
                            name = userEntity.name,
                            phone = userEntity.phone,
                            qrCodeData = "USER_${userEntity.id}"
                        )
                    )
                }
            }
        }
    }

    private fun observeUnreadNotifications() {
        viewModelScope.launch {
            notificationRepository.getNotifications().collect { notifications ->
                val cachedUnreadCount = notifications.count { !it.isRead }
                _uiState.update {
                    it.copy(unreadNotificationCount = cachedUnreadCount)
                }

                runCatching {
                    notificationRepository.getUnreadCount()
                }.onSuccess { serverUnreadCount ->
                    _uiState.update {
                        it.copy(
                            unreadNotificationCount = serverUnreadCount
                                .coerceAtMost(Int.MAX_VALUE.toLong())
                                .toInt()
                        )
                    }
                }
            }
        }
    }

    private fun observeNetworkConnection() {
        viewModelScope.launch {
            var wasDisconnected = !networkMonitor.isConnected.value
            networkMonitor.isConnected.collect { isConnected ->
                    if (isConnected && wasDisconnected) {
                        snackbarController.show("Đã có kết nối mạng trở lại.")
                    }
                    wasDisconnected = !isConnected
                }
        }
    }
}
