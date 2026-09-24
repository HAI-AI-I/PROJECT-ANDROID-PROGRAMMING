package com.group_7.library_management.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.repository.UserRepository
import com.group_7.library_management.data.remote.dto.RegistrationVerificationMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccess: Boolean = false,
    val loggedInUserId: Long? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        method: RegistrationVerificationMethod,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = userRepository.sendRegistrationCode(
                name, email, phone, password, method
            )
            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(isLoading = false, isRegisterSuccess = true)
                onSuccess(response.registrationId)
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Đăng ký thất bại"
                )
            }
        }
    }

    fun verifyRegistrationCode(
        registrationId: String,
        code: String,
        method: RegistrationVerificationMethod,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = userRepository.verifyRegistrationCode(registrationId, code, method)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Xác nhận mã thất bại"
                )
            }
        }
    }

    fun resendRegistrationCode(
        registrationId: String,
        method: RegistrationVerificationMethod
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = userRepository.resendRegistrationCode(registrationId, method)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Không thể gửi lại mã xác nhận"
                )
            }
        }
    }

    fun login(
        emailOrPhone: String,
        password: String,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = userRepository.loginUser(emailOrPhone, password)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(isLoading = false, loggedInUserId = user.id)
                onSuccess(user.id)
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Đăng nhập thất bại"
                )
            }
        }
    }

    fun requestForgotPasswordCode(
        identifier: String,
        method: RegistrationVerificationMethod,
        onSuccess: (String) -> Unit
    ) {
        executePasswordRequest(
            request = { userRepository.sendForgotPasswordCode(identifier, method) },
            onSuccess = onSuccess
        )
    }

    fun requestChangePasswordCode(
        method: RegistrationVerificationMethod,
        onSuccess: (String) -> Unit
    ) {
        executePasswordRequest(
            request = { userRepository.sendChangePasswordCode(method) },
            onSuccess = onSuccess
        )
    }

    private fun executePasswordRequest(
        request: suspend () -> Result<com.group_7.library_management.data.remote.dto.PasswordCodeResponseDto>,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            request().onSuccess { response ->
                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess(response.requestId)
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Không thể gửi mã xác nhận"
                )
            }
        }
    }

    fun verifyPasswordCode(
        requestId: String,
        code: String,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            userRepository.verifyPasswordCode(requestId, code)
                .onSuccess { resetToken ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess(resetToken)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Mã xác nhận không hợp lệ"
                    )
                }
        }
    }

    fun resendPasswordCode(requestId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            userRepository.resendPasswordCode(requestId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Không thể gửi lại mã xác nhận"
                    )
                }
        }
    }

    fun resetPassword(
        resetToken: String,
        newPassword: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            userRepository.resetPassword(resetToken, newPassword)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Không thể thay đổi mật khẩu"
                    )
                }
        }
    }

    fun loginWithBiometrics(
        savedUserId: String? = null,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val userIdLong = savedUserId?.toLongOrNull()
            val user = if (userIdLong != null) {
                userRepository.getUserById(userIdLong) ?: userRepository.getLatestUser()
            } else {
                userRepository.getLatestUser()
            }

            if (user != null) {
                _uiState.value = _uiState.value.copy(isLoading = false, loggedInUserId = user.id)
                onSuccess(user.id)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Chưa có tài khoản nào được lưu. Vui lòng đăng ký/đăng nhập trước."
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
