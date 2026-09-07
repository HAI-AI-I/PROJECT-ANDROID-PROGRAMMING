package com.group_7.library_management.components

import android.media.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.group_7.library_management.ui.theme.LibrarySpacing

@Composable
fun MemberTopBar(
    leftIcon: ImageVector=Icons.Default.Menu,
    leftIconTint:Color= MaterialTheme.colorScheme.onSurface,
    onLeftClick: () -> Unit,
    rightIcon:ImageVector=Icons.Default.Notifications,
    rightIconTint: Color= MaterialTheme.colorScheme.onSurface,
    onRightClick: () -> Unit,
    showNotificationBadge: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onLeftClick,
            modifier = Modifier.size(LibrarySpacing.Huge)
        ) {
            Icon(
                imageVector=leftIcon,
                contentDescription =null,
                tint = leftIconTint
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CreateLogoIcon()
            Spacer(modifier = Modifier.width(12.dp))
            CreateLogoTitle()
        }

        // Nút thông báo
        IconButton(
            onClick = onRightClick,
            modifier = Modifier.size(LibrarySpacing.Huge)
        ) {
            Box {
                Icon(
                    imageVector = rightIcon,
                    contentDescription =null,
                    tint = rightIconTint
                )
                if (showNotificationBadge) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error)
                            .border(1.5.dp, MaterialTheme.colorScheme.background, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }
        }
    }
}