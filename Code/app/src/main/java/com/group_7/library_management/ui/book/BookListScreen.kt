package com.group_7.library_management.ui.book

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group_7.library_management.components.BookListItemCard
import com.group_7.library_management.components.SearchBar
import com.group_7.library_management.models.Book
import com.group_7.library_management.ui.theme.LibrarySpacing

private val QUICK_GENRE_TABS = listOf("Tất cả", "Lập trình", "Khoa học máy tính", "Cơ sở dữ liệu", "Mạng máy tính")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    modifier: Modifier = Modifier,
    viewModel: BookViewModel = viewModel(),
    onBookClick: (Book) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = LibrarySpacing.Medium),
        ) {
            Spacer(Modifier.height(LibrarySpacing.Small))
            Text("Sách", style = MaterialTheme.typography.headlineSmall)

            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholder = "Tìm sách, tác giả...",
                onFilterClick = { viewModel.setFilterSheetVisible(true) },
                showMic = true
            )

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

            if (uiState.filteredBooks.isEmpty()) {
                EmptyBooksState(
                    onViewPopularClick = viewModel::resetFilters
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Small)
                ) {
                    items(uiState.filteredBooks, key = { it.id }) { book ->
                        BookListItemCard(book = book, onClick = { onBookClick(book) })
                    }
                }
            }
        }

        if(uiState.showFilterSheet) {
//            BookFilterBottomSheet(
//                initialFilter = uiState.filter,
//                onDismiss = { viewModel.setFilterSheetVisible(false) },
//                onApply = viewModel::onApplyFilter
//            )
        }
    }
}
