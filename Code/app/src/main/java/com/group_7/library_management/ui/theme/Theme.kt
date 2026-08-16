package com.group_7.library_management.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LibraryColorScheme = lightColorScheme(

    primary = PrimaryBlue,

    secondary = SecondaryBlue,

    secondaryContainer = SecondaryContainerBlue,
    onSecondaryContainer = OnSecondaryContainerBlue,

    background = Background,

    surface = Surface,

    surfaceVariant = SurfaceVariant,

    onPrimary = Surface,

    onSecondary = Surface,

    onBackground = TextPrimary,

    onSurface = TextPrimary,

    onSurfaceVariant = TextSecondary,

    error = Error,

    onError = Surface,

    outlineVariant=Color(0xFFC6C5D4),

)

@Composable
fun Library_managementTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LibraryColorScheme,

        typography = LibraryTypography,

        shapes = LibraryShapes,

        content = content
    )
}