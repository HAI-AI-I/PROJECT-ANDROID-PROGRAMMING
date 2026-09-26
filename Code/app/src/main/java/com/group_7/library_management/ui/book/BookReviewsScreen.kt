package com.group_7.library_management.ui.book

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.group_7.library_management.components.BookCoverImage
import com.group_7.library_management.models.Book
import com.group_7.library_management.models.BookReview
import com.group_7.library_management.ui.theme.Border
import com.group_7.library_management.ui.theme.StarColor
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun BookReviewsScreen(
    onBack: () -> Unit = {},
    viewModel: BookReviewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var reviewToDelete by remember { mutableStateOf<BookReview?>(null) }
    val reachedEnd by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            totalItems > 0 && lastVisible >= totalItems - 2
        }
    }

    LaunchedEffect(reachedEnd) {
        if (reachedEnd) viewModel.loadNextPage()
    }

    Scaffold(
        topBar = { ReviewTopBar(uiState.book, onBack) },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = viewModel::openReviewEditor,
                    enabled = !uiState.isSubmitting && !uiState.isCheckingMyReview,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    if (uiState.isCheckingMyReview) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (uiState.isCheckingMyReview) "Đang kiểm tra..." else "Viết đánh giá",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingContent(Modifier.padding(innerPadding))
            uiState.errorMessage != null -> ErrorContent(
                message = requireNotNull(uiState.errorMessage),
                onRetry = viewModel::refresh,
                modifier = Modifier.padding(innerPadding)
            )
            else -> LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 8.dp,
                    bottom = 20.dp
                )
            ) {
                if (uiState.reviews.isEmpty()) {
                    item {
                        Text(
                            text = "Chưa có đánh giá nào. Hãy là người đầu tiên đánh giá cuốn sách này.",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(uiState.reviews, key = BookReview::id) { review ->
                        ReviewItem(
                            review = review,
                            isOwner = review.userId == viewModel.currentUserId,
                            onEdit = { viewModel.editReview(review) },
                            onDelete = { reviewToDelete = review }
                        )
                    }
                }

                if (uiState.isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        }
    }

    if (uiState.isEditorVisible) {
        WriteReviewDialog(
            existingReview = uiState.editingReview,
            isSubmitting = uiState.isSubmitting,
            onDismiss = viewModel::dismissEditor,
            onSubmit = viewModel::submitReview
        )
    }

    reviewToDelete?.let { review ->
        AlertDialog(
            onDismissRequest = { if (!uiState.isDeleting) reviewToDelete = null },
            title = { Text("Xóa đánh giá?") },
            text = { Text("Đánh giá của bạn sẽ bị xóa và điểm trung bình của sách được tính lại.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteReview(review.id)
                        reviewToDelete = null
                    },
                    enabled = !uiState.isDeleting
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { reviewToDelete = null },
                    enabled = !uiState.isDeleting
                ) { Text("Hủy") }
            }
        )
    }
}

@Composable
private fun ReviewTopBar(book: Book?, onBack: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
            BookCoverImage(
                coverImageUrl = book?.coverImageUrl,
                contentDescription = book?.title,
                modifier = Modifier.size(width = 48.dp, height = 58.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = book?.title ?: "Đánh giá sách",
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (book != null) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = String.format(Locale.US, "%.1f", book.rating),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.width(3.dp))
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = StarColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Xếp hạng và đánh giá${book?.let { " · ${it.ratingCount} lượt" } ?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ReviewItem(
    review: BookReview,
    isOwner: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var helpfulChoice by rememberSaveable(review.id) { mutableStateOf<Boolean?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = review.userName.firstOrNull()?.uppercase() ?: "?",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = review.userName,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (isOwner) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Tùy chọn đánh giá")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Thay đổi") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Xóa") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(5) { index ->
                Icon(
                    imageVector = if (index < review.rating) Icons.Default.Star else Icons.Outlined.Star,
                    contentDescription = null,
                    tint = if (index < review.rating) StarColor else Border,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = formatReviewDate(review.createdAt),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }

        if (!review.comment.isNullOrBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )
        }

        Spacer(Modifier.height(18.dp))
        HorizontalDivider(modifier = Modifier.width(48.dp), thickness = 1.dp, color = Border)
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Bài đánh giá này có hữu ích không?",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HelpfulButton("Có", selected = helpfulChoice == true) { helpfulChoice = true }
            Spacer(Modifier.width(8.dp))
            HelpfulButton("Không", selected = helpfulChoice == false) { helpfulChoice = false }
        }
        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = Border)
    }
}

@Composable
private fun HelpfulButton(text: String, selected: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
            contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
    ) {
        Text(text, fontSize = 14.sp)
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onRetry) { Text("Tải lại") }
        }
    }
}

@Composable
private fun WriteReviewDialog(
    existingReview: BookReview?,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var rating by remember(existingReview?.id) {
        mutableIntStateOf(existingReview?.rating ?: 5)
    }
    var comment by remember(existingReview?.id) {
        mutableStateOf(existingReview?.comment.orEmpty())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingReview == null) "Viết đánh giá" else "Thay đổi đánh giá") },
        text = {
            Column {
                Text("Bạn đánh giá cuốn sách này bao nhiêu sao?")
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(5) { index ->
                        val star = index + 1
                        Icon(
                            imageVector = if (star <= rating) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = "$star sao",
                            tint = if (star <= rating) StarColor else Color.Gray,
                            modifier = Modifier.size(36.dp).clickable { rating = star }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { if (it.length <= 2000) comment = it },
                    label = { Text("Bình luận (không bắt buộc)") },
                    supportingText = { Text("${comment.length}/2000") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSubmitting
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(rating, comment) }, enabled = !isSubmitting) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (existingReview == null) "Gửi" else "Lưu")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSubmitting) { Text("Hủy") }
        }
    )
}

private fun formatReviewDate(value: String): String = runCatching {
    Instant.parse(value)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}.getOrDefault(value)
