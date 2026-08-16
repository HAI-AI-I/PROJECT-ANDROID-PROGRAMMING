package com.group_7.library_management.components

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
import androidx.compose.ui.unit.dp
import com.group_7.library_management.ui.theme.LibrarySpacing

@Composable
fun MemberTopBar(
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit,
    showNotificationBadge: Boolean = true, // Cho phép tắt badge nếu không có thông báo
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(LibrarySpacing.Huge)
        ) {
            Icon(
                Icons.Default.Menu,
                contentDescription = "Menu",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        // Logo và tên ứng dụng
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CreateLogoIcon()
            Spacer(modifier = Modifier.width(12.dp))
            CreateLogoTitle()
        }

        // Nút thông báo
        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier.size(LibrarySpacing.Huge)
        ) {
            Box {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurface
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