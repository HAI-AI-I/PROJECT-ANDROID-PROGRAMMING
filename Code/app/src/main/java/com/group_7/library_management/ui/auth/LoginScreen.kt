package com.group_7.library_management.ui.auth

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group_7.library_management.R
import com.group_7.library_management.components.CreateLogoIcon
import com.group_7.library_management.components.CreateLogoTitle
import com.group_7.library_management.components.CustomTextField
import com.group_7.library_management.components.PasswordTextField
import com.group_7.library_management.components.auth.AuthButton
import com.group_7.library_management.components.auth.AuthFooter
import com.group_7.library_management.components.auth.AuthHeader
import com.group_7.library_management.ui.theme.LibrarySpacing



@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit,
                onNavigateToForgotPassword:()->Unit) {

    var textEmailorPassword by remember { mutableStateOf("") }
    var textPassword by remember { mutableStateOf("")}
    var passwordVisible by remember{mutableStateOf(false)}

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
                modifier = Modifier.padding(top=40.dp, bottom = 40.dp)
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
                value=textEmailorPassword,
                onValueChange = {textEmailorPassword=it},
                label = "Email/Số điện thoại",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))
            PasswordTextField(
                password = textPassword,
                onPasswordChange = {textPassword=it},
                label = "Mật khẩu",
                isPasswordVisible = passwordVisible,
                onToggleVisibility = {passwordVisible=!passwordVisible},
            )

            // Forgot Password
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(onClick =onNavigateToForgotPassword) {
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

            // Sign In Button
            AuthButton(
                text = "ĐĂNG NHẬP",
                onClick = {}
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
