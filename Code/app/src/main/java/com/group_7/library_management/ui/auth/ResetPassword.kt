package com.group_7.library_management.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.group_7.library_management.components.screens.ChangePasswordScreen

@Composable
fun RestPassword(
    resetToken: String,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ChangePasswordScreen(
        onNavigateBack = onNavigateBack,
        onSubmit = { password ->
            viewModel.resetPassword(resetToken, password, onSuccess)
        },
        isLoading = state.isLoading,
        serverErrorMessage = state.errorMessage
    )
}
