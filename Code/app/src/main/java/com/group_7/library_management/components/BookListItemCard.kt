package com.group_7.library_management.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.group_7.library_management.models.Book
import com.group_7.library_management.ui.theme.LibrarySpacing
import com.group_7.library_management.ui.theme.Success
import com.group_7.library_management.ui.theme.TextSecondary

/**
 * BookListItemCard
 * Card sách dùng chung — khớp thiết kế Stitch "Danh sách sách": bìa sách +
 * nhãn thể loại (UPPERCASE) + tiêu đề + giá mượn + tác giả + badge trạng
 * thái + nút hành động.
 *
 * Đây là "file chung" leader yêu cầu — dùng lại cho "Lịch sử mượn sách" và
 * "Sách của tôi" bằng cách truyền `subtitleOverride` (thay dòng tác giả,
 * vd: "Mượn: 01/08 · Hạn trả: 15/08") và `trailingContent` (thay badge +
 * nút mặc định bằng nút/badge riêng của từng trang).
 */
@Composable
fun BookListItemCard(
    book: Book,
    modifier: Modifier = Modifier,
    subtitleOverride: String? = null,
    trailingContent: (@Composable ColumnScope.() -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LibrarySpacing.Medium),
            horizontalArrangement = Arrangement.spacedBy(LibrarySpacing.Small),
        ) {
            // Placeholder bìa sách — thay bằng AsyncImage (Coil) khi có coverImageUrl thật.
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 88.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    book.title.firstOrNull()?.uppercase() ?: "?",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    book.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    book.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    formatVnd(book.borrowFee),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    subtitleOverride ?: book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(LibrarySpacing.ExtraSmall))

                if (trailingContent != null) {
                    Column { trailingContent() }
                } else {
                    DefaultTrailingRow(book)
                }
            }
        }
    }
}

@Composable
private fun DefaultTrailingRow(book: Book) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AvailabilityBadge(availableCopies = book.availableCopies)
        OutlinedButton(onClick = {}, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
            Text("Xem chi tiết", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun AvailabilityBadge(availableCopies: Int) {
    val isAvailable = availableCopies > 0
    val label = if (isAvailable) "Sẵn có ($availableCopies bản)" else "Đang mượn"
    val color = if (isAvailable) Success else TextSecondary
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), MaterialTheme.shapes.extraSmall)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Medium)
    }
}

private fun formatVnd(amount: Long): String {
    val formatted = amount.toString().reversed().chunked(3).joinToString(".").reversed()
    return "${formatted}đ"
}
