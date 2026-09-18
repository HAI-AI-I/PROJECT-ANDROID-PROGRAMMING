package com.group_7.library_management.ui.book

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun BorrowFailureScreen(onTryAgain: () -> Unit, onBackToHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(modifier = Modifier.size(76.dp), shape = CircleShape, color = MaterialTheme.colorScheme.errorContainer) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(40.dp))
            }
        }
        Spacer(Modifier.height(22.dp))
        Text("Sách vừa hết", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Text(
            "Bản sách cuối cùng đã được người khác đặt trước khi yêu cầu của bạn được xác nhận. Vui lòng thử lại sau hoặc bật thông báo khi có sách.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
        Button(onClick = onTryAgain, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Icon(Icons.Outlined.Refresh, null)
            Spacer(Modifier.width(8.dp))
            Text("Quay lại kiểm tra")
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onBackToHome, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Icon(Icons.Outlined.Home, null)
            Spacer(Modifier.width(8.dp))
            Text("Quay lại trang chủ")
        }
    }
}
