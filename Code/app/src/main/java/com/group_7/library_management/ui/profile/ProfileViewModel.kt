package com.group_7.library_management.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.data.repository.BorrowRepository
import com.group_7.library_management.data.repository.UserRepository
import com.group_7.library_management.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val errorMessage: String? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val borrowRepository: BorrowRepository,
    private val checkLogin: CheckLogin
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val userIdString = checkLogin.getUserId()
            if (userIdString == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Bạn chưa đăng nhập hoặc phiên làm việc hết hạn") }
                return@launch
            }

            val userId = userIdString.toLongOrNull()
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "ID người dùng không hợp lệ") }
                return@launch
            }

            try {
                val userEntity = userRepository.getUserById(userId)
                val borrowedCount = borrowRepository.getBorrowedCount(userId)
                val totalReadCount = borrowRepository.getTotalBooksReadCount(userId)

                if (userEntity != null) {
                    val profile = UserProfile(
                        id = userEntity.id.toString(),
                        name = userEntity.name,
                        email = userEntity.email,
                        phone = userEntity.phone,
                        joinDate = userEntity.joinDate,
                        avatarUrl = userEntity.avatarUri ?: "",
                        borrowedBooksCount = borrowedCount,
                        totalBooksRead = totalReadCount
                    )
                    _uiState.update { it.copy(isLoading = false, userProfile = profile) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Không tìm thấy thông tin người dùng. Vui lòng đăng xuất và đăng nhập lại.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onAvatarChange(uri: String) {
        val current = _uiState.value.userProfile ?: return
        _uiState.update { it.copy(userProfile = current.copy(avatarUrl = uri)) }
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

        val profile = _uiState.value.userProfile ?: return errors
        val userId = profile.id.toLongOrNull() ?: return errors

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, saveSuccess = false) }
            val result = userRepository.updateUserProfile(
                userId = userId,
                name = name.trim(),
                email = email.trim(),
                phone = phone.trim(),
                avatarUri = profile.avatarUrl
            )
            
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccess = true,
                        userProfile = profile.copy(
                            name = name.trim(),
                            email = email.trim(),
                            phone = phone.trim()
                        )
                    )
                }
            } else {
                _uiState.update { it.copy(isSaving = false, errorMessage = result.exceptionOrNull()?.message) }
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
        get() = (nameError != null || emailError != null || phoneError != null)
}
