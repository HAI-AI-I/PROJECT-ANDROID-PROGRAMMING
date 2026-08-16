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
import androidx.compose.material.icons.filled.Close
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
import com.group_7.library_management.components.MemberBottomBar
import com.group_7.library_management.components.MemberTopBar
import com.group_7.library_management.components.NotificationCard

// Import màu từ file Color.kt (Dùng dấu * để lấy hết các màu bạn đã khai báo)
import com.group_7.library_management.ui.theme.*

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

@Composable
fun NotificationsScreen(
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
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
        ),
        NotificationItem(
            id = "4",
            title = "Bảo trì hệ thống",
            message = "Hệ thống thư viện sẽ tạm ngưng hoạt động từ 22:00 đến 02:00 ngày mai để bảo trì định kỳ.",
            time = "3 ngày trước",
            type = NotificationType.INFO
        ),
        NotificationItem(
            id = "4",
            title = "Bảo trì hệ thống",
            message = "Hệ thống thư viện sẽ tạm ngưng hoạt động từ 22:00 đến 02:00 ngày mai để bảo trì định kỳ.",
            time = "3 ngày trước",
            type = NotificationType.INFO
        ),
        NotificationItem(
            id = "4",
            title = "Bảo trì hệ thống",
            message = "Hệ thống thư viện sẽ tạm ngưng hoạt động từ 22:00 đến 02:00 ngày mai để bảo trì định kỳ.",
            time = "3 ngày trước",
            type = NotificationType.INFO
        ),
        NotificationItem(
            id = "4",
            title = "Bảo trì hệ thống",
            message = "Hệ thống thư viện sẽ tạm ngưng hoạt động từ 22:00 đến 02:00 ngày mai để bảo trì định kỳ.",
            time = "3 ngày trước",
            type = NotificationType.INFO
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { MemberTopBar(
            onMenuClick=onMenuClick,
            onNotificationClick=onNotificationClick,
            showNotificationBadge = true
        ) },
        bottomBar = {
            MemberBottomBar(
                currentRoute=currentRoute,
                onNavigate=onNavigate
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = LibrarySpacing.Large),
            verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium)
        ) {
            item { Spacer(modifier = Modifier.height(LibrarySpacing.Small)) }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Thông báo",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.clickable { /* Xử lý đánh dấu đã đọc */ },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(LibrarySpacing.ExtraSmall)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Mark as read",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(LibrarySpacing.Medium)
                        )
                        Text(
                            text = "Đánh dấu đã đọc tất cả",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(4.dp)) }

            items(notifications) { notification ->
                NotificationCard(notification)
            }

        }
    }
}
