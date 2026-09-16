package com.group_7.library_management.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val avatarUri: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val checkLogin: CheckLogin
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private var currentUserId: Long = -1

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val userIdString = checkLogin.getUserId() ?: return@launch
            currentUserId = userIdString.toLongOrNull() ?: return@launch

            val user = userRepository.getUserById(currentUserId)
            user?.let {
                _uiState.update { state ->
                    state.copy(
                        name = it.name,
                        email = it.email,
                        phone = it.phone,
                        avatarUri = it.avatarUri
                    )
                }
            }
        }
    }

    fun onAvatarChange(newUri: String) {
        _uiState.update { it.copy(avatarUri = newUri) }
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }

    fun onPhoneChange(newPhone: String) {
        _uiState.update { it.copy(phone = newPhone) }
    }

    fun updateProfile() {
        if (currentUserId == -1L) return

        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.name.isBlank() || currentState.email.isBlank() || currentState.phone.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Vui lòng điền đầy đủ thông tin") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = userRepository.updateUserProfile(
                userId = currentUserId,
                name = currentState.name,
                email = currentState.email,
                phone = currentState.phone,
                avatarUri = currentState.avatarUri
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
