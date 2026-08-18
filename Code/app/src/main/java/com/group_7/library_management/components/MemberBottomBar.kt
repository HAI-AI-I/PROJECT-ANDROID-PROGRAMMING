package com.group_7.library_management.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.group_7.library_management.navigation.Routes
import com.group_7.library_management.ui.theme.LibrarySpacing

@Composable
fun MemberBottomBar(
    currentRoute: String,          // Xác định item nào đang được chọn
    onNavigate: (String) -> Unit   // Callback khi bấm vào item
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.onSecondary,
        tonalElevation = LibrarySpacing.Small,
    ) {
        val items = listOf(
            BottomNavItem(Routes.HOME, Icons.Default.Home, "Trang Chủ"),
            BottomNavItem(Routes.BOOKS, Icons.Default.Book, "Sách"),
            BottomNavItem(Routes.SCAN_QR,Icons.Default.QrCodeScanner,"Quét QR"),
            BottomNavItem(Routes.MY_BOOKS, Icons.AutoMirrored.Filled.LibraryBooks, "Mượn Sách"),
            BottomNavItem(Routes.PROFILE, Icons.Default.Person, "Hồ Sơ")
        )
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(text=item.label, style = MaterialTheme.typography.labelLarge) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            )
        }
    }
}

private data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)