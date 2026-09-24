package com.group_7.library_management.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.group_7.library_management.components.screens.ConfirmCodeScreen
import com.group_7.library_management.data.remote.dto.RegistrationVerificationMethod

@Composable
fun ConfirmCodeResetAuthScreen(
    requestId: String,
    method: RegistrationVerificationMethod,
    onNavigateBack: () -> Unit,
    onVerified: (String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ConfirmCodeScreen(
        onNavigateBack = onNavigateBack,
        onSubmit = { code ->
            viewModel.verifyPasswordCode(requestId, code, onVerified)
        },
        onResendCode = { viewModel.resendPasswordCode(requestId) },
        isEmail = method == RegistrationVerificationMethod.EMAIL,
        isLoading = state.isLoading,
        errorMessage = state.errorMessage
    )
}
