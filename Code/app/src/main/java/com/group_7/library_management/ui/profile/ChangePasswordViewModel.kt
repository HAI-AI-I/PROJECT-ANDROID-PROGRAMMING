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

data class ChangePasswordUiState(
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val checkLogin: CheckLogin
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun onOldPasswordChange(value: String) {
        _uiState.update { it.copy(oldPassword = value) }
    }

    fun onNewPasswordChange(value: String) {
        _uiState.update { it.copy(newPassword = value) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value) }
    }

    fun changePassword() {
        val currentState = _uiState.value
        if (currentState.oldPassword.isBlank() || currentState.newPassword.isBlank() || currentState.confirmPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng điền đầy đủ thông tin") }
            return
        }

        if (currentState.newPassword != currentState.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Mật khẩu xác nhận không khớp") }
            return
        }

        if (currentState.newPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = "Mật khẩu mới phải có ít nhất 6 ký tự") }
            return
        }

        viewModelScope.launch {
            val userIdString = checkLogin.getUserId() ?: return@launch
            val userId = userIdString.toLongOrNull() ?: return@launch

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = userRepository.changePassword(
                userId = userId,
                oldPass = currentState.oldPassword,
                newPass = currentState.newPassword
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
