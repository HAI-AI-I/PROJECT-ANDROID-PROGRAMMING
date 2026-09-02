package com.group_7.library_management.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserRootUiState(
    val currentUser: User? = null,
    val unreadNotificationCount: Int = 0,
    val isLoading: Boolean = false
)

@HiltViewModel
class UserRootViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(UserRootUiState())
    val uiState: StateFlow<UserRootUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUserInfo()
    }

    private fun loadCurrentUserInfo() {
        viewModelScope.launch {
            // Lấy thông tin user từ Session/Database/DataStore
            // _uiState.update { it.copy(currentUser = ...) }
        }
    }
}