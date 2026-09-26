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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group_7.library_management.components.BookListItemCard
import com.group_7.library_management.components.BookCoverImage
import com.group_7.library_management.components.SearchBar
import com.group_7.library_management.models.Book
import com.group_7.library_management.models.UserBorrowSummary

import com.group_7.library_management.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    scrollToTopSignal: Int = 0,
    onBookClick: (Book) -> Unit,
    onViewAllClick: (String) -> Unit = {},
    onOpenQRClick: () -> Unit = {},
    onNavigateToBorrowTab: (String) -> Unit = {},
    onNavigateToFavorite: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val homeListState = rememberLazyListState()

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.resetSearch()
                viewModel.refreshBooks()
                viewModel.refreshBorrowSummary()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(uiState.searchQuery) {
        homeListState.scrollToItem(0)
    }

    LaunchedEffect(scrollToTopSignal) {
        if (scrollToTopSignal > 0) homeListState.animateScrollToItem(0)
    }

    LaunchedEffect(
        homeListState,
        uiState.searchQuery,
        uiState.searchResults.size,
        uiState.searchHasMore
    ) {
        snapshotFlow {
            val layoutInfo = homeListState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            lastVisibleIndex to layoutInfo.totalItemsCount
        }
            .distinctUntilChanged()
            .collect { (lastVisibleIndex, totalItemsCount) ->
                if (uiState.searchQuery.isNotBlank() &&
                    uiState.searchResults.isNotEmpty() &&
                    totalItemsCount > 0 &&
                    lastVisibleIndex >= totalItemsCount - 4
                ) {
                    viewModel.loadNextSearchPage()
                }
            }
    }

    if(uiState.isLoadingBooks){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }else{
        LazyColumn(
            state = homeListState,
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

            if (uiState.searchQuery.isBlank()) uiState.bookLoadError?.let { message ->
                item {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (uiState.searchQuery.isNotBlank()) {
                uiState.searchError?.let { message ->
                    item {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (uiState.isSearching && uiState.searchResults.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                } else if (uiState.searchResults.isEmpty()) {
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
                    items(uiState.searchResults, key = { it.id }) { book ->
                        BookListItemCard(book = book, onClick = { onBookClick(book) })
                    }
                }

                if (uiState.isLoadingMoreSearch) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = LibrarySpacing.Medium),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
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
                        errorMessage = uiState.summaryLoadError,
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
            BookCoverImage(
                coverImageUrl = book.coverImageUrl,
                contentDescription = "Bìa sách ${book.title}",
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
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
    errorMessage: String? = null,
    onStatusClick: (String) -> Unit = {}
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = LibrarySpacing.Large),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (errorMessage != null) {
        Text(
            text = errorMessage,
            modifier = Modifier.fillMaxWidth().padding(vertical = LibrarySpacing.Medium),
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        return
    }

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
            onClick = { onStatusClick("due_soon") }
        )
        StatusRowItem(
            icon = Icons.Default.AddCircle,
            title = "Chờ nhận",
            count = summary.pendingPickupCount.toString(),
            iconTint = WarningColor,
            onClick = { onStatusClick("pending") }
        )
        StatusRowItem(
            icon = Icons.Default.Warning,
            title = "Quá hạn",
            count = summary.overdueCount.toString(),
            iconTint = ErrorColor,
            onClick = { onStatusClick("overdue") }
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
            title = "Đã trả",
            count = summary.returnedCount.toString(),
            iconTint = SuccessColor,
            onClick = { onStatusClick("returned") }
        )
        StatusRowItem(
            icon = Icons.Default.History,
            title = "Lịch sử mượn",
            count = summary.allBorrowCount.toString(),
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
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LibrarySpacing.Large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "QR Check-in",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Xuất trình mã QR của bạn tại quầy thủ thư hoặc cổng tự động để vào thư viện hoặc mượn sách.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))
            Button(
                onClick = onOpenQRClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mở QR", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))
            Surface(
                modifier = Modifier
                    .size(130.dp)
                    .clip(MaterialTheme.shapes.medium),
                color = Color.White,
                tonalElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = "QR Code",
                        tint = Color.Black,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
        }
    }
}
