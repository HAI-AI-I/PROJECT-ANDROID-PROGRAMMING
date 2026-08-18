package com.group_7.library_management.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }
    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val profile = UserProfile(
                    id = "0345115421",
                    name = "Le",
                    email = "nguyenvanhai@example.com",
                    phone = "0123456789",
                    joinDate = "15/08/2024",
                    borrowedBooksCount = 3,
                    totalBooksRead = 12
                )
                _uiState.update { it.copy(isLoading = false, userProfile = profile) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}