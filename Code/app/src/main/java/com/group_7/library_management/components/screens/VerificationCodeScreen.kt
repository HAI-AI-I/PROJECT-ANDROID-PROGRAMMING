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
import com.group_7.library_management.data.remote.dto.RegistrationVerificationMethod
import com.group_7.library_management.ui.theme.LibrarySpacing
import com.group_7.library_management.utils.ValidationUtils

@Composable
fun VerificationCodeScreen(
    title: String,
    subtitle: String,
    onNavigateBack: () -> Unit,
    onSubmit: (String, RegistrationVerificationMethod) -> Unit,
    requiresIdentifier: Boolean = true,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var showDialog by remember { mutableStateOf(true) }
    var identifier by remember { mutableStateOf("") }
    var method by remember { mutableStateOf<RegistrationVerificationMethod?>(null) }
    var localError by remember { mutableStateOf<String?>(null) }

    if (showDialog) {
        VerificationMethodDialog(
            onDismiss = {
                if (method == null) onNavigateBack() else showDialog = false
            },
            onConfirm = { selected ->
                method = if (selected == "Sdt") {
                    RegistrationVerificationMethod.SMS
                } else {
                    RegistrationVerificationMethod.EMAIL
                }
                identifier = ""
                localError = null
                showDialog = false
            }
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = LibrarySpacing.Large, vertical = LibrarySpacing.Huge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
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
                Spacer(Modifier.width(12.dp))
                CreateLogoTitle()
            }

            AuthHeader(title = title, subtitle = subtitle)

            method?.let { selectedMethod ->
                if (requiresIdentifier) {
                    CustomTextField(
                        value = identifier,
                        onValueChange = {
                            identifier = it
                            localError = null
                        },
                        label = if (selectedMethod == RegistrationVerificationMethod.EMAIL) {
                            "Email"
                        } else {
                            "Số điện thoại"
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (selectedMethod == RegistrationVerificationMethod.EMAIL) {
                                KeyboardType.Email
                            } else {
                                KeyboardType.Phone
                            }
                        )
                    )
                } else {
                    Text(
                        text = if (selectedMethod == RegistrationVerificationMethod.EMAIL) {
                            "Mã xác nhận sẽ được gửi tới email của tài khoản."
                        } else {
                            "Mã xác nhận sẽ được gửi tới số điện thoại của tài khoản."
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            val shownError = localError ?: errorMessage
            if (!shownError.isNullOrBlank()) {
                Spacer(Modifier.height(LibrarySpacing.Small))
                Text(
                    text = shownError,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(LibrarySpacing.ExtraLarge))
            AuthButton(
                text = if (isLoading) "ĐANG GỬI..." else "GỬI MÃ XÁC NHẬN",
                enabled = method != null && !isLoading,
                onClick = {
                    val selectedMethod = method ?: return@AuthButton
                    val valid = !requiresIdentifier || when (selectedMethod) {
                        RegistrationVerificationMethod.EMAIL -> ValidationUtils.isValidEmail(identifier.trim())
                        RegistrationVerificationMethod.SMS -> ValidationUtils.isValidPhone(identifier.trim())
                    }
                    if (valid) {
                        onSubmit(identifier.trim(), selectedMethod)
                    } else {
                        localError = if (selectedMethod == RegistrationVerificationMethod.EMAIL) {
                            "Email không hợp lệ"
                        } else {
                            "Số điện thoại không hợp lệ"
                        }
                    }
                }
            )

            if (isLoading) {
                Spacer(Modifier.height(LibrarySpacing.Medium))
                CircularProgressIndicator()
            }

            Spacer(Modifier.weight(1f))
            AuthFooter(
                descriptionText = "",
                actionText = "Chọn phương thức khác",
                onActionClick = { if (!isLoading) showDialog = true }
            )
        }
    }
}
