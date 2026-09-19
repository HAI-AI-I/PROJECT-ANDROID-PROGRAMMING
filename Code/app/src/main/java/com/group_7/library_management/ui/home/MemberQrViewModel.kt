package com.group_7.library_management.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.local.entity.UserEntity
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MemberQrUiState(
    val user: UserEntity? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class MemberQrViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val checkLogin: CheckLogin
) : ViewModel() {
    private val _uiState = MutableStateFlow(MemberQrUiState())
    val uiState: StateFlow<MemberQrUiState> = _uiState.asStateFlow()

    init { loadMember() }

    fun loadMember() {
        viewModelScope.launch {
            val userId = checkLogin.getSavedUserId()?.toLongOrNull()
            val user = userId?.let { userRepository.getUserById(it) } ?: userRepository.getLatestUser()
            _uiState.value = if (user == null) {
                MemberQrUiState(isLoading = false, errorMessage = "Không tìm thấy thông tin thành viên trên thiết bị.")
            } else {
                MemberQrUiState(user = user, isLoading = false)
            }
        }
    }
}
