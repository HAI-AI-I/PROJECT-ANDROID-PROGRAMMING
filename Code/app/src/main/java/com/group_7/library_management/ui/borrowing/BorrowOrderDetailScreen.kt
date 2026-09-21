package com.group_7.library_management.ui.borrowing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.group_7.library_management.components.BorrowStatusBadge
import com.group_7.library_management.models.BorrowOrder
import com.group_7.library_management.ui.book.BorrowOrderViewModel
import java.text.NumberFormat
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowOrderDetailScreen(
    onBack: () -> Unit,
    onViewQrCode: (Long) -> Unit,
    onBookClick: (Long) -> Unit,
    viewModel: BorrowOrderViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showCancelConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết đơn mượn", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.errorMessage != null -> OrderDetailError(
                message = requireNotNull(state.errorMessage),
                onRetry = viewModel::loadOrder,
                onBack = onBack,
                modifier = Modifier.padding(innerPadding)
            )

            state.order != null -> OrderDetailContent(
                order = requireNotNull(state.order),
                onViewQrCode = onViewQrCode,
                onBookClick = onBookClick,
                onCancelClick = { showCancelConfirmation = true },
                isCancelling = state.isCancelling,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    if (showCancelConfirmation) {
        AlertDialog(
            onDismissRequest = { showCancelConfirmation = false },
            title = { Text("Xác nhận hủy đơn") },
            text = {
                Text("Bạn có chắc muốn hủy đơn này? Lần hủy này sẽ được tính vào giới hạn 5 lần trong tháng.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelConfirmation = false
                        viewModel.cancelOrder()
                    }
                ) {
                    Text("Hủy đơn", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirmation = false }) {
                    Text("Không")
                }
            }
        )
    }
}

@Composable
private fun OrderDetailContent(
    order: BorrowOrder,
    onViewQrCode: (Long) -> Unit,
    onBookClick: (Long) -> Unit,
    onCancelClick: () -> Unit,
    isCancelling: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mã đơn", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(order.referenceCode, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    BorrowStatusBadge(order)
                }
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onBookClick(order.bookId) }
                        .padding(4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    DetailBookCover(order = order)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = order.bookTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(order.bookAuthor, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Mã bản sách: ${order.copyBarcode}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        DetailCard(title = "Thông tin mượn") {
            DetailRow("Người mượn", order.borrowerName)
            DetailRow("Địa điểm nhận", order.pickupLocation)
            DetailRow("Thời hạn mượn", "${order.borrowDays} ngày")
            DetailRow("Ngày đặt", formatDateTime(order.requestedAt))
            order.borrowedAt?.let { DetailRow("Thời gian nhận", formatDateTime(it)) }
            DetailRow("Hạn trả", formatDateTime(order.dueAt), emphasized = order.status == "OVERDUE")
            order.returnedAt?.let { DetailRow("Thời gian trả", formatDateTime(it)) }
        }

        DetailCard(title = "Thanh toán tại thư viện") {
            DetailRow("Phí mượn", formatMoney(order.borrowFee))
            DetailRow("Tiền cọc hoàn lại", formatMoney(order.depositAmount))
            HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
            DetailRow("Tổng cần trả", formatMoney(order.totalAmount), emphasized = true)
            DetailRow("Tổng đã trả", formatMoney(order.paidAmount), emphasized = true)
            if (order.remainingRefundAmount > 0) {
                DetailRow(
                    "Tổng dư cần hoàn",
                    formatMoney(order.remainingRefundAmount),
                    emphasized = true
                )
            }
        }

        if (order.status != "RETURNED" && order.status != "CANCELLED") {
            Button(
                onClick = { onViewQrCode(order.id) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.QrCode2, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Xem mã QR đơn mượn", fontWeight = FontWeight.Bold)
            }
        }
        if (order.canCancel()) {
            OutlinedButton(
                onClick = onCancelClick,
                enabled = !isCancelling,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isCancelling) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = if (isCancelling) "Đang hủy..." else "Hủy đơn",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun DetailCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, emphasized: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = label,
            modifier = Modifier.weight(0.43f),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            modifier = Modifier.weight(0.57f),
            textAlign = TextAlign.End,
            color = if (emphasized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun DetailBookCover(order: BorrowOrder) {
    Box(
        modifier = Modifier
            .size(width = 82.dp, height = 112.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            model = order.coverImageUrl,
            contentDescription = "Bìa sách ${order.bookTitle}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = { DetailCoverPlaceholder() },
            error = { DetailCoverPlaceholder() }
        )
    }
}

@Composable
private fun DetailCoverPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Icon(
            Icons.AutoMirrored.Filled.MenuBook,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
            modifier = Modifier.size(34.dp)
        )
    }
}

@Composable
private fun OrderDetailError(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        Spacer(Modifier.height(14.dp))
        Button(onClick = onRetry) { Text("Thử lại") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onBack) { Text("Quay lại") }
    }
}

private fun formatDateTime(value: String): String = runCatching {
    DATE_TIME_FORMATTER.format(Instant.parse(value))
}.getOrDefault(value)

private fun BorrowOrder.canCancel(now: Instant = Instant.now()): Boolean {
    if (status != "REQUESTED") return false
    val requested = runCatching { Instant.parse(requestedAt) }.getOrNull() ?: return false
    val elapsed = Duration.between(requested, now)
    return !elapsed.isNegative && elapsed <= Duration.ofHours(24)
}

private fun formatMoney(value: Long): String =
    "${NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(value)} đ"

private val DATE_TIME_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault())
