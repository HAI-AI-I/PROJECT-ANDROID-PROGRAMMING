package com.group_7.library_management.ui.book

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.group_7.library_management.components.QrDisplayCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowQrScreen(
    onBack: () -> Unit,
    onBackToHome: () -> Unit,
    viewModel: BorrowOrderViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mã QR đơn mượn", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại") } }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.errorMessage != null -> BorrowOrderLoadError(
                    message = requireNotNull(state.errorMessage),
                    onRetry = viewModel::loadOrder,
                    onBackToHome = onBackToHome
                )
                state.order != null -> {
                    val order = requireNotNull(state.order)
                    QrDisplayCard(
                        qrContent = order.referenceCode,
                        code = order.referenceCode,
                        title = order.bookTitle,
                        description = "Đưa mã này cho thủ thư khi nhận hoặc trả sách.",
                        modifier = Modifier.fillMaxSize().padding(24.dp).wrapContentHeight(Alignment.CenterVertically)
                    ) {
                        Text("Số tiền cần trả: ${money(order.totalAmount)}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
