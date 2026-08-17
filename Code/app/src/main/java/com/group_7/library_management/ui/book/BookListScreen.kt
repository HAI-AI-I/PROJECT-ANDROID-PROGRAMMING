package com.group_7.library_management.ui.book

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.group_7.library_management.components.BookListItemCard
import com.group_7.library_management.models.Book
import com.group_7.library_management.ui.theme.LibrarySpacing

private val QUICK_GENRE_TABS = listOf("Tất cả", "Lập trình", "Khoa học máy tính", "Cơ sở dữ liệu", "Mạng máy tính")

/**
 * BookListContent
 * Chỉ chứa nội dung danh sách sách, để HomeScreen có thể nhúng vào Scaffold chung.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListContent(
    modifier: Modifier = Modifier,
    onBookClick: (Book) -> Unit = {},
) {
    val allBooks = remember {
        listOf(
            Book("1", "Clean Architecture", "Robert C. Martin", "Lập trình", borrowFee = 180000, availableCopies = 2, rating = 4.8),
            Book("2", "Design Patterns", "Gang of Four", "Lập trình", borrowFee = 150000, availableCopies = 5, rating = 4.7),
            Book("3", "Kotlin in Action", "Dmitry Jemerov", "Lập trình", borrowFee = 120000, availableCopies = 1, rating = 4.9),
            Book("4", "Cấu trúc dữ liệu và giải thuật nâng cao", "Nguyễn Văn A", "Lập trình", borrowFee = 150000, availableCopies = 3, rating = 4.6),
            Book("5", "Hệ quản trị cơ sở dữ liệu quan hệ", "Trần Thị B", "Cơ sở dữ liệu", borrowFee = 120000, availableCopies = 0, rating = 4.3),
            Book("6", "Mạng máy tính căn bản", "Lê Văn C", "Mạng máy tính", borrowFee = 100000, availableCopies = 5, rating = 4.5)
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var quickGenre by remember { mutableStateOf(QUICK_GENRE_TABS.first()) }
    var quickStatus by remember { mutableStateOf<String?>(null) } 
    var filter by remember { mutableStateOf(BookFilterState()) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val filteredBooks = allBooks.filter { book ->
        val matchesQuery = searchQuery.isBlank() ||
                book.title.contains(searchQuery, ignoreCase = true) ||
                book.author.contains(searchQuery, ignoreCase = true)
        val matchesQuickGenre = quickGenre == "Tất cả" || book.category == quickGenre
        val matchesQuickStatus = when (quickStatus) {
            "available" -> book.availableCopies > 0
            "borrowed" -> book.availableCopies == 0
            else -> true
        }
        val matchesFilterGenre = filter.selectedGenres.isEmpty() || book.category in filter.selectedGenres
        val matchesRating = filter.minRating == 0 || book.rating >= filter.minRating
        matchesQuery && matchesQuickGenre && matchesQuickStatus && matchesFilterGenre && matchesRating
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = LibrarySpacing.Medium),
        ) {
            Spacer(Modifier.height(LibrarySpacing.Small))
            Text("Sách", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            
            OutlinedButton(onClick = { showFilterSheet = true }) {
                Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Thể loại")
            }

            Spacer(Modifier.height(LibrarySpacing.Small))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Tìm sách...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
            )

            Spacer(Modifier.height(LibrarySpacing.Small))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(QUICK_GENRE_TABS) { tab ->
                    FilterChip(
                        selected = quickGenre == tab, 
                        onClick = { quickGenre = tab }, 
                        label = { Text(tab) }
                    )
                }
            }

            Spacer(Modifier.height(LibrarySpacing.Small))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Trạng thái:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
                FilterChip(selected = quickStatus == "available", onClick = { quickStatus = if (quickStatus == "available") null else "available" }, label = { Text("Sẵn có") })
                Spacer(Modifier.width(8.dp))
                FilterChip(selected = quickStatus == "borrowed", onClick = { quickStatus = if (quickStatus == "borrowed") null else "borrowed" }, label = { Text("Đang mượn") })
            }

            Spacer(Modifier.height(LibrarySpacing.Medium))

            if (filteredBooks.isEmpty()) {
                EmptyBooksState(
                    onViewPopularClick = {
                        searchQuery = ""
                        quickGenre = QUICK_GENRE_TABS.first()
                        quickStatus = null
                        filter = BookFilterState()
                    },
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Small)
                ) {
                    items(filteredBooks, key = { it.id }) { book ->
                        BookListItemCard(book = book, onClick = { onBookClick(book) })
                    }
                }
            }
        }

        if (showFilterSheet) {
            BookFilterBottomSheet(
                initialFilter = filter,
                onDismiss = { showFilterSheet = false },
                onApply = { newFilter ->
                    filter = newFilter
                    showFilterSheet = false
                },
            )
        }
    }
}
