package com.group_7.library_management.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.group_7.library_management.components.screens.ConfirmCodeScreen
import com.group_7.library_management.data.remote.dto.RegistrationVerificationMethod

@Composable
fun ConfirmCodeRegisAuthScreen(
    registrationId: String,
    method: RegistrationVerificationMethod,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by authViewModel.uiState.collectAsState()

    ConfirmCodeScreen(
        onNavigateBack = onNavigateBack,
        onSubmit = { code ->
            authViewModel.verifyRegistrationCode(
                registrationId = registrationId,
                code = code,
                method = method,
                onSuccess = onSuccess
            )
        },
        onResendCode = {
            authViewModel.resendRegistrationCode(registrationId, method)
        },
        isEmail = method == RegistrationVerificationMethod.EMAIL,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage
    )
}
