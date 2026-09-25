package com.group_7.library_management.ui.borrowing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.group_7.library_management.components.BorrowOrderItemCard
import com.group_7.library_management.components.BorrowingTopBar
import com.group_7.library_management.models.BorrowOrder
import com.group_7.library_management.ui.theme.LibrarySpacing
import java.time.Duration
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowRecordListScreen(title: String, onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { innerPadding ->
        BorrowRecordListContent(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun BorrowRecordListContent(
    modifier: Modifier = Modifier,
    initialTab: BorrowTab = BorrowTab.ALL,
    onOrderClick: (Long) -> Unit = {},
    viewModel: BorrowRecordListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabName by rememberSaveable { mutableStateOf(initialTab.name) }
    var orderToCancel by remember { mutableStateOf<BorrowOrder?>(null) }
    val selectedTab = BorrowTab.entries.firstOrNull { it.name == selectedTabName } ?: BorrowTab.ALL

    LaunchedEffect(initialTab) { selectedTabName = initialTab.name }

    val filteredOrders = remember(state.orders, selectedTab) {
        state.orders.filter { order ->
            when (selectedTab) {
                BorrowTab.ALL -> true
                BorrowTab.PENDING -> order.status == "PENDING_PAYMENT" || order.status == "REQUESTED"
                BorrowTab.BORROWING -> order.status == "BORROWED" && !order.isDueSoon()
                BorrowTab.DUE_SOON -> order.status == "BORROWED" && order.isDueSoon()
                BorrowTab.OVERDUE -> order.status == "OVERDUE"
                BorrowTab.RETURNED -> order.status == "RETURNED"
            }
        }
    }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BorrowingTopBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTabName = it.name }
        )

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.errorMessage != null -> BorrowListError(
                message = requireNotNull(state.errorMessage),
                onRetry = viewModel::refresh
            )
            filteredOrders.isEmpty() -> EmptyBorrowList(selectedTab)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = LibrarySpacing.Medium),
                verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium)
            ) {
                item { Spacer(Modifier.height(LibrarySpacing.Small)) }
                items(filteredOrders, key = { it.id }) { order ->
                    BorrowOrderItemCard(
                        order = order,
                        onClick = { onOrderClick(order.id) },
                        onCancelClick = if (order.canCancel()) {
                            { orderToCancel = order }
                        } else {
                            null
                        },
                        isCancelling = state.cancellingOrderId == order.id
                    )
                }
                item { Spacer(Modifier.height(LibrarySpacing.Medium)) }
            }
        }
    }

    orderToCancel?.let { order ->
        AlertDialog(
            onDismissRequest = { orderToCancel = null },
            title = { Text("Xác nhận hủy đơn") },
            text = {
                Text("Bạn có chắc muốn hủy đơn ${order.referenceCode}? Lượt hủy trong tháng sẽ bị trừ.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        orderToCancel = null
                        viewModel.cancelOrder(order.id)
                    }
                ) {
                    Text("Hủy đơn", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToCancel = null }) {
                    Text("Không")
                }
            }
        )
    }
}

@Composable
private fun BorrowListError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry) { Text("Thử lại") }
    }
}

@Composable
private fun EmptyBorrowList(tab: BorrowTab) {
    Box(Modifier.fillMaxSize().padding(LibrarySpacing.Large), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Bookmark,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(LibrarySpacing.Medium))
            Text("Không có đơn ${tab.title.lowercase()}", fontWeight = FontWeight.Bold)
        }
    }
}

private fun BorrowOrder.isDueSoon(now: Instant = Instant.now()): Boolean {
    if (status != "BORROWED") return false
    val due = runCatching { Instant.parse(dueAt) }.getOrNull() ?: return false
    val remaining = Duration.between(now, due)
    return !remaining.isNegative && remaining <= Duration.ofDays(DUE_SOON_DAYS)
}

private fun BorrowOrder.canCancel(now: Instant = Instant.now()): Boolean {
    if (status != "PENDING_PAYMENT" && status != "REQUESTED") return false
    val requested = runCatching { Instant.parse(requestedAt) }.getOrNull() ?: return false
    val elapsed = Duration.between(requested, now)
    return !elapsed.isNegative && elapsed <= Duration.ofHours(24)
}
private const val DUE_SOON_DAYS = 1L
