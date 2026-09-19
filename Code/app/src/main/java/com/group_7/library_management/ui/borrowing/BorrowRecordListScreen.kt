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
import com.group_7.library_management.components.BookListItemCard
import com.group_7.library_management.components.BorrowingTopBar
import com.group_7.library_management.models.Book
import com.group_7.library_management.models.BorrowOrder
import com.group_7.library_management.ui.theme.LibrarySpacing
import com.group_7.library_management.ui.theme.Success
import com.group_7.library_management.ui.theme.Warning
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
    viewModel: BorrowRecordListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabName by rememberSaveable { mutableStateOf(initialTab.name) }
    val selectedTab = BorrowTab.entries.firstOrNull { it.name == selectedTabName } ?: BorrowTab.ALL

    LaunchedEffect(initialTab) { selectedTabName = initialTab.name }

    val filteredOrders = remember(state.orders, selectedTab) {
        state.orders.filter { order ->
            when (selectedTab) {
                BorrowTab.ALL -> true
                BorrowTab.PENDING -> order.status == "REQUESTED"
                BorrowTab.BORROWING -> order.status == "BORROWED" && !order.isDueSoon()
                BorrowTab.DUE_SOON -> order.status == "BORROWED" && order.isDueSoon()
                BorrowTab.OVERDUE -> order.status == "OVERDUE"
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
                    BookListItemCard(
                        book = Book(
                            id = order.bookId.toString(),
                            title = order.bookTitle,
                            author = order.bookAuthor,
                            category = "",
                            coverImageUrl = order.coverImageUrl,
                            borrowFee = order.borrowFee
                        ),
                        subtitleOverride = orderSubtitle(order),
                        trailingContent = { StatusBadge(order) }
                    )
                }
                item { Spacer(Modifier.height(LibrarySpacing.Medium)) }
            }
        }
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

@Composable
private fun StatusBadge(order: BorrowOrder) {
    val (label, color) = when {
        order.status == "REQUESTED" -> "Chờ nhận" to MaterialTheme.colorScheme.primary
        order.status == "BORROWED" && order.isDueSoon() -> "Sắp đến hạn" to Warning
        order.status == "BORROWED" -> "Đang mượn" to MaterialTheme.colorScheme.primary
        order.status == "OVERDUE" -> "Quá hạn" to MaterialTheme.colorScheme.error
        order.status == "RETURNED" -> "Đã trả" to Success
        else -> "Đã hủy" to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(color = color.copy(alpha = 0.12f), shape = MaterialTheme.shapes.extraSmall) {
        Text(
            label,
            color = color,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

private fun BorrowOrder.isDueSoon(now: Instant = Instant.now()): Boolean {
    if (status != "BORROWED") return false
    val due = runCatching { Instant.parse(dueAt) }.getOrNull() ?: return false
    val remaining = Duration.between(now, due)
    return !remaining.isNegative && remaining <= Duration.ofDays(DUE_SOON_DAYS)
}

private fun orderSubtitle(order: BorrowOrder): String {
    val start = order.borrowedAt ?: order.requestedAt
    val prefix = if (order.status == "REQUESTED") "Đặt" else "Mượn"
    return "$prefix: ${formatDate(start)} · Hạn trả: ${formatDate(order.dueAt)}"
}

private fun formatDate(value: String): String = runCatching {
    DATE_FORMATTER.format(Instant.parse(value))
}.getOrDefault(value)

private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    .withZone(ZoneId.systemDefault())
private const val DUE_SOON_DAYS = 1L
