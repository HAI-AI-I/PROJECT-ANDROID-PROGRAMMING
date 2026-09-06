package com.group_7.library_management.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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
fun RegisterScreen(
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by authViewModel.uiState.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var sdt by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

    val displayError = errorMessage.ifEmpty { uiState.errorMessage ?: "" }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = LibrarySpacing.Large,
                    vertical = LibrarySpacing.Huge
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    top = LibrarySpacing.Medium,
                    bottom = LibrarySpacing.Large
                )
            ) {
                CreateLogoIcon()

                Spacer(
                    modifier = Modifier.width(LibrarySpacing.Small)
                )

                CreateLogoTitle()
            }

            AuthHeader(
                title = "Tạo tài khoản",
                subtitle = "Đăng ký để sử dụng các dịch vụ của thư viện"
            )

            CustomTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    errorMessage = ""
                    authViewModel.clearError()
                },
                label = "Họ và tên",
            )
            Spacer(
                modifier = Modifier.height(LibrarySpacing.Medium)
            )

            CustomTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                    authViewModel.clearError()
                },
                label = "Email tài khoản",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(
                modifier = Modifier.height(LibrarySpacing.Medium)
            )
            CustomTextField(
                value = sdt,
                onValueChange = {
                    sdt = it
                    errorMessage = ""
                    authViewModel.clearError()
                },
                label = "Số điện thoại",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(
                modifier = Modifier.height(LibrarySpacing.Medium)
            )
            PasswordTextField(
                password = password,
                onPasswordChange = {
                    password = it
                    errorMessage = ""
                    authViewModel.clearError()
                },
                label = "Mật khẩu",
                isPasswordVisible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible }
            )
            Spacer(
                modifier = Modifier.height(LibrarySpacing.Medium)
            )
            PasswordTextField(
                password = confirmPassword,
                onPasswordChange = {
                    confirmPassword = it
                    errorMessage = ""
                    authViewModel.clearError()
                },
                label = "Nhập lại mật khẩu",
                isPasswordVisible = confirmPasswordVisible,
                onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible }
            )

            // Thông báo lỗi
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

            Spacer(
                modifier = Modifier.height(LibrarySpacing.Large)
            )

            AuthButton(
                text = if (uiState.isLoading) "ĐANG XỬ LÝ..." else "ĐĂNG KÝ",
                onClick = {
                    when {
                        fullName.isBlank() -> {
                            errorMessage = "Vui lòng nhập họ và tên"
                        }

                        email.isBlank() -> {
                            errorMessage = "Vui lòng nhập email"
                        }

                        !android.util.Patterns.EMAIL_ADDRESS
                            .matcher(email)
                            .matches() -> {
                            errorMessage = "Email không hợp lệ"
                        }

                        sdt.isBlank() -> {
                            errorMessage = "Vui lòng nhập số điện thoại"
                        }

                        password.isBlank() -> {
                            errorMessage = "Vui lòng nhập mật khẩu"
                        }

                        password.length < 6 -> {
                            errorMessage = "Mật khẩu phải có ít nhất 6 ký tự"
                        }

                        confirmPassword.isBlank() -> {
                            errorMessage = "Vui lòng nhập lại mật khẩu"
                        }

                        password != confirmPassword -> {
                            errorMessage = "Mật khẩu nhập lại không khớp"
                        }

                        else -> {
                            errorMessage = ""
                            authViewModel.register(
                                name = fullName,
                                email = email,
                                phone = sdt,
                                password = password,
                                onSuccess = onRegisterClick
                            )
                        }
                    }
                }
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            AuthFooter(
                descriptionText = "Đã có tài khoản",
                actionText = "Đăng nhập",
                onActionClick = onNavigateToLogin
            )

        }
    }
}
