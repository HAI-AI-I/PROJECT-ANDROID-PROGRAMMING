package com.group_7.library_management.ui.home

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Fingerprint
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.group_7.library_management.models.User
import com.group_7.library_management.navigation.Routes
import com.group_7.library_management.ui.theme.LibrarySpacing
import com.group_7.library_management.utils.BiometricAuthManager

@Composable
fun AppNavigationDrawer(
    user: User?,
    unreadNotificationCount: Int = 0,
    isBiometricEnabled: Boolean = false,
    onToggleBiometric: (Boolean) -> Unit = {},
    currentRoute: String = Routes.HOME,
    onItemClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val biometricAuthManager = remember(activity) {
        activity?.let { BiometricAuthManager(it) }
    }

    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
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
                        text = "Id tài khoản: ${user?.phone ?: "Chưa cập nhật"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(LibrarySpacing.Large))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp, modifier = Modifier.padding(horizontal = LibrarySpacing.Large))
            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

            DrawerItem(
                icon = Icons.Default.Home,
                label = "Trang chủ",
                isSelected = currentRoute == Routes.HOME,
                onClick = { onItemClick(Routes.HOME) }
            )
            DrawerItem(
                icon = Icons.Default.Book,
                label = "Danh sách sách",
                isSelected = currentRoute == Routes.BOOKS,
                onClick = { onItemClick(Routes.BOOKS) }
            )
            DrawerItem(
                icon = Icons.Default.History,
                label = "Lịch sử mượn",
                isSelected = currentRoute == Routes.BORROW,
                onClick = { onItemClick(Routes.BORROW) }
            )

            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp, modifier = Modifier.padding(horizontal = LibrarySpacing.Large))
            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))


            DrawerItem(
                icon = Icons.Default.Person,
                label = "Hồ sơ cá nhân",
                isSelected = currentRoute == Routes.PROFILE,
                onClick = { onItemClick(Routes.PROFILE) }
            )

            NavigationDrawerItem(
                label = {
                    Text(text = "Thông báo", style = MaterialTheme.typography.titleSmall)
                },
                icon = {
                    Box {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                        if (unreadNotificationCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.error)
                                    .align(Alignment.TopEnd)
                                    .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            )
                        }
                    }
                },
                selected = currentRoute == Routes.NOTIFICATIONS,
                onClick = { onItemClick(Routes.NOTIFICATIONS) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            NavigationDrawerItem(
                label = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sinh trắc học",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = isBiometricEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    if (biometricAuthManager?.isBiometricAvailable() == true) {
                                        biometricAuthManager.showBiometricPrompt(
                                            title = "Xác thực sinh trắc học",
                                            subtitle = "Xác thực vân tay/khuôn mặt để bật tính năng",
                                            onSuccess = {
                                                onToggleBiometric(true)
                                                Toast.makeText(context, "Đã bật đăng nhập bằng vân tay/ Khuôn mặt", Toast.LENGTH_SHORT).show()
                                            },
                                            onError = { err ->
                                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    } else {
                                        Toast.makeText(context, "Thiết bị không hỗ trợ sinh trắc học", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    onToggleBiometric(false)
                                    Toast.makeText(context, "Đã tắt đăng nhập bằng vân tay", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Sinh trắc học"
                    )
                },
                selected = false,
                onClick = {
                    val nextState = !isBiometricEnabled
                    if (nextState) {
                        if (biometricAuthManager?.isBiometricAvailable() == true) {
                            biometricAuthManager.showBiometricPrompt(
                                title = "Xác thực sinh trắc học",
                                subtitle = "Xác thực vân tay/khuôn mặt để bật tính năng",
                                onSuccess = {
                                    onToggleBiometric(true)
                                    Toast.makeText(context, "Đã bật đăng nhập bằng vân tay", Toast.LENGTH_SHORT).show()
                                },
                                onError = { err ->
                                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            Toast.makeText(context, "Thiết bị không hỗ trợ sinh trắc học", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        onToggleBiometric(false)
                        Toast.makeText(context, "Đã tắt đăng nhập bằng vân tay", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    unselectedIconColor = if (isBiometricEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            DrawerItem(
                icon = Icons.Default.Settings,
                label = "Cài đặt",
                isSelected = currentRoute == Routes.SETTINGS,
                onClick = { onItemClick(Routes.SETTINGS) }
            )
            DrawerItem(
                icon = Icons.Default.Chat,
                label = "Chat với Admin",
                isSelected = false,
                onClick = {
                    val phoneNumber = "0367036415"
                    val zaloUri = Uri.parse("https://zalo.me/$phoneNumber")
                    val intent = Intent(Intent.ACTION_VIEW, zaloUri).apply {
                        setPackage("com.zing.zalo")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        val fallbackIntent = Intent(Intent.ACTION_VIEW, zaloUri)
                        context.startActivity(fallbackIntent)
                    }
                }
            )
            DrawerItem(
                icon = Icons.Outlined.HelpOutline,
                label = "Hỗ trợ",
                isSelected = currentRoute == Routes.HELP,
                onClick = { onItemClick(Routes.HELP) }
            )


            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = LibrarySpacing.Large))
            Spacer(modifier = Modifier.height(LibrarySpacing.Small))

            // --- FOOTER (Logout) ---
            NavigationDrawerItem(
                label = {
                    Text(
                        text = "Đăng xuất",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout"
                    )
                },
                selected = false,
                onClick = onLogout,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.error,
                    unselectedTextColor = MaterialTheme.colorScheme.error
                )
            )
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
