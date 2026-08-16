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
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group_7.library_management.models.User
import com.group_7.library_management.components.MemberTopBar
import com.group_7.library_management.components.MemberBottomBar

@Composable
fun AppNavigationDrawer(
    user: User?,
    currentRoute: String = "home",
    onItemClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerContainerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp)
        ) {
            // --- HEADER ---
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // Avatar (Tạm thời dùng Icon, bạn có thể thay bằng AsyncImage của Coil)
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape),
                    tint = Color.LightGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = user?.name ?: "Nguyễn Văn An",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "MSSV: ${user?.studentId ?: "UTH123456"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(horizontal = 24.dp))
            Spacer(modifier = Modifier.height(20.dp))


            Text(
                text = "MAIN",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(horizontal = 24.dp))
            Spacer(modifier = Modifier.height(20.dp))


            Text(
                text = "ACCOUNT",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            DrawerItem(
                icon = Icons.Default.Person,
                label = "Hồ sơ cá nhân",
                isSelected = currentRoute == "profile",
                onClick = { onItemClick("profile") }
            )

            // Drawer item đặc biệt cho Thông báo (có dấu chấm đỏ)
            NavigationDrawerItem(
                label = {
                    Text(text = "Thông báo", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                },
                icon = {
                    Box {
                        Icon(Icons.Default.Notifications, contentDescription = null)

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                                .align(Alignment.TopEnd)
                                .border(1.dp, Color.White, CircleShape)
                        )
                    }
                },
                selected = currentRoute == "notifications",
                onClick = { onItemClick("notifications") },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    unselectedIconColor = Color.DarkGray,
                    unselectedTextColor = Color.DarkGray
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

            Spacer(modifier = Modifier.weight(1f)) // Đẩy nút Đăng xuất xuống dưới cùng
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // --- FOOTER (Logout) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogout() }
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color(0xFFD32F2F)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Đăng xuất",
                    color = Color(0xFFD32F2F),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
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
            Text(text = label, fontSize = 16.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium)
        },
        icon = { Icon(icon, contentDescription = null) },
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = Color(0xFFE8EAF6),
            selectedIconColor = Color(0xFF0D1B54),
            selectedTextColor = Color(0xFF0D1B54),
            unselectedContainerColor = Color.Transparent,
            unselectedIconColor = Color.DarkGray,
            unselectedTextColor = Color.DarkGray
        )
    )
}