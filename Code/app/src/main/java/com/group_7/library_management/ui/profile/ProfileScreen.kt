package com.group_7.library_management.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.group_7.library_management.ui.theme.LibrarySpacing

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val joinDate: String,
    val avatarUrl: String = "",
    val borrowedBooksCount: Int = 0,
    val totalBooksRead: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEditProfileClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hồ Sơ Cá Nhân",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        ProfileContent(
            modifier = Modifier.padding(paddingValues),
            onEditProfileClick = onEditProfileClick,
            onChangePasswordClick = onChangePasswordClick,
            onSettingsClick = onSettingsClick,
            onLogoutClick = onLogoutClick
        )
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    onEditProfileClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val userProfile = remember {
        mutableStateOf(
            UserProfile(
                id = "US001",
                name = "Nguyễn Văn hair",
                email = "nguyenvana@example.com",
                phone = "0123456789",
                joinDate = "15/08/2024",
                borrowedBooksCount = 5,
                totalBooksRead = 12
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(LibrarySpacing.Large))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LibrarySpacing.Medium),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(LibrarySpacing.Large),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfileAvatar(
                        initial = userProfile.value.name.first(),
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

                    Text(
                        text = userProfile.value.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall))

                    Text(
                        text = "ID: ${userProfile.value.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(LibrarySpacing.Large))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(LibrarySpacing.Medium),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem(
                            label = "Sách đang mượn",
                            value = userProfile.value.borrowedBooksCount.toString()
                        )
                        StatItem(
                            label = "Tổng đã đọc",
                            value = userProfile.value.totalBooksRead.toString()
                        )
                    }

                    Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

                    Button(
                        onClick = onEditProfileClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Chỉnh sửa",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.size(LibrarySpacing.Small))
                        Text(
                            text = "Chỉnh sửa hồ sơ",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        item {
            ProfileInfoSection(userProfile.value)
        }

        item {
            SettingsSection(
                onChangePasswordClick = onChangePasswordClick,
                onSettingsClick = onSettingsClick
            )
        }

        item {
            AccountSection(onLogoutClick = onLogoutClick)
        }

        item {
            Spacer(modifier = Modifier.height(LibrarySpacing.Large))
        }
    }
}

@Composable
private fun ProfileAvatar(
    initial: Char,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial.toString().uppercase(),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileInfoSection(userProfile: UserProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LibrarySpacing.Medium)
    ) {
        Text(
            text = "Thông tin cá nhân",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = LibrarySpacing.Small)
        )
        Spacer(modifier = Modifier.height(LibrarySpacing.Medium))
        ProfileInfoCard(
            icon = Icons.Default.Email,
            label = "Email",
            value = userProfile.email
        )
        Spacer(modifier = Modifier.height(LibrarySpacing.Small))
        ProfileInfoCard(
            icon = Icons.Default.Phone,
            label = "Số điện thoại",
            value = userProfile.phone
        )
        Spacer(modifier = Modifier.height(LibrarySpacing.Small))
        ProfileInfoCard(
            icon = Icons.Default.Info,
            label = "Ngày tham gia",
            value = userProfile.joinDate
        )
    }
}

@Composable
private fun ProfileInfoCard(
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LibrarySpacing.Medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(LibrarySpacing.Medium))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    onChangePasswordClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LibrarySpacing.Medium)
    ) {
        Text(
            text = "Cài đặt",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = LibrarySpacing.Small)
        )
        Spacer(modifier = Modifier.height(LibrarySpacing.Medium))
        SettingOptionCard(
            icon = Icons.Default.Lock,
            title = "Thay đổi mật khẩu",
            description = "Cập nhật mật khẩu của bạn",
            onClick = onChangePasswordClick
        )
        Spacer(modifier = Modifier.height(LibrarySpacing.Small))
        SettingOptionCard(
            icon = Icons.Default.Settings,
            title = "Cài đặt ứng dụng",
            description = "Tùy chỉnh cài đặt ứng dụng",
            onClick = onSettingsClick
        )
    }
}

@Composable
private fun SettingOptionCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LibrarySpacing.Medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.size(LibrarySpacing.Medium))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AccountSection(
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LibrarySpacing.Medium)
    ) {
        Text(
            text = "Tài khoản",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = LibrarySpacing.Small)
        )
        Spacer(modifier = Modifier.height(LibrarySpacing.Medium))
        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Đăng xuất",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.size(LibrarySpacing.Small))
            Text(
                text = "Đăng xuất",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
