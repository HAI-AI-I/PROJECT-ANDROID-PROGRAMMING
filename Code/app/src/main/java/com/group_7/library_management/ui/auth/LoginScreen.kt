package com.group_7.library_management.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.group_7.library_management.components.CreateLogoIcon
import com.group_7.library_management.components.CreateLogoTitle
import com.group_7.library_management.components.CustomTextField
import com.group_7.library_management.components.PasswordTextField
import com.group_7.library_management.components.auth.AuthButton
import com.group_7.library_management.components.auth.AuthFooter
import com.group_7.library_management.components.auth.AuthHeader
import com.group_7.library_management.ui.theme.LibrarySpacing

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: (Long) -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by authViewModel.uiState.collectAsState()

    var textEmailorPassword by remember { mutableStateOf("") }
    var textPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf("") }

    val displayError = localError.ifEmpty { uiState.errorMessage ?: "" }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = LibrarySpacing.Large, vertical = LibrarySpacing.Huge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 40.dp, bottom = 40.dp)
            ) {
                CreateLogoIcon()
                Spacer(modifier = Modifier.width(12.dp))
                CreateLogoTitle()
            }
            AuthHeader(
                title = "Chào mừng trở lại",
                subtitle = "Đăng nhập để tiếp tục sử dụng thư viện"
            )
            CustomTextField(
                value = textEmailorPassword,
                onValueChange = {
                    textEmailorPassword = it
                    localError = ""
                    authViewModel.clearError()
                },
                label = "Email/Số điện thoại",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))
            PasswordTextField(
                password = textPassword,
                onPasswordChange = {
                    textPassword = it
                    localError = ""
                    authViewModel.clearError()
                },
                label = "Mật khẩu",
                isPasswordVisible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible },
            )

            if (displayError.isNotEmpty()) {
                Text(
                    text = displayError,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = LibrarySpacing.Small)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(onClick = onNavigateToForgotPassword) {
                    Text(
                        text = "Quên mật khẩu?",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

            AuthButton(
                text = if (uiState.isLoading) "ĐANG XỬ LÝ..." else "ĐĂNG NHẬP",
                onClick = {
                    when {
                        textEmailorPassword.isBlank() -> {
                            localError = "Vui lòng nhập Email hoặc Số điện thoại"
                        }
                        textPassword.isBlank() -> {
                            localError = "Vui lòng nhập mật khẩu"
                        }
                        else -> {
                            localError = ""
                            authViewModel.login(
                                emailOrPhone = textEmailorPassword,
                                password = textPassword,
                                onSuccess = { userId ->
                                    onLoginSuccess(userId)
                                }
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            AuthFooter(
                descriptionText = "Chưa có tài khoản",
                actionText = "Tạo tài khoản",
                onActionClick = onNavigateToRegister
            )
        }
    }
}
