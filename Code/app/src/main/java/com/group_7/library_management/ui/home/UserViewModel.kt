package com.group_7.library_management.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.local.preferences.CheckLogin
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
    val isLoading: Boolean = false
)

@HiltViewModel
class UserRootViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    @ApplicationContext context: Context
) : ViewModel() {
    private val checkLogin = CheckLogin(context)
    private val _uiState = MutableStateFlow(UserRootUiState())
    val uiState: StateFlow<UserRootUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(isBiometricEnabled = checkLogin.isBiometricEnabled()) }
        loadCurrentUserInfo()
        observeUnreadNotifications()
    }

    fun toggleBiometricSetting(enabled: Boolean) {
        checkLogin.setBiometricEnabled(enabled)
        _uiState.update { it.copy(isBiometricEnabled = enabled) }
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
                val unreadCount = notifications.count { !it.isRead }
                _uiState.update { it.copy(unreadNotificationCount = unreadCount) }
            }
        }
    }
}
