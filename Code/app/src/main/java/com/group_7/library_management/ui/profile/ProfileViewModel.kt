package com.group_7.library_management.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.data.repository.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext context: Context
) : ViewModel() {
    private val checkLogin = CheckLogin(context)
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val savedUserId = checkLogin.getSavedUserId()?.toLongOrNull()
                val userEntity = if (savedUserId != null) {
                    userRepository.getUserById(savedUserId) ?: userRepository.getLatestUser()
                } else {
                    userRepository.getLatestUser()
                }

                if (userEntity != null) {
                    val profile = UserProfile(
                        id = userEntity.id.toString(),
                        name = userEntity.name,
                        email = userEntity.email,
                        phone = userEntity.phone,
                        joinDate = userEntity.joinDate,
                        borrowedBooksCount = 0,
                        totalBooksRead = 0
                    )
                    _uiState.update { it.copy(isLoading = false, userProfile = profile) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Không tìm thấy thông tin người dùng."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun updateUserProfile(name: String, email: String, phone: String): ProfileFieldErrors {
        val errors = ProfileFieldErrors(
            nameError = if (!ValidationUtils.isValidName(name)) "Họ tên phải có ít nhất 2 ký tự" else null,
            emailError = if (!ValidationUtils.isValidEmail(email)) "Email không hợp lệ" else null,
            phoneError = if (!ValidationUtils.isValidPhone(phone)) "Số điện thoại không hợp lệ (tối thiểu 10 số)" else null
        )

        if (errors.hasError) {
            return errors
        }

        val current = _uiState.value.userProfile
        if (current == null) {
            _uiState.update { it.copy(errorMessage = "Không tìm thấy thông tin người dùng") }
            return errors
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, saveSuccess = false) }
            // Giả lập độ trễ khi lưu vào cơ sở dữ liệu / máy chủ
            delay(500)
            _uiState.update {
                it.copy(
                    isSaving = false,
                    saveSuccess = true,
                    userProfile = current.copy(
                        name = name.trim(),
                        email = email.trim(),
                        phone = phone.trim()
                    )
                )
            }
        }

        return errors
    }

    fun consumeSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class ProfileFieldErrors(
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null
) {
    val hasError: Boolean
        get() = nameError != null || emailError != null || phoneError != null
}
