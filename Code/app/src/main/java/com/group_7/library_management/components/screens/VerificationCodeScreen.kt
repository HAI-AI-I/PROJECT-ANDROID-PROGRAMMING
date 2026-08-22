package com.group_7.library_management.components.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.group_7.library_management.components.CreateLogoIcon
import com.group_7.library_management.components.CreateLogoTitle
import com.group_7.library_management.components.CustomTextField
import com.group_7.library_management.components.auth.AuthButton
import com.group_7.library_management.components.auth.AuthFooter
import com.group_7.library_management.components.auth.AuthHeader
import com.group_7.library_management.components.dialogs.VerificationMethodDialog
import com.group_7.library_management.ui.theme.LibrarySpacing
import com.group_7.library_management.utils.ValidationUtils

@Composable
fun VerificationCodeScreen(
    title: String,
    subtitle: String,
    onNavigateBack: () -> Unit,
    onSubmit: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var textEmailOrPhone by remember { mutableStateOf("") }
    var isEmail by remember { mutableStateOf(true) }

    if (showDialog) {
        VerificationMethodDialog(
            onDismiss = { showDialog = false },
            onConfirm = { method ->
                when (method) {
                    "Email" -> {
                        isEmail = true
                        textEmailOrPhone = ""
                    }
                    "Sdt" -> {
                        isEmail = false
                        textEmailOrPhone = ""
                    }
                }
                showDialog = false
            }
        )
    }

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


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 20.dp, bottom = 32.dp)
            ) {
                CreateLogoIcon()
                Spacer(modifier = Modifier.width(12.dp))
                CreateLogoTitle()
            }

            AuthHeader(
                title = title,
                subtitle = subtitle
            )

            if (isEmail) {
                CustomTextField(
                    value = textEmailOrPhone,
                    onValueChange = { textEmailOrPhone = it },
                    label = "Email",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            } else {
                CustomTextField(
                    value = textEmailOrPhone,
                    onValueChange = { textEmailOrPhone = it },
                    label = "Số điện thoại",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }

            Spacer(modifier = Modifier.height(LibrarySpacing.ExtraLarge))

            AuthButton(
                text = "GỬI YÊU CẦU",
                onClick = {
                    if(isEmail){
                        if(ValidationUtils.checkExitsEmail(textEmailOrPhone)){
                            onSubmit()
                        }
                        else{
                            textEmailOrPhone=""
                        }
                    }
                    else{
                        if(ValidationUtils.checkExitsPhone(textEmailOrPhone)){
                            onSubmit()
                        }
                        else{
                            textEmailOrPhone=""
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

            Text(
                text = "Bạn sẽ nhận được mã OTP hoặc liên kết đặt lại mật khẩu trong vài phút",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            AuthFooter(
                descriptionText = "",
                actionText = "Chọn phương thức khác",
                onActionClick = { showDialog = true }
            )
        }
    }
}