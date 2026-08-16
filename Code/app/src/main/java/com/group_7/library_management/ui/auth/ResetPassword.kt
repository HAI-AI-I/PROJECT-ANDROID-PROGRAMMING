package com.group_7.library_management.ui.auth


import androidx.compose.runtime.*
import com.group_7.library_management.components.screens.ChangePasswordScreen

@Composable
fun RestPassword(
    onNavigateBack: () -> Unit = {},
    onSubmit: (String) -> Unit = {}
) {
    ChangePasswordScreen(onNavigateBack = onNavigateBack,
        onSubmit = onSubmit)
}