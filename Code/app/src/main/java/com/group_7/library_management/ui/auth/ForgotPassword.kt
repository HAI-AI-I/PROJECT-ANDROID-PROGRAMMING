package com.group_7.library_management.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.group_7.library_management.components.screens.VerificationCodeScreen
import com.group_7.library_management.data.remote.dto.RegistrationVerificationMethod

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onCodeSent: (String, RegistrationVerificationMethod) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    VerificationCodeScreen(
        title = "Khôi phục mật khẩu",
        subtitle = "Chọn phương thức nhận mã, sau đó nhập email hoặc số điện thoại đã đăng ký.",
        onNavigateBack = onNavigateBack,
        onSubmit = { identifier, method ->
            viewModel.requestForgotPasswordCode(identifier, method) { requestId ->
                onCodeSent(requestId, method)
            }
        },
        isLoading = state.isLoading,
        errorMessage = state.errorMessage
    )
}

@Composable
fun ChangePasswordStartScreen(
    onNavigateBack: () -> Unit,
    onCodeSent: (String, RegistrationVerificationMethod) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    VerificationCodeScreen(
        title = "Thay đổi mật khẩu",
        subtitle = "Chọn nơi nhận mã xác nhận cho tài khoản đang đăng nhập.",
        onNavigateBack = onNavigateBack,
        onSubmit = { _, method ->
            viewModel.requestChangePasswordCode(method) { requestId ->
                onCodeSent(requestId, method)
            }
        },
        requiresIdentifier = false,
        isLoading = state.isLoading,
        errorMessage = state.errorMessage
    )
}
