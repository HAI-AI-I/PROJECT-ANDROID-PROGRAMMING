package com.group_7.library_management.ui.book

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.group_7.library_management.components.BookListItemCard
import com.group_7.library_management.components.SearchBar
import com.group_7.library_management.models.Book
import com.group_7.library_management.ui.theme.LibrarySpacing
import kotlinx.coroutines.flow.distinctUntilChanged

private val QUICK_GENRE_TABS = listOf("Tất cả", "Lập trình", "Khoa học máy tính", "Cơ sở dữ liệu", "Mạng máy tính")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    modifier: Modifier = Modifier,
    viewModel: BookViewModel = hiltViewModel(),
    scrollToTopSignal: Int = 0,
    onBookClick: (Book) -> Unit = {},
    initialFilter: String? = null,
    onInitialFilterApplied: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val bookListState = rememberLazyListState()
    val lifecycleOwner = LocalLifecycleOwner.current
    var showQuickFilters by remember { mutableStateOf(true) }
    val quickFiltersScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source == NestedScrollSource.UserInput) {
                    when {
                        available.y < 0f -> showQuickFilters = false
                        available.y > 0f -> showQuickFilters = true
                    }
                }
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(initialFilter) {
        initialFilter?.let {
            viewModel.applyInitialFilter(it)
            onInitialFilterApplied()
        }
    }

    LaunchedEffect(
        uiState.searchQuery,
        uiState.quickGenre,
        uiState.quickStatus,
        uiState.filter
    ) {
        bookListState.scrollToItem(0)
        showQuickFilters = true
    }

    LaunchedEffect(scrollToTopSignal) {
        if (scrollToTopSignal > 0) {
            bookListState.animateScrollToItem(0)
            showQuickFilters = true
        }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshBooks()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(bookListState, uiState.filteredBooks.size, uiState.hasMore) {
        snapshotFlow {
            bookListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
        }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (uiState.filteredBooks.isNotEmpty()
                    && lastVisibleIndex >= uiState.filteredBooks.lastIndex - 4
                ) {
                    viewModel.loadNextPage()
                }
            }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = LibrarySpacing.Medium)
                .nestedScroll(quickFiltersScrollConnection),
        ) {
            Spacer(Modifier.height(LibrarySpacing.Small))

            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholder = "Tìm sách, tác giả...",
                onFilterClick = { viewModel.setFilterSheetVisible(true)
                                },
                showMic = true
            )

            AnimatedVisibility(
                visible = showQuickFilters,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(LibrarySpacing.Small))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(QUICK_GENRE_TABS) { tab ->
                            FilterChip(
                                selected = uiState.quickGenre == tab,
                                onClick = { viewModel.onQuickGenreChange(tab) },
                                label = { Text(tab) }
                            )
                        }
                    }

                    Spacer(Modifier.height(LibrarySpacing.Small))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Trạng thái:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = uiState.quickStatus == "available",
                            onClick = { viewModel.onQuickStatusToggle("available") },
                            label = { Text("Sẵn có") }
                        )
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = uiState.quickStatus == "borrowed",
                            onClick = { viewModel.onQuickStatusToggle("borrowed") },
                            label = { Text("Đang mượn") }
                        )
                    }

                    Spacer(Modifier.height(LibrarySpacing.Medium))
                }
            }

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = LibrarySpacing.Small)
                )
            }

            if (uiState.isLoading && uiState.filteredBooks.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.filteredBooks.isEmpty()) {
                EmptyBooksState(
                    onViewPopularClick = viewModel::resetFilters
                )
            } else {
                LazyColumn(
                    state = bookListState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Small)
                ) {
                    items(uiState.filteredBooks, key = { it.id }) { book ->
                        BookListItemCard(
                            book = book,
                            onClick = { onBookClick(book) },
                            onBorrowClick = { onBookClick(book) },
                        )
                    }
                    if (uiState.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(LibrarySpacing.Medium),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(28.dp))
                            }
                        }
                    }
                }
            }
        }

        if (uiState.showFilterSheet) {
            BookFilterBottomSheet(
                initialFilter = uiState.filter,
                onDismiss = { viewModel.setFilterSheetVisible(false) },
                onApply = viewModel::onApplyFilter
            )
        }
    }
}
