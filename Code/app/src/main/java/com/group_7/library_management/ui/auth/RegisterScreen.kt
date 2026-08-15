package com.group_7.library_management.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group_7.library_management.components.CreateLogoIcon
import com.group_7.library_management.components.CreateLogoTitle
import com.group_7.library_management.ui.theme.LibrarySpacing

@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
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

            // Tiêu đề
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {

                Text(
                    text = "Tạo tài khoản",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(
                    text = "Đăng ký để sử dụng các dịch vụ của thư viện",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(
                        top = LibrarySpacing.Small,
                        bottom = LibrarySpacing.Large
                    )
                )
            }

            // Họ và tên
            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    errorMessage = ""
                },
                label = {
                    Text("Họ và tên")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(
                modifier = Modifier.height(LibrarySpacing.Medium)
            )

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                },
                label = {
                    Text("Email")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(
                modifier = Modifier.height(LibrarySpacing.Medium)
            )

            // Mật khẩu
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                label = {
                    Text("Mật khẩu")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation =
                    if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                trailingIcon = {

                    val image =
                        if (passwordVisible)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff

                    IconButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {
                        Icon(
                            imageVector = image,
                            contentDescription =
                                if (passwordVisible)
                                    "Ẩn mật khẩu"
                                else
                                    "Hiện mật khẩu"
                        )
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(
                modifier = Modifier.height(LibrarySpacing.Medium)
            )

            // Nhập lại mật khẩu
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                },
                label = {
                    Text("Nhập lại mật khẩu")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation =
                    if (confirmPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                trailingIcon = {

                    val image =
                        if (confirmPasswordVisible)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff

                    IconButton(
                        onClick = {
                            confirmPasswordVisible =
                                !confirmPasswordVisible
                        }
                    ) {
                        Icon(
                            imageVector = image,
                            contentDescription =
                                if (confirmPasswordVisible)
                                    "Ẩn mật khẩu"
                                else
                                    "Hiện mật khẩu"
                        )
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            // Thông báo lỗi
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

            Spacer(
                modifier = Modifier.height(LibrarySpacing.Large)
            )

            // Nút đăng ký
            Button(
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

                        password.isBlank() -> {
                            errorMessage = "Vui lòng nhập mật khẩu"
                        }

                        password.length < 6 -> {
                            errorMessage =
                                "Mật khẩu phải có ít nhất 6 ký tự"
                        }

                        confirmPassword.isBlank() -> {
                            errorMessage =
                                "Vui lòng nhập lại mật khẩu"
                        }

                        password != confirmPassword -> {
                            errorMessage =
                                "Mật khẩu nhập lại không khớp"
                        }

                        else -> {
                            errorMessage = ""

                            // Sau này nối với database
                            onRegisterClick()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(
                    text = "ĐĂNG KÝ",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 1.sp
                    )
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            // Chuyển sang đăng nhập
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = LibrarySpacing.Medium
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Đã có tài khoản? ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                TextButton(
                    onClick = onLoginClick
                ) {
                    Text(
                        text = "Đăng nhập",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}