package com.group_7.library_management.components.screens



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.group_7.library_management.components.CreateLogoIcon
import com.group_7.library_management.components.CreateLogoTitle
import com.group_7.library_management.components.auth.AuthButton
import com.group_7.library_management.components.auth.AuthFooter
import com.group_7.library_management.components.auth.AuthHeader
import com.group_7.library_management.ui.theme.LibrarySpacing


import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged

@Composable
fun ConfirmCodeScreen(
    onNavigateBack: () -> Unit = {},
    onSubmit: (String) -> Unit = {},
    onResendCode: () -> Unit = {},
    isEmail: Boolean=true
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }
    var otpCode by remember { mutableStateOf(List(6) { "" }) }
    val isComplete = otpCode.all { it.isNotEmpty() }


    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
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
            AuthHeader(title ="Nhập mã xác minh",subtitle = "Chúng tôi đã gửi 6 chữ số đến ${if (isEmail) "email" else "số điện thoại "}của bạn")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(LibrarySpacing.Small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                otpCode.forEachIndexed { index, value ->
                    OtpTextField(
                        value = value,
                        onValueChange = { newValue ->
                            if (newValue.length <= 1) {
                                val newList = otpCode.toMutableList()
                                newList[index] = newValue
                                otpCode = newList
                                if (newValue.isNotEmpty() && index < 5) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                                if (newValue.isEmpty() && index > 0) {
                                    focusRequesters[index - 1].requestFocus()
                                }
                            }
                        },
                        focusRequester = focusRequesters[index],
                        primaryColor = MaterialTheme.colorScheme.primary,
                        onFocusChanged = {}
                    )
                }
            }

            Spacer(modifier = Modifier.height(LibrarySpacing.ExtraLarge))

            AuthButton(
                text ="XÁC NHẬN",
                onClick = {
                    if (isComplete) {
                        onSubmit(otpCode.joinToString(""))
                    }
                },
                enabled = isComplete
            )

            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

            // Footer với hành động gửi lại mã
            AuthFooter(
                descriptionText = "",
                actionText = "Gửi lại mã",
                onActionClick = onResendCode
            )
        }
    }
}

@Composable
fun OtpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    primaryColor: Color,
    onFocusChanged: (Boolean) -> Unit = {}
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(width = 50.dp, height = 60.dp)
            .border(
                width = 2.dp,
                color = if (isFocused || value.isNotEmpty()) primaryColor else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            )
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                onFocusChanged(focusState.isFocused)
            },
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (value.isEmpty() && isFocused) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(24.dp)
                            .background(primaryColor)
                    )
                }
                innerTextField()
            }
        )
    }
}
