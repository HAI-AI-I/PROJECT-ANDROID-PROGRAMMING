package com.group_7.library_management.ui.borrowing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.group_7.library_management.components.BookListItemCard
import com.group_7.library_management.models.Book
import com.group_7.library_management.ui.theme.Error
import com.group_7.library_management.ui.theme.LibrarySpacing
import com.group_7.library_management.ui.theme.Success
import com.group_7.library_management.ui.theme.TextSecondary

/**
 * BorrowRecordListScreen
 * Theo đúng lời leader: "Lịch sử mượn sách" và "Sách của tôi" dùng CHUNG 1
 * file này (view giống nhau), chỉ khác tiêu đề trang — truyền qua tham số
 * `title`. Tái dùng thẳng `BookListItemCard` (component dùng chung) với
 * `subtitleOverride` (ngày mượn/trả) + `trailingContent` (badge Đã trả/Quá hạn).
 *
 * Gọi cho "Lịch sử mượn sách":  BorrowRecordListScreen(title = "Lịch sử mượn sách", records = ...)
 * Gọi cho "Sách của tôi":       BorrowRecordListScreen(title = "Sách của tôi", records = ...)
 */

enum class BorrowStatus { BORROWING, RETURNED, OVERDUE }

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
    // TODO: thay bằng val records by viewModel.borrowRecords.collectAsState()
    val records = remember {
        listOf(
            BorrowRecord(
                Book("1", "Clean Architecture", "Robert C. Martin", "Lập trình", borrowFee = 180_000, availableCopies = 0),
                "01/08/2026", "15/08/2026", BorrowStatus.OVERDUE,
            ),
            BorrowRecord(
                Book("2", "Design Patterns", "Gang of Four", "Lập trình", borrowFee = 150_000, availableCopies = 0),
                "05/08/2026", "19/08/2026", BorrowStatus.BORROWING,
            ),
            BorrowRecord(
                Book("3", "Deep Learning", "Ian Goodfellow", "Khoa học máy tính", borrowFee = 200_000, availableCopies = 1),
                "20/07/2026", "03/08/2026", BorrowStatus.RETURNED,
            ),
        )
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Tất cả", "Đang mượn", "Đã trả")
    val filtered = when (selectedTab) {
        1 -> records.filter { it.status == BorrowStatus.BORROWING || it.status == BorrowStatus.OVERDUE }
        2 -> records.filter { it.status == BorrowStatus.RETURNED }
        else -> records
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(title) }) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, label ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(label) })
                }
            }

            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có mục nào.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(LibrarySpacing.Medium),
                    verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Small),
                ) {
                    items(filtered, key = { it.book.id }) { record ->
                        BookListItemCard(
                            book = record.book,
                            subtitleOverride = "Mượn: ${record.borrowDate} · Hạn trả: ${record.dueDate}",
                            trailingContent = { StatusBadge(record.status) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: BorrowStatus) {
    val (label, color) = when (status) {
        BorrowStatus.BORROWING -> "Đang mượn" to TextSecondary
        BorrowStatus.RETURNED -> "Đã trả" to Success
        BorrowStatus.OVERDUE -> "Quá hạn" to Error
    }
    Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
}
