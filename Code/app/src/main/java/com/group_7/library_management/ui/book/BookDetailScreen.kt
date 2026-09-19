package com.group_7.library_management.ui.book

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.group_7.library_management.R
import com.group_7.library_management.components.MemberTopBar
import com.group_7.library_management.models.Book
import com.group_7.library_management.ui.theme.LibrarySpacing
import com.group_7.library_management.ui.theme.StarColor
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BookDetailScreen(
    onBack: () -> Unit,
    viewModel: BookDetailViewModel = hiltViewModel(),
    onNavigateToReviews: () -> Unit = {},
    onNavigateToBorrow: () -> Unit = {},
    onRelatedBookClick: (Book) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MemberTopBar(
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onLeftClick = onBack,
                rightIcon = if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                rightIconTint = if (uiState.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                onRightClick = viewModel::toggleFavorite,
                showNotificationBadge = false
            )
        },
        bottomBar = {
            uiState.book?.let { book ->
                BookDetailBottomBar(
                    canBorrow = book.availableCopies > 0,
                    borrowStatus = uiState.currentBorrowOrder?.status,
                    isSubscribed = uiState.isAvailabilitySubscribed,
                    isUpdatingSubscription = uiState.isUpdatingSubscription,
                    onToggleSubscription = viewModel::toggleAvailabilitySubscription,
                    onNavigateToBorrow = { viewModel.requestBorrow(onNavigateToBorrow) }
                )
            }
        }
    ) { innerPadding ->
        when {
            uiState.book != null -> BookDetailContent(
                book = requireNotNull(uiState.book),
                relatedBooks = uiState.relatedBooks,
                isLoadingRelated = uiState.isLoadingRelated,
                onNavigateToReviews = onNavigateToReviews,
                onRelatedBookClick = onRelatedBookClick,
                modifier = Modifier.padding(innerPadding)
            )

            uiState.isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            else -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.errorMessage ?: "Không thể tải chi tiết sách.",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = viewModel::refreshBook) { Text("Tải lại") }
                }
            }
        }
    }
}

@Composable
private fun BookDetailBottomBar(
    canBorrow: Boolean,
    borrowStatus: String?,
    isSubscribed: Boolean,
    isUpdatingSubscription: Boolean,
    onToggleSubscription: () -> Unit,
    onNavigateToBorrow: () -> Unit
) {
    Surface(
        shadowElevation = 12.dp,
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedIconButton(
                onClick = onToggleSubscription,
                enabled = !isUpdatingSubscription,
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = if (isSubscribed) {
                        Icons.Default.NotificationsActive
                    }else{
                        Icons.Outlined.Notifications
                    },
                    contentDescription = if (isSubscribed) {
                        "Tắt thông báo sách"
                    } else {
                        "Bật thông báo sách"
                    },
                    tint =MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = onNavigateToBorrow,
                enabled = borrowStatus == null,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    when (borrowStatus) {
                        "REQUESTED" -> "Chờ lấy sách"
                        "BORROWED" -> "Đang mượn"
                        "OVERDUE" -> "Đã quá hạn"
                        else -> if (canBorrow) "Mượn sách" else "Sách đang hết"
                    }
                )
            }
        }
    }
}

@Composable
private fun BookDetailContent(
    book: Book,
    relatedBooks: List<Book>,
    isLoadingRelated: Boolean,
    onNavigateToReviews: () -> Unit,
    onRelatedBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedFee = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(book.borrowFee)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(310.dp).background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = book.coverImageUrl,
                contentDescription = "Bìa sách ${book.title}",
                modifier = Modifier.height(240.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit,
                fallback = androidx.compose.ui.res.painterResource(R.drawable.cleancode),
                error = androidx.compose.ui.res.painterResource(R.drawable.cleancode)
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth().offset(y = (-24).dp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(LibrarySpacing.Large)) {
                Text(book.title, style = MaterialTheme.typography.titleLarge)
                Text(
                    book.author,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(onClick = onNavigateToReviews)
                ) {
                    repeat(5) {
                        Icon(Icons.Default.Star, null, tint = StarColor, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(6.dp))
                    Text("${book.rating}/5 (${book.ratingCount} lượt đánh giá)")
                }

                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailChip("THỂ LOẠI", book.category, Modifier.weight(1f))
                    DetailChip("NHÀ XUẤT BẢN", book.publisher ?: "Chưa cập nhật", Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailChip("NĂM XUẤT BẢN", book.publishYear?.toString() ?: "Chưa cập nhật", Modifier.weight(1f))
                    DetailChip("ISBN", book.isbn ?: "Chưa cập nhật", Modifier.weight(1f))
                }

                Spacer(Modifier.height(8.dp))
                HighlightChip("GIÁ MƯỢN", "$formattedFee đ", Modifier.fillMaxWidth(0.5f))

                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE2E4FA))
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("TRẠNG THÁI", fontSize = 10.sp, color = Color(0xFF4C55B4), fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (book.availableCopies > 0) {
                                "Sẵn có (${book.availableCopies} / ${book.totalCopies} bản)"
                            } else {
                                "Đang hết sách"
                            },
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0A1268)
                        )
                    }
                }

                if (!book.description.isNullOrBlank()) {
                    Spacer(Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Info, null, tint = Color(0xFF202773), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Mô tả", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = book.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.MenuBook,
                        contentDescription = null,
                        tint = Color(0xFF202773),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Sách liên quan", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))

                when {
                    isLoadingRelated && relatedBooks.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(28.dp))
                        }
                    }

                    relatedBooks.isEmpty() -> Text(
                        "Chưa có sách liên quan cùng thể loại.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    else -> LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(relatedBooks, key = { it.id }) { relatedBook ->
                            RelatedBookCard(
                                book = relatedBook,
                                onClick = { onRelatedBookClick(relatedBook) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RelatedBookCard(book: Book, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(120.dp).clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = book.coverImageUrl,
            contentDescription = "Bìa sách ${book.title}",
            modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop,
            fallback = androidx.compose.ui.res.painterResource(R.drawable.cleancode),
            error = androidx.compose.ui.res.painterResource(R.drawable.cleancode)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = book.title,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 2
        )
        Text(
            text = book.author,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
fun DetailChip(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F6))
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(title, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HighlightChip(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE2E4FA))
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(title, fontSize = 9.sp, color = Color(0xFF4C55B4), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0A1268))
        }
    }
}
