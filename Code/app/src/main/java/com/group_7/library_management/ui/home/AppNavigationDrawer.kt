package com.group_7.library_management.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.group_7.library_management.models.User

@Composable
fun AppNavigationDrawer(
    user: User?,
    currentRoute: String = "home",
    onItemClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(vertical = LibrarySpacing.Large)
        ) {
            // --- HEADER ---
            Column(modifier = Modifier.padding(horizontal = LibrarySpacing.Large)) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

                Text(
                    text = user?.name ?: "Nguyễn Văn An",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.extraSmall)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "MSSV: ${user?.studentId ?: "UTH123456"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(LibrarySpacing.Large))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp, modifier = Modifier.padding(horizontal = LibrarySpacing.Large))
            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

            Text(
                text = "MAIN",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 28.dp)
            )
            Spacer(modifier = Modifier.height(LibrarySpacing.Small))

            DrawerItem(
                icon = Icons.Default.Home,
                label = "Trang chủ",
                isSelected = currentRoute == "home",
                onClick = { onItemClick("home") }
            )
            DrawerItem(
                icon = Icons.Default.Book,
                label = "Danh sách sách",
                isSelected = currentRoute == "books",
                onClick = { onItemClick("books") }
            )
            DrawerItem(
                icon = Icons.Default.LibraryBooks,
                label = "Sách của tôi",
                isSelected = currentRoute == "my_books",
                onClick = { onItemClick("my_books") }
            )
            DrawerItem(
                icon = Icons.Default.History,
                label = "Lịch sử mượn",
                isSelected = currentRoute == "history",
                onClick = { onItemClick("history") }
            )

            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp, modifier = Modifier.padding(horizontal = LibrarySpacing.Large))
            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

            Text(
                text = "ACCOUNT",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 28.dp)
            )
            Spacer(modifier = Modifier.height(LibrarySpacing.Small))

            DrawerItem(
                icon = Icons.Default.Person,
                label = "Hồ sơ cá nhân",
                isSelected = currentRoute == "profile",
                onClick = { onItemClick("profile") }
            )

            NavigationDrawerItem(
                label = {
                    Text(text = "Thông báo", style = MaterialTheme.typography.titleSmall)
                },
                icon = {
                    Box {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error)
                                .align(Alignment.TopEnd)
                                .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        )
                    }
                },
                selected = currentRoute == "notifications",
                onClick = { onItemClick("notifications") },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            DrawerItem(
                icon = Icons.Default.Settings,
                label = "Cài đặt",
                isSelected = currentRoute == "settings",
                onClick = { onItemClick("settings") }
            )
            DrawerItem(
                icon = Icons.Outlined.HelpOutline,
                label = "Hỗ trợ",
                isSelected = currentRoute == "help",
                onClick = { onItemClick("help") }
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

            // --- FOOTER (Logout) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogout() }
                    .padding(vertical = LibrarySpacing.Medium),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(LibrarySpacing.Small))
                Text(
                    text = "Đăng xuất",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
fun DrawerItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            )
        },
        icon = { Icon(icon, contentDescription = null) },
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unselectedContainerColor = Color.Transparent,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}