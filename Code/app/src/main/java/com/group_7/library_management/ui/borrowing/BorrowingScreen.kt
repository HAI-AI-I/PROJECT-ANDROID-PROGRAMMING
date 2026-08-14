package com.group_7.library_management.ui.borrowing

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.group_7.library_management.ui.theme.LibrarySpacing

data class BorrowingBook(
    val id: Int,
    val title: String,
    val author: String,
    val borrowDate: String,
    val dueDate: String,
    val status: BorrowingStatus
)

enum class BorrowingStatus {
    BORROWING,
    OVERDUE,
    RETURNED
}

@Composable
fun BorrowingScreen(
    onScanQrClick: () -> Unit = {}
) {

    var selectedTab by remember {
        mutableStateOf(0)
    }

    val borrowingBooks = remember {
        mutableStateListOf(
            BorrowingBook(
                id = 1,
                title = "Đắc Nhân Tâm",
                author = "Dale Carnegie",
                borrowDate = "10/08/2026",
                dueDate = "24/08/2026",
                status = BorrowingStatus.BORROWING
            ),
            BorrowingBook(
                id = 2,
                title = "Clean Code",
                author = "Robert C. Martin",
                borrowDate = "01/08/2026",
                dueDate = "08/08/2026",
                status = BorrowingStatus.OVERDUE
            ),
            BorrowingBook(
                id = 3,
                title = "Nhà Giả Kim",
                author = "Paulo Coelho",
                borrowDate = "20/07/2026",
                dueDate = "03/08/2026",
                status = BorrowingStatus.RETURNED
            )
        )
    }

    val filteredBooks = when (selectedTab) {
        0 -> borrowingBooks.filter {
            it.status == BorrowingStatus.BORROWING ||
                    it.status == BorrowingStatus.OVERDUE
        }

        else -> borrowingBooks.filter {
            it.status == BorrowingStatus.RETURNED
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mượn sách",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = onScanQrClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Quét QR"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            TabRow(
                selectedTabIndex = selectedTab
            ) {

                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                    },
                    text = {
                        Text("Đang mượn")
                    }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                    },
                    text = {
                        Text("Lịch sử")
                    }
                )
            }

            if (selectedTab == 0) {

                Button(
                    onClick = onScanQrClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(LibrarySpacing.Medium)
                        .height(52.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.size(LibrarySpacing.Small)
                    )

                    Text("QUÉT QR ĐỂ MƯỢN SÁCH")
                }
            }

            if (filteredBooks.isEmpty()) {

                EmptyBorrowingView(
                    selectedTab = selectedTab
                )

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(
                        LibrarySpacing.Medium
                    )
                ) {

                    item {
                        Spacer(
                            modifier = Modifier.height(
                                LibrarySpacing.Small
                            )
                        )
                    }

                    items(
                        items = filteredBooks,
                        key = {
                            it.id
                        }
                    ) { book ->

                        BorrowingBookItem(
                            book = book,
                            onReturnClick = {

                                val index = borrowingBooks.indexOfFirst {
                                    it.id == book.id
                                }

                                if (index != -1) {

                                    borrowingBooks[index] =
                                        borrowingBooks[index].copy(
                                            status = BorrowingStatus.RETURNED
                                        )
                                }
                            }
                        )
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(
                                LibrarySpacing.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BorrowingBookItem(
    book: BorrowingBook,
    onReturnClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LibrarySpacing.Medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LibrarySpacing.Medium)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "Sách",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.size(
                        LibrarySpacing.Medium
                    )
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(
                            LibrarySpacing.ExtraSmall
                        )
                    )

                    Text(
                        text = book.author,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(
                    LibrarySpacing.Medium
                )
            )

            Text(
                text = "Ngày mượn: ${book.borrowDate}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(
                modifier = Modifier.height(
                    LibrarySpacing.ExtraSmall
                )
            )

            Text(
                text = "Hạn trả: ${book.dueDate}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(
                modifier = Modifier.height(
                    LibrarySpacing.Small
                )
            )

            BorrowingStatusView(
                status = book.status
            )

            if (
                book.status == BorrowingStatus.BORROWING ||
                book.status == BorrowingStatus.OVERDUE
            ) {

                Spacer(
                    modifier = Modifier.height(
                        LibrarySpacing.Medium
                    )
                )

                OutlinedButton(
                    onClick = onReturnClick,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text("TRẢ SÁCH")
                }
            }
        }
    }
}

@Composable
private fun BorrowingStatusView(
    status: BorrowingStatus
) {

    when (status) {

        BorrowingStatus.BORROWING -> {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(
                    modifier = Modifier.size(
                        LibrarySpacing.Small
                    )
                )

                Text(
                    text = "Đang mượn",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        BorrowingStatus.OVERDUE -> {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(
                    modifier = Modifier.size(
                        LibrarySpacing.Small
                    )
                )

                Text(
                    text = "Đã quá hạn",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        BorrowingStatus.RETURNED -> {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(
                    modifier = Modifier.size(
                        LibrarySpacing.Small
                    )
                )

                Text(
                    text = "Đã trả",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmptyBorrowingView(
    selectedTab: Int
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(LibrarySpacing.Large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(
                LibrarySpacing.Medium
            )
        )

        Text(
            text = if (selectedTab == 0)
                "Bạn chưa mượn sách nào"
            else
                "Chưa có lịch sử mượn sách",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
