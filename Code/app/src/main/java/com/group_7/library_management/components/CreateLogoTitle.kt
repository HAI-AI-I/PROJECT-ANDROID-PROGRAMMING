package com.group_7.library_management.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle


@Composable
fun CreateLogoTitle(
    text: String = "Library Management",
    style: TextStyle= MaterialTheme.typography.headlineSmall,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = style.copy(color = color),
        modifier = modifier
    )
}