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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group_7.library_management.components.BookListItemCard
import com.group_7.library_management.components.SearchBar
import com.group_7.library_management.models.Book
import com.group_7.library_management.models.UserBorrowSummary

import com.group_7.library_management.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onBookClick: (Book) -> Unit,
    onViewAllClick: (String) -> Unit = {},
    onOpenQRClick: () -> Unit = {},
    onNavigateToBorrowTab: (String) -> Unit = {},
    onNavigateToFavorite: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if(uiState.isLoadingBooks){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }else{
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = LibrarySpacing.Large),
            verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Large)
        ) {
            item { Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall)) }

            item {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    placeholder = "Tìm kiếm sách, tác giả,...",
                    onFilterClick = null,
                    showMic = true
                )
            }

            if (uiState.searchQuery.isNotBlank()) {
                val allBooks = (uiState.popularBooks + uiState.newBooks + uiState.recommendedBooks).distinctBy { it.id }
                val searchResults = allBooks.filter {
                    it.title.contains(uiState.searchQuery, ignoreCase = true) ||
                    it.author.contains(uiState.searchQuery, ignoreCase = true)
                }

                if (searchResults.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Không tìm thấy sách phù hợp",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(searchResults, key = { it.id }) { book ->
                        BookListItemCard(book = book, onClick = { onBookClick(book) })
                    }
                }
            } else {
                item {
                    BookListSection(
                        title = "Sách phổ biến",
                        actionText = "Xem tất cả",
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
                        actionText = "",
                        books = uiState.recommendedBooks,
                        onBookClick = onBookClick,
                        onActionClick = {}
                    )
                }
                item {
                    BorrowStatusSection(
                        summary = uiState.borrowSummary,
                        isLoading = uiState.isLoadingSummary,
                        onStatusClick = { tabKey ->
                            if (tabKey == "favorite") {
                                onNavigateToFavorite()
                            } else {
                                onNavigateToBorrowTab(tabKey)
                            }
                        }
                    )
                }
                item { QRCheckInCard(onOpenQRClick = onOpenQRClick) }
            }

            item { Spacer(modifier = Modifier.height(LibrarySpacing.Medium)) }
        }
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
            if (actionText.isNotEmpty() && books.isNotEmpty()) {
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

        if (books.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Không có ${title.lowercase()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium)
        ) {
            items(books) { book ->
                BookItemCard(
                    book = book,
                    onClick = { onBookClick(book) }
                )
            }
        }
    }
    }
}

@Composable
fun BookItemCard(book: Book,
                 onClick:()->Unit={} ){
    Column(modifier = Modifier.width(130.dp)
        .clickable{onClick()}) {
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
fun BorrowStatusSection(
    summary: UserBorrowSummary,
    isLoading: Boolean = false,
    onStatusClick: (String) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        StatusRowItem(
            icon = Icons.Default.LibraryBooks,
            title = "Đang mượn",
            count = summary.borrowingCount.toString(),
            iconTint = MaterialTheme.colorScheme.secondary,
            onClick = { onStatusClick("borrowing") }
        )
        StatusRowItem(
            icon = Icons.Default.Event,
            title = "Sắp đến hạn",
            count = summary.dueSoonCount.toString(),
            iconTint = WarningColor,
            onClick = { onStatusClick("borrowing") }
        )
        StatusRowItem(
            icon = Icons.Default.AddCircle,
            title = "Hàng chờ",
            count = summary.pendingPickupCount.toString(),
            iconTint = WarningColor,
            onClick = { onStatusClick("pending") }
        )
        StatusRowItem(
            icon = Icons.Default.Warning,
            title = "Quá hạn",
            count = summary.overdueCount.toString(),
            iconTint = ErrorColor,
            onClick = { onStatusClick("borrowing") }
        )
        StatusRowItem(
            icon = Icons.Default.Favorite,
            title = "Yêu thích",
            count = summary.favoriteCount.toString(),
            iconTint = ErrorColor,
            onClick = { onStatusClick("favorite") }
        )
        StatusRowItem(
            icon = Icons.Default.Done,
            title = "Đã mượn",
            count = summary.returnedCount.toString(),
            iconTint = SuccessColor,
            onClick = { onStatusClick("history") }
        )
    }
}

@Composable
fun StatusRowItem(
    icon: ImageVector,
    title: String,
    count: String,
    iconTint: Color,
    bgColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    onClick: () -> Unit = {}
) {
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
fun QRCheckInCard(onOpenQRClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenQRClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LibrarySpacing.Large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LibrarySpacing.Large)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Quét mã QR tại đây",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Check-in nhanh hoặc mượn/trả sách tự động",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.QrCode2,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
