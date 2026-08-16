package com.group_7.library_management.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Import màu từ file Color.kt (Dùng dấu * để lấy hết các màu bạn đã khai báo)
import com.group_7.library_management.ui.theme.*

// ==========================================
// THÊM 2 MODEL VÀO TRỰC TIẾP FILE NÀY ĐỂ FIX LỖI "UNRESOLVED REFERENCE"
// ==========================================
enum class NotificationType {
    WARNING, SUCCESS, ERROR, INFO
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType
)
// ==========================================

@Composable
fun NotificationsScreen(
    onMenuClick: () -> Unit = {}
) {
    var selectedBottomTab by remember { mutableIntStateOf(0) }

    val notifications = listOf(
        NotificationItem(
            id = "1",
            title = "Clean Code còn 2 ngày nữa đến hạn trả",
            message = "Vui lòng sắp xếp thời gian trả sách để tránh bị phạt phí quá hạn.",
            time = "15 phút trước",
            type = NotificationType.WARNING
        ),
        NotificationItem(
            id = "2",
            title = "Bạn đã mượn sách thành công",
            message = "Sách \"Design Patterns\" đã được thêm vào tài khoản của bạn. Hạn trả: 15/11/2023.",
            time = "2 giờ trước",
            type = NotificationType.SUCCESS
        ),
        NotificationItem(
            id = "3",
            title = "Sách Kotlin in Action đã quá hạn 3 ngày",
            message = "Tài khoản của bạn đang bị tính phí phạt. Vui lòng hoàn trả sách ngay lập tức.",
            time = "Hôm qua",
            type = NotificationType.ERROR
        ),
        NotificationItem(
            id = "4",
            title = "Bảo trì hệ thống",
            message = "Hệ thống thư viện sẽ tạm ngưng hoạt động từ 22:00 đến 02:00 ngày mai để bảo trì định kỳ.",
            time = "3 ngày trước",
            type = NotificationType.INFO
        )
    )

    Scaffold(
        containerColor = BackgroundLight,
        topBar = { NotificationTopBar(onMenuClick) },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.height(80.dp)
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home", style = MaterialTheme.typography.labelSmall) },
                    selected = selectedBottomTab == 0,
                    onClick = { selectedBottomTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Book, contentDescription = null) },
                    label = { Text("Books", style = MaterialTheme.typography.labelSmall) },
                    selected = selectedBottomTab == 1,
                    onClick = { selectedBottomTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(imageVector = Icons.AutoMirrored.Filled.LibraryBooks, contentDescription = null) },
                    label = { Text("My Borrowing", style = MaterialTheme.typography.labelSmall) },
                    selected = selectedBottomTab == 2,
                    onClick = { selectedBottomTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile", style = MaterialTheme.typography.labelSmall) },
                    selected = selectedBottomTab == 3,
                    onClick = { selectedBottomTab = 3 }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Thông báo",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )

                    Row(
                        modifier = Modifier.clickable { /* Xử lý đánh dấu đã đọc */ },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Mark as read",
                            tint = InfoColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Đánh dấu đã đọc tất cả",
                            style = MaterialTheme.typography.labelLarge,
                            color = InfoColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(4.dp)) }

            items(notifications) { notification ->
                NotificationCard(notification)
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun NotificationCard(item: NotificationItem) {

    val (indicatorColor, icon) = when (item.type) {
        NotificationType.WARNING -> WarningColor to Icons.Default.WarningAmber
        NotificationType.SUCCESS -> SuccessColor to Icons.Default.CheckCircleOutline
        NotificationType.ERROR -> ErrorColor to Icons.Default.ErrorOutline
        NotificationType.INFO -> InfoColor to Icons.Default.Info
    }

    val titleColor = if (item.type == NotificationType.ERROR) ErrorColor else Color.Black
    val timeColor = if (item.type == NotificationType.ERROR) ErrorColor else TextGray

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(indicatorColor)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top // Đã import Alignment để fix
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = indicatorColor,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = item.time,
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                        color = timeColor
                    )
                }

                IconButton(
                    onClick = {  },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationTopBar(onMenuClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClick, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.Black)
        }

        Text(
            text = "Library UTH 13",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = DarkBlue
        )

        IconButton(onClick = { }, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = DarkBlue)
        }
    }
}