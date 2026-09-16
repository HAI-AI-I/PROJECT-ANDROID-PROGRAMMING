package com.group_7.library_management.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.group_7.library_management.components.PasswordTextField
import com.group_7.library_management.ui.theme.LibrarySpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    viewModel: ChangePasswordViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSaveSuccess()
            viewModel.resetSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thay đổi mật khẩu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(LibrarySpacing.Medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium)
        ) {
            Text(
                text = "Mật khẩu mới phải có ít nhất 6 ký tự.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            PasswordTextField(
                password = uiState.oldPassword,
                onPasswordChange = { viewModel.onOldPasswordChange(it) },
                label = "Mật khẩu cũ",
                isPasswordVisible = oldPasswordVisible,
                onToggleVisibility = { oldPasswordVisible = !oldPasswordVisible }
            )

            PasswordTextField(
                password = uiState.newPassword,
                onPasswordChange = { viewModel.onNewPasswordChange(it) },
                label = "Mật khẩu mới",
                isPasswordVisible = newPasswordVisible,
                onToggleVisibility = { newPasswordVisible = !newPasswordVisible }
            )

            PasswordTextField(
                password = uiState.confirmPassword,
                onPasswordChange = { viewModel.onConfirmPasswordChange(it) },
                label = "Xác nhận mật khẩu mới",
                isPasswordVisible = confirmPasswordVisible,
                onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible }
            )

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.changePassword() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !uiState.isLoading,
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Cập nhật mật khẩu", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
