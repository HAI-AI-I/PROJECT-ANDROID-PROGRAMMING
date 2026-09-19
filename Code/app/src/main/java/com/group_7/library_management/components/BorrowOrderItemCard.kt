package com.group_7.library_management.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.group_7.library_management.models.BorrowOrder
import com.group_7.library_management.ui.theme.Success
import com.group_7.library_management.ui.theme.Warning
import java.text.NumberFormat
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun BorrowOrderItemCard(
    order: BorrowOrder,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                OrderBookCover(order)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = order.referenceCode,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        BorrowStatusBadge(order)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = order.bookTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = order.bookAuthor.ifBlank { "Chưa cập nhật tác giả" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(7.dp))
                    Text(
                        text = "Mã sách: ${order.copyBarcode}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(7.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = orderDateLabel(order),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = orderDateValue(order),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Tổng thanh toán",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatMoney(order.totalAmount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Xem chi tiết",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BorrowStatusBadge(order: BorrowOrder) {
    val (label, color) = borrowStatusStyle(order)
    Surface(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(50)) {
        Text(
            text = label,
            color = color,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun OrderBookCover(order: BorrowOrder) {
    Box(
        modifier = Modifier
            .size(width = 72.dp, height = 100.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            model = order.coverImageUrl,
            contentDescription = "Bìa sách ${order.bookTitle}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = { CoverPlaceholder() },
            error = { CoverPlaceholder() }
        )
    }
}

@Composable
private fun CoverPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.MenuBook,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun borrowStatusStyle(order: BorrowOrder): Pair<String, Color> = when {
    order.status == "REQUESTED" -> "Chờ nhận" to MaterialTheme.colorScheme.primary
    order.status == "BORROWED" && order.isDueSoon() -> "Sắp đến hạn" to Warning
    order.status == "BORROWED" -> "Đang mượn" to MaterialTheme.colorScheme.primary
    order.status == "OVERDUE" -> "Quá hạn" to MaterialTheme.colorScheme.error
    order.status == "RETURNED" -> "Đã trả" to Success
    else -> "Đã hủy" to MaterialTheme.colorScheme.onSurfaceVariant
}

private fun orderDateLabel(order: BorrowOrder): String = when (order.status) {
    "REQUESTED" -> "Ngày đặt · Hạn trả dự kiến"
    "RETURNED" -> "Ngày mượn · Ngày trả"
    else -> "Ngày mượn · Hạn trả"
}

private fun orderDateValue(order: BorrowOrder): String {
    val start = order.borrowedAt ?: order.requestedAt
    val end = if (order.status == "RETURNED") order.returnedAt ?: order.dueAt else order.dueAt
    return "${formatDate(start)} · ${formatDate(end)}"
}

private fun BorrowOrder.isDueSoon(now: Instant = Instant.now()): Boolean {
    if (status != "BORROWED") return false
    val due = runCatching { Instant.parse(dueAt) }.getOrNull() ?: return false
    val remaining = Duration.between(now, due)
    return !remaining.isNegative && remaining <= Duration.ofDays(1)
}

private fun formatDate(value: String): String = runCatching {
    DATE_FORMATTER.format(Instant.parse(value))
}.getOrDefault(value)

private fun formatMoney(value: Long): String =
    "${NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(value)} đ"

private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    .withZone(ZoneId.systemDefault())
