package com.group_7.library_management.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.group_7.library_management.ui.theme.DarkBlue
import com.group_7.library_management.ui.theme.LibrarySpacing
import com.group_7.library_management.ui.theme.PrimaryBlue
import com.group_7.library_management.ui.theme.SecondaryBlue
import com.group_7.library_management.ui.theme.SuccessColor
import com.group_7.library_management.ui.theme.TextGray
import com.group_7.library_management.ui.theme.TextSecondary

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

    val userProfile = remember {
        mutableStateOf(
            UserProfile(
                id = "US001",
                name = "Nguyễn Văn A",
                email = "nguyenvana@example.com",
                phone = "0123456789",
                joinDate = "15/08/2024",
                borrowedBooksCount = 5,
                totalBooksRead = 12
            )
        )
    }

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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Profile Header Card
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

                        // Avatar
                        ProfileAvatar(
                            initial = userProfile.value.name.first(),
                            modifier = Modifier.size(80.dp)
                        )

                        Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

                        // User Name
                        Text(
                            text = userProfile.value.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall))

                        // User ID
                        Text(
                            text = "ID: ${userProfile.value.id}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )

                        Spacer(modifier = Modifier.height(LibrarySpacing.Large))

                        // Stats Row
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

                        // Edit Profile Button
                        Button(
                            onClick = onEditProfileClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue
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

            // Profile Information Section
            item {
                ProfileInfoSection(userProfile.value)
            }

            // Settings Section
            item {
                SettingsSection(
                    onChangePasswordClick = onChangePasswordClick,
                    onSettingsClick = onSettingsClick
                )
            }

            // Account Section
            item {
                AccountSection(onLogoutClick = onLogoutClick)
            }

            item {
                Spacer(modifier = Modifier.height(LibrarySpacing.Large))
            }
        }
    }
}

@Composable
private fun ProfileAvatar(
    initial: Char,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(CircleShape)
            .background(SecondaryBlue),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
            color = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextGray
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

        // Email Card
        ProfileInfoCard(
            icon = Icons.Default.Email,
            label = "Email",
            value = userProfile.email
        )

        Spacer(modifier = Modifier.height(LibrarySpacing.Small))

        // Phone Card
        ProfileInfoCard(
            icon = Icons.Default.Phone,
            label = "Số điện thoại",
            value = userProfile.phone
        )

        Spacer(modifier = Modifier.height(LibrarySpacing.Small))

        // Join Date Card
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
                tint = PrimaryBlue
            )

            Spacer(modifier = Modifier.size(LibrarySpacing.Medium))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray,
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

        // Change Password Option
        SettingOptionCard(
            icon = Icons.Default.Lock,
            title = "Thay đổi mật khẩu",
            description = "Cập nhật mật khẩu của bạn",
            onClick = onChangePasswordClick
        )

        Spacer(modifier = Modifier.height(LibrarySpacing.Small))

        // Settings Option
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
                    tint = SecondaryBlue
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
                        color = TextGray
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = TextGray
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

        // Logout Button
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
