package com.group_7.library_management.ui.support

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.group_7.library_management.ui.theme.LibrarySpacing
import com.group_7.library_management.ui.theme.Success

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySupportRequestsScreen(
    viewModel: SupportViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val tabs = listOf("Tất cả", "Đang xử lý", "Đã xử lý", "Đã đóng")

    val filteredRequests = when (uiState.selectedRequestTab) {
        "Đang xử lý" -> uiState.supportRequests.filter {
            it.status == "OPEN" || it.status == "IN_PROGRESS"
        }
        "Đã xử lý" -> uiState.supportRequests.filter { it.status == "RESOLVED" }
        "Đã đóng" -> uiState.supportRequests.filter { it.status == "CLOSED" }
        else -> uiState.supportRequests
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yêu cầu hỗ trợ của tôi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = viewModel::refreshSupportData,
                        enabled = !uiState.isLoading
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Tải lại")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            ScrollableTabRow(
                selectedTabIndex = tabs.indexOf(uiState.selectedRequestTab),
                edgePadding = LibrarySpacing.Medium
            ) {
                tabs.forEach { tab ->
                    Tab(
                        selected = uiState.selectedRequestTab == tab,
                        onClick = { viewModel.selectRequestTab(tab) },
                        text = { Text(tab, fontWeight = if (uiState.selectedRequestTab == tab) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(LibrarySpacing.Small))

            if (uiState.isLoading && uiState.supportRequests.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null && uiState.supportRequests.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(LibrarySpacing.Large),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.error.orEmpty(),
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(LibrarySpacing.Small))
                    Button(onClick = viewModel::refreshSupportData) {
                        Text("Thử lại")
                    }
                }
            } else if (filteredRequests.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không có yêu cầu nào.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = LibrarySpacing.Medium),
                    verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium)
                ) {
                    item { Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall)) }
                    items(filteredRequests, key = { it.id }) { req ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(LibrarySpacing.Medium)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = req.bookTitle ?: "Yêu cầu chung",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatusBadge(status = req.status)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Vấn đề: ${req.problemType}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = req.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ngày gửi: ${req.date}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )

                                if (!req.adminReply.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Phản hồi từ thư viện",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Success
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = req.adminReply,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    req.repliedDate?.let { repliedDate ->
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = repliedDate,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(LibrarySpacing.Medium)) }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val color = when (status) {
        "RESOLVED" -> Success
        "CLOSED" -> MaterialTheme.colorScheme.onSurfaceVariant
        "IN_PROGRESS" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.tertiary
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            text = when (status) {
                "OPEN" -> "Mới gửi"
                "IN_PROGRESS" -> "Đang xử lý"
                "RESOLVED" -> "Đã xử lý"
                "CLOSED" -> "Đã đóng"
                else -> status
            },
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
