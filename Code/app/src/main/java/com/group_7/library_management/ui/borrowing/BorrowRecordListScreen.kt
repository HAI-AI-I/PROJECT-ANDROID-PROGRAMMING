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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.group_7.library_management.components.BookListItemCard
import com.group_7.library_management.components.BorrowingTopBar
import com.group_7.library_management.models.Book
import com.group_7.library_management.ui.theme.LibrarySpacing
import com.group_7.library_management.ui.theme.Success

enum class BorrowStatus { PENDING, BORROWING, RETURNED, OVERDUE }

data class BorrowRecord(
    val book: Book,
    val borrowDate: String,
    val dueDate: String,
    val status: BorrowStatus,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowRecordListScreen(
    title: String,
    onBack: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        BorrowRecordListContent(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun BorrowRecordListContent(
    modifier: Modifier = Modifier,
    initialTab: BorrowTab = BorrowTab.ALL
) {
    val records = remember {
        listOf(
            BorrowRecord(
                Book("1", "Clean Architecture", "Robert C. Martin", "Lập trình", borrowFee = 180000, availableCopies = 0),
                "01/08/2026", "15/08/2026", BorrowStatus.OVERDUE,
            ),
            BorrowRecord(
                Book("2", "Design Patterns", "Gang of Four", "Lập trình", borrowFee = 150000, availableCopies = 0),
                "05/08/2026", "19/08/2026", BorrowStatus.BORROWING,
            ),
            BorrowRecord(
                Book("6", "Mạng máy tính căn bản", "Lê Văn C", "Mạng máy tính", borrowFee = 100000, availableCopies = 5),
                "20/07/2026", "03/08/2026", BorrowStatus.RETURNED,
            ),
            BorrowRecord(
                Book("7", "Code Dạo Ký Sự", "Phạm Huy Hoàng", "Lập trình", borrowFee = 50000, availableCopies = 2),
                "22/08/2026", "05/09/2026", BorrowStatus.PENDING,
            )
        )
    }

    var selectedTab by remember { mutableStateOf(initialTab) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(initialTab) {
        selectedTab = initialTab
    }

    val tabFiltered = when (selectedTab) {
        BorrowTab.PENDING -> records.filter { it.status == BorrowStatus.PENDING }
        BorrowTab.BORROWING -> records.filter { it.status == BorrowStatus.BORROWING || it.status == BorrowStatus.OVERDUE }
        BorrowTab.ALL -> records
    }

    val filtered = if (searchQuery.isBlank()) {
        tabFiltered
    } else {
        tabFiltered.filter {
            it.book.title.contains(searchQuery, ignoreCase = true) ||
                    it.book.author.contains(searchQuery, ignoreCase = true)
        }
    }

    val tabs = BorrowTab.entries

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TabRow(selectedTabIndex = selectedTab.ordinal) {
            tabs.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = { Text(tab.title, fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        Spacer(modifier = Modifier.height(LibrarySpacing.Small))


        Spacer(modifier = Modifier.height(LibrarySpacing.Small))

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(LibrarySpacing.Large),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(LibrarySpacing.Medium))
                    Text(
                        text = "Không tìm thấy phiếu mượn sách phù hợp",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall))
                    Text(
                        text = "Hãy thử tìm kiếm với từ khóa khác hoặc chuyển sang tab khác.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = LibrarySpacing.Medium),
                verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium),
            ) {
                item { Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall)) }
                items(filtered, key = { it.book.id }) { record ->
                    BookListItemCard(
                        book = record.book,
                        subtitleOverride = "Mượn: ${record.borrowDate} · Hạn trả: ${record.dueDate}",
                        trailingContent = { StatusBadge(record.status) },
                    )
                }
                item { Spacer(modifier = Modifier.height(LibrarySpacing.Medium)) }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: BorrowStatus) {
    val (label, color) = when (status) {
        BorrowStatus.PENDING -> "Hàng chờ" to MaterialTheme.colorScheme.outline
        BorrowStatus.BORROWING -> "Đang mượn" to MaterialTheme.colorScheme.onSurfaceVariant
        BorrowStatus.RETURNED -> "Đã trả" to Success
        BorrowStatus.OVERDUE -> "Quá hạn" to MaterialTheme.colorScheme.error
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
