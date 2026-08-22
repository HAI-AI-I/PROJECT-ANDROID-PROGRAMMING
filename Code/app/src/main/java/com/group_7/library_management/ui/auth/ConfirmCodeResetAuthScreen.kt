package com.group_7.library_management.ui.auth

import androidx.compose.runtime.Composable
import com.group_7.library_management.components.screens.ConfirmCodeScreen

@Composable
fun ConfirmCodeResetAuthScreen(
    onNavigateBack:()-> Unit,
    onSubmit:(String)->Unit
) {
    ConfirmCodeScreen(onNavigateBack=onNavigateBack,
        onSubmit=onSubmit)
}