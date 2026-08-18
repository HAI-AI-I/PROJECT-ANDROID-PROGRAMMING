package com.group_7.library_management.ui.auth


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.group_7.library_management.components.CreateLogoIcon
import com.group_7.library_management.components.CreateLogoTitle
import com.group_7.library_management.components.CustomTextField
import com.group_7.library_management.components.auth.AuthButton
import com.group_7.library_management.components.auth.AuthFooter
import com.group_7.library_management.components.auth.AuthHeader
import com.group_7.library_management.components.dialogs.VerificationMethodDialog
import com.group_7.library_management.components.screens.VerificationCodeScreen
import com.group_7.library_management.ui.theme.LibrarySpacing

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onSubmit: () -> Unit
) {
    VerificationCodeScreen(
        title = "Khôi phục mật khẩu",
        subtitle = "Nhập email hoặc số điện thoại đã đăng ký, chúng tôi sẽ gửi mã xác nhận để đặt lại mật khẩu của bạn",
        onNavigateBack = onNavigateBack,
        onSubmit = onSubmit
    )
}



