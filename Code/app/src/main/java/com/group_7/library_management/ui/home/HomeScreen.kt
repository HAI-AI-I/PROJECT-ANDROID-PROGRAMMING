package com.group_7.library_management.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group_7.library_management.components.SearchBar
import com.group_7.library_management.models.Book
import com.group_7.library_management.ui.borrowing.BorrowTab
import com.group_7.library_management.ui.theme.*

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    onBookClick: (Book) -> Unit = {},
    onViewAllClick: (String) -> Unit = {},
    onOpenQRClick: () -> Unit = {},
    onBorrowStatusClick: (BorrowTab) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = LibrarySpacing.Large),
        verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Large)
    ) {
        item { Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall)) }

        item {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Tìm kiếm sách, tác giả,...",
                onFilterClick = null,
                showMic = true
            )
        }

        item {
            BookListSection(
                title = "Sách phổ biến",
                actionText = "Xem tất cả>",
                books = uiState.popularBooks ,
                onBookClick = onBookClick,
                onActionClick = { onViewAllClick("popular") }
            )
        }
        item {
            BookListSection(
                title = "Sách mới",
                actionText = "Xem tất cả",
                books = uiState.newBooks,
                onBookClick = onBookClick,
                onActionClick = { onViewAllClick("new")}
            )
        }
        item {
            BookListSection(
                title = "Sách dành cho bạn",
                actionText = "Xem tất cả",
                books = uiState.recommendedBooks,
                onBookClick = onBookClick,
                onActionClick = { onViewAllClick("recommended") }
            )
        }

        item {
            BorrowStatusSection(
                borrowingCount = uiState.borrowingCount,
                dueSoonCount = uiState.dueSoonCount,
                overdueCount = uiState.overdueCount,
                onStatusClick = onBorrowStatusClick
            )
        }

        item { QRCheckInCard(onOpenQRClick = onOpenQRClick) }
        item { Spacer(modifier = Modifier.height(LibrarySpacing.Medium)) }
    }
}

@Composable
fun BorrowStatusSection(
    borrowingCount: Int,
    dueSoonCount: Int,
    overdueCount: Int,
    onStatusClick: (BorrowTab) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        StatusRowItem(
            icon = Icons.Default.LibraryBooks,
            title = "Đang mượn",
            count = borrowingCount.toString(),
            iconTint = MaterialTheme.colorScheme.secondary,
            bgColor = MaterialTheme.colorScheme.surfaceVariant,
            onClick = { onStatusClick(BorrowTab.BORROWING) }
        )
        StatusRowItem(
            icon = Icons.Default.Event,
            title = "Sắp đến hạn",
            count = dueSoonCount.toString(),
            iconTint = WarningColor,
            bgColor = MaterialTheme.colorScheme.surfaceVariant,
            onClick = { onStatusClick(BorrowTab.BORROWING) } // Vẫn thuộc Tab Đang mượn
        )
        StatusRowItem(
            icon = Icons.Default.Warning,
            title = "Quá hạn",
            count = overdueCount.toString(),
            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            bgColor = MaterialTheme.colorScheme.surfaceVariant,
            onClick = { onStatusClick(BorrowTab.BORROWING) } // Vẫn thuộc Tab Đang mượn
        )
    }
}

@Composable
fun StatusRowItem(icon: ImageVector, title: String, count: String, iconTint: Color, bgColor: Color, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.extraLarge)
            .clickable { onClick() }
            .padding(horizontal = LibrarySpacing.Large, vertical = LibrarySpacing.Medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            text = count,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun BookListSection(title: String, actionText: String, books: List<Book>,
                    onBookClick: (Book) -> Unit = {},
                    onActionClick: () -> Unit = {}) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (actionText.isNotEmpty()) {
                TextButton(
                    onClick = {onActionClick()}
                ){
                    Text(
                        text = actionText,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium)
        ) {
            items(books) { book ->
                BookItemCard(book=book,onClick={onBookClick(book)})
            }
        }
    }
}

@Composable
fun BookItemCard(book: Book,
                 onClick:()->Unit={} ){
    Column(modifier = Modifier.width(130.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Book,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = book.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = book.author,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
      )
    }
}

@Composable
fun QRCheckInCard(
    onOpenQRClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier.padding(LibrarySpacing.Large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "QR Check-in",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Xuất trình mã QR của bạn tại quầy thủ thư hoặc cổng tự động để vào thư viện hoặc mượn sách.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(LibrarySpacing.Large))
            Button(
                onClick =onOpenQRClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondary)
                Spacer(modifier = Modifier.width(LibrarySpacing.Small))
                Text("Mở QR", color = MaterialTheme.colorScheme.onSecondary, style = MaterialTheme.typography.labelLarge)
            }
            Spacer(modifier = Modifier.height(LibrarySpacing.Large))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(topStart = LibrarySpacing.Medium, topEnd = LibrarySpacing.Medium))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(LibrarySpacing.Small),
                contentAlignment = Alignment.TopCenter
            ) {
                Icon(
                    Icons.Default.QrCode2,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
