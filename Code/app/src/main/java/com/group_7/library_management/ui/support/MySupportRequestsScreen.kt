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

    val tabs = listOf("Tất cả", "Đang xử lý", "Đã giải quyết")

    val filteredRequests = when (uiState.selectedRequestTab) {
        "Đang xử lý" -> uiState.supportRequests.filter { it.status == "Đang xử lý" }
        "Đã giải quyết" -> uiState.supportRequests.filter { it.status == "Đã giải quyết" }
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

            if (filteredRequests.isEmpty()) {
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
                                        text = req.bookTitle,
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
    val isDone = status == "Đã giải quyết"
    val color = if (isDone) Success else MaterialTheme.colorScheme.tertiary
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
