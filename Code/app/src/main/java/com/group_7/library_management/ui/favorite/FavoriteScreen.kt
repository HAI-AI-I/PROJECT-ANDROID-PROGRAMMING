package com.group_7.library_management.ui.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.group_7.library_management.components.BookListItemCard
import com.group_7.library_management.models.Book
import com.group_7.library_management.ui.theme.LibrarySpacing

@Composable
fun FavoriteScreen(
    scrollToTopSignal: Int = 0,
    onBookClick: (Book) -> Unit = {},
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(scrollToTopSignal) {
        if (scrollToTopSignal > 0) listState.animateScrollToItem(0)
    }

    when {
        state.isLoading && state.books.isEmpty() -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        state.errorMessage != null && state.books.isEmpty() -> Column(
            modifier = Modifier.fillMaxSize().padding(LibrarySpacing.Large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = requireNotNull(state.errorMessage),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(LibrarySpacing.Medium))
            Button(onClick = viewModel::refresh) { Text("Thử lại") }
        }

        state.books.isEmpty() -> EmptyFavoriteState()

        else -> LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = LibrarySpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Small)
        ) {
            item { Spacer(Modifier.height(LibrarySpacing.Small)) }
            items(items = state.books, key = { it.id }) { book ->
                BookListItemCard(
                    book = book,
                    onClick = { onBookClick(book) },
                    onBorrowClick = { onBookClick(book) },
                    isFavorite = true,
                    onFavoriteClick = { viewModel.removeFavorite(book) },
                    onRemoveClick = { viewModel.removeFavorite(book) },
                    onMoreClick = { onBookClick(book) }
                )
            }
            item { Spacer(Modifier.height(LibrarySpacing.Medium)) }
        }
    }
}

@Composable
private fun EmptyFavoriteState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(LibrarySpacing.Large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(LibrarySpacing.Medium))
        Text(
            text = "Chưa có sách yêu thích",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(LibrarySpacing.Small))
        Text(
            text = "Các sách bạn yêu thích sẽ xuất hiện ở đây.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
