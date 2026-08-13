package com.group_7.library_management.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.group_7.library_management.R

@Composable
fun CreateLogoIcon(
    @DrawableRes iconRes: Int = R.drawable.logo,
    contentDescription: String = "logo",
    size: Dp = 50.dp,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(iconRes),
        contentDescription = contentDescription,
        tint = Color.Unspecified,
        modifier = modifier
            .size(size)
            .clip(MaterialTheme.shapes.small)
    )
}