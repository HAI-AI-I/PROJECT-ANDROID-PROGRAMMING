package com.group_7.library_management.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.group_7.library_management.components.NotificationCard
import com.group_7.library_management.ui.theme.*

enum class NotificationType {
    WARNING, SUCCESS, ERROR, INFO,BOOK
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val date: String,
    val type: NotificationType,
    val isRead: Boolean = false
)

@Composable
fun NotificationsContent(
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications by viewModel.notificationsFlow.collectAsState()

    val groupedNotifications = remember(notifications) {
        notifications.groupBy { it.date }
    }

    val unreadCount = remember(notifications) {
        notifications.count { !it.isRead }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = LibrarySpacing.Large),
        verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium)
    ) {
        item { Spacer(modifier = Modifier.height(LibrarySpacing.Small)) }

        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Thông báo",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (unreadCount > 0) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(
                                text = "$unreadCount",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.clickable {
                        viewModel.markAllAsRead()
                    },
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
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(4.dp)) }

        if (notifications.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Không có thông báo nào",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        } else {
            groupedNotifications.forEach { (date, itemsForDate) ->
                // Phân nhóm theo ngày
                item(key = "header_$date") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = LibrarySpacing.Small, bottom = LibrarySpacing.ExtraSmall),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                items(itemsForDate, key = { it.id }) { notification ->
                    NotificationCard(
                        item = notification,
                        onClick = {
                            viewModel.markAsRead(notification.id)
                        },
                        onDelete = {
                            viewModel.deleteNotification(notification.id)
                        }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(LibrarySpacing.Large)) }
    }
}
