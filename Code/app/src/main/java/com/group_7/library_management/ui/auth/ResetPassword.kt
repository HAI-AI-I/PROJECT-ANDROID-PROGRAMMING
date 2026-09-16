package com.group_7.library_management.ui.auth

import androidx.compose.runtime.Composable
import com.group_7.library_management.ui.profile.ChangePasswordScreen

@Composable
fun RestPassword(
    onNavigateBack: () -> Unit = {},
    onSubmit: (String) -> Unit = {}
) {
    ChangePasswordScreen(
        onNavigateBack = onNavigateBack,
        onSaveSuccess = { onSubmit("") } // Giả sử thành công thì gọi onSubmit
    )
}
