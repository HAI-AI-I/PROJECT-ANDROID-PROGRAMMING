package com.group_7.library_management.ui.favorite

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.group_7.library_management.ui.theme.LibrarySpacing

data class FavoriteBook(
    val id: Int,
    val title: String,
    val author: String,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen() {

    val favoriteBooks = remember {
        mutableStateListOf(
            FavoriteBook(
                id = 1,
                title = "Đắc Nhân Tâm",
                author = "Dale Carnegie",
                category = "Kỹ năng sống"
            ),
            FavoriteBook(
                id = 2,
                title = "Nhà Giả Kim",
                author = "Paulo Coelho",
                category = "Tiểu thuyết"
            ),
            FavoriteBook(
                id = 3,
                title = "Clean Code",
                author = "Robert C. Martin",
                category = "Lập trình"
            )
        )
    }

    Scaffold(
        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = "Sách yêu thích",
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            )
        }
    ) { paddingValues ->

        if (favoriteBooks.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(LibrarySpacing.Large),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Không có sách yêu thích",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(LibrarySpacing.Medium)
                )

                Text(
                    text = "Chưa có sách yêu thích",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(LibrarySpacing.Small)
                )

                Text(
                    text = "Các sách bạn yêu thích sẽ xuất hiện ở đây.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = LibrarySpacing.Medium),
                verticalArrangement = Arrangement.spacedBy(
                    LibrarySpacing.Medium
                )
            ) {

                item {
                    Spacer(
                        modifier = Modifier.height(LibrarySpacing.Small)
                    )

                    Text(
                        text = "${favoriteBooks.size} sách đã yêu thích",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(
                    items = favoriteBooks,
                    key = { it.id }
                ) { book ->

                    FavoriteBookItem(
                        book = book,
                        onRemoveFavorite = {
                            favoriteBooks.remove(book)
                        }
                    )
                }

                item {
                    Spacer(
                        modifier = Modifier.height(LibrarySpacing.Medium)
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteBookItem(
    book: FavoriteBook,
    onRemoveFavorite: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LibrarySpacing.Medium),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = "Sách",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.size(LibrarySpacing.Medium)
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
                    modifier = Modifier.height(LibrarySpacing.ExtraSmall)
                )

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(LibrarySpacing.ExtraSmall)
                )

                Text(
                    text = book.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = onRemoveFavorite
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Bỏ yêu thích",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}