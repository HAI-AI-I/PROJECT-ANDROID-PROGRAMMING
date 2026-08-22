package com.group_7.library_management.components.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.group_7.library_management.components.CreateLogoIcon
import com.group_7.library_management.components.CreateLogoTitle
import com.group_7.library_management.components.PasswordTextField
import com.group_7.library_management.components.auth.AuthButton
import com.group_7.library_management.components.auth.AuthHeader
import com.group_7.library_management.ui.theme.LibrarySpacing

@Composable
fun ChangePasswordScreen(
    onSubmit: (String) -> Unit,
    onNavigateBack:()->Unit,
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

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
            if (onNavigateBack != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 20.dp, bottom = 32.dp)
            ) {
                CreateLogoIcon()
                Spacer(modifier = Modifier.width(12.dp))
                CreateLogoTitle()
            }

            // Header với tiêu đề và mô tả
            AuthHeader(title ="Đặt lại mật khẩu", subtitle = "Vui lòng nhập mật khẩu mới cho tài khoản của bạn")

            // Trường nhập mật khẩu mới
            PasswordTextField(
                password = password,
                onPasswordChange = { password = it },
                label = "Mật khẩu mới",
                isPasswordVisible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible }
            )

            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

            // Trường nhập lại mật khẩu
            PasswordTextField(
                password = confirmPassword,
                onPasswordChange = { confirmPassword = it },
                label = "Nhập lại mật khẩu mới",
                isPasswordVisible = confirmPasswordVisible,
                onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible }
            )

            // Hiển thị lỗi
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = LibrarySpacing.Small)
                )
            }

            Spacer(modifier = Modifier.height(LibrarySpacing.Large))

            // Nút submit
            AuthButton(
                text = "CẬP NHẬT  MẬT KHẨU",
                onClick ={onSubmit(password)}
            )
        }
    }
}