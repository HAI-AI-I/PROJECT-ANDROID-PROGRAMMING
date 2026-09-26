package com.group_7.library_management.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import com.group_7.library_management.data.local.entity.UserEntity
import com.group_7.library_management.data.repository.UserRepository
import com.group_7.library_management.data.repository.BorrowRepository
import com.group_7.library_management.models.UserBorrowSummary
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    private val borrowRepository: BorrowRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    private var loadProfileJob: Job? = null
    private var saveProfileJob: Job? = null

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        if (loadProfileJob?.isActive == true) return
        loadProfileJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val userResult = userRepository.getCurrentUserProfile()
            if (userResult.isFailure) {
                val error = userResult.exceptionOrNull()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error?.message ?: "Không thể tải thông tin hồ sơ."
                    )
                }
                return@launch
            }

            runCatching { borrowRepository.getBorrowSummary().first() }
                .onSuccess { summary ->
                    val userEntity = userResult.getOrThrow()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userProfile = userEntity.toProfile(summary),
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Không thể tải thông tin hồ sơ."
                        )
                    }
                }
        }
    }

    private fun UserEntity.toProfile(summary: UserBorrowSummary? = null) = UserProfile(
        id = id.toString(),
        name = name,
        email = email,
        phone = phone,
        joinDate = joinDate,
        borrowedBooksCount = summary?.let {
            it.borrowingCount + it.dueSoonCount + it.overdueCount
        } ?: 0,
        totalBorrowedCount = summary?.let {
            it.borrowingCount + it.dueSoonCount + it.overdueCount + it.returnedCount
        } ?: 0
    )

    fun updateUserProfile(name: String, email: String, phone: String): ProfileFieldErrors {
        val errors = ProfileFieldErrors(
            nameError = if (!ValidationUtils.isValidName(name)) "Họ tên phải có ít nhất 2 ký tự" else null,
            emailError = if (!ValidationUtils.isValidEmail(email)) "Email không hợp lệ" else null,
            phoneError = if (!ValidationUtils.isValidPhone(phone)) "Số điện thoại không hợp lệ (tối thiểu 10 số)" else null
        )

        if (errors.hasError) {
            return errors
        }

        if (saveProfileJob?.isActive == true) {
            return errors
        }

        val current = _uiState.value.userProfile
        if (current == null) {
            _uiState.update { it.copy(errorMessage = "Không tìm thấy thông tin người dùng") }
            return errors
        }

        saveProfileJob = viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, saveSuccess = false) }
            userRepository.updateCurrentUserProfile(name, email, phone)
                .onSuccess { updatedUser ->
                    _uiState.update {
                        val currentProfile = it.userProfile
                        it.copy(
                            isSaving = false,
                            saveSuccess = true,
                            userProfile = updatedUser.toProfile().copy(
                                borrowedBooksCount = currentProfile?.borrowedBooksCount ?: 0,
                                totalBorrowedCount = currentProfile?.totalBorrowedCount ?: 0
                            ),
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            saveSuccess = false,
                            errorMessage = error.message ?: "Không thể cập nhật hồ sơ."
                        )
                    }
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
