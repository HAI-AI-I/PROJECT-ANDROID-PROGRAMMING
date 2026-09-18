package com.group_7.library_management.ui.book

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.group_7.library_management.models.BorrowOrder
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun BorrowSuccessScreen(
    onViewQrCode: (Long) -> Unit,
    onBackToHome: () -> Unit,
    viewModel: BorrowOrderViewModel = hiltViewModel()
) {
    BackHandler(onBack = onBackToHome)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when {
        state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        state.errorMessage != null -> BorrowOrderLoadError(
            message = requireNotNull(state.errorMessage),
            onRetry = viewModel::loadOrder,
            onBackToHome = onBackToHome
        )
        state.order != null -> BorrowSuccessContent(
            order = requireNotNull(state.order),
            onViewQrCode = onViewQrCode,
            onBackToHome = onBackToHome
        )
    }
}

@Composable
private fun BorrowSuccessContent(order: BorrowOrder, onViewQrCode: (Long) -> Unit, onBackToHome: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Surface(modifier = Modifier.size(76.dp).clip(CircleShape), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(34.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Text("Đặt mượn sách thành công!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Mang mã đơn đến thủ thư để nhận sách và thanh toán.", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Thông tin đơn mượn", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    OrderRow("Mã đơn", order.referenceCode, true)
                    OrderRow("Sách", order.bookTitle)
                    if (order.bookAuthor.isNotBlank()) OrderRow("Tác giả", order.bookAuthor)
                    OrderRow("Mã bản sách", order.copyBarcode)
                    OrderRow("Người mượn", order.borrowerName)
                    OrderRow("Nơi nhận", order.pickupLocation)
                    OrderRow("Ngày tạo", formatInstant(order.requestedAt))
                    OrderRow("Hạn trả dự kiến", formatInstant(order.dueAt))
                    OrderRow("Thời hạn", "${order.borrowDays} ngày")
                    OrderRow("Trạng thái", "Chờ nhận sách", true)
                }
            }

            Spacer(Modifier.height(14.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Thanh toán tại thủ thư", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    OrderRow("Phí mượn", money(order.borrowFee))
                    OrderRow("Tiền cọc (được hoàn lại)", money(order.depositAmount))
                    HorizontalDivider()
                    OrderRow("Tổng cần trả", money(order.totalAmount), true)
                }
            }

            Spacer(Modifier.height(22.dp))
            Button(onClick = { onViewQrCode(order.id) }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Icon(Icons.Outlined.QrCode2, null)
                Spacer(Modifier.width(8.dp))
                Text("Xem mã QR đơn hàng", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(onClick = onBackToHome, modifier = Modifier.fillMaxWidth().height(52.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                Icon(Icons.Outlined.Home, null)
                Spacer(Modifier.width(8.dp))
                Text("Quay lại trang chủ")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun OrderRow(label: String, value: String, emphasized: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, modifier = Modifier.weight(0.43f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            modifier = Modifier.weight(0.57f),
            textAlign = TextAlign.End,
            color = if (emphasized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
internal fun BorrowOrderLoadError(message: String, onRetry: () -> Unit, onBackToHome: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Thử lại") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onBackToHome) { Text("Về trang chủ") }
    }
}

internal fun money(amount: Long): String = "${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(amount)} đ"

private fun formatInstant(value: String): String = runCatching {
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault()).format(Instant.parse(value))
}.getOrDefault(value)
