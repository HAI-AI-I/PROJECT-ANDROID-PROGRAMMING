package com.group_7.library_management.ui.book

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.group_7.library_management.models.BorrowPayment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowPaymentScreen(
    onBack: () -> Unit,
    onBackToHome: () -> Unit,
    onPaymentSuccess: (Long) -> Unit,
    viewModel: BorrowPaymentViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isPaid, state.payment?.orderId) {
        if (state.isPaid) state.payment?.orderId?.let(onPaymentSuccess)
    }
    BackHandler(onBack = onBack)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán đơn mượn", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.errorMessage != null && state.payment == null -> BorrowOrderLoadError(
                    message = requireNotNull(state.errorMessage),
                    onRetry = viewModel::retry,
                    onBackToHome = onBackToHome
                )
                state.payment != null -> PaymentContent(
                    payment = requireNotNull(state.payment),
                    isRefreshing = state.isRefreshing,
                    onSave = {
                        scope.launch {
                            val result = saveQrImage(context, requireNotNull(state.payment))
                            snackbarHostState.showSnackbar(
                                result.fold(
                                    onSuccess = { "Đã lưu mã QR vào thư viện ảnh." },
                                    onFailure = { "Không thể lưu mã QR." }
                                )
                            )
                        }
                    },
                    onShare = {
                        scope.launch {
                            shareQrImage(context, requireNotNull(state.payment))
                                .onFailure { snackbarHostState.showSnackbar("Không thể chia sẻ mã QR.") }
                        }
                    },
                    onBackToHome = onBackToHome
                )
            }
        }
    }
}

@Composable
private fun PaymentContent(
    payment: BorrowPayment,
    isRefreshing: Boolean,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onBackToHome: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Quét mã để thanh toán", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(
            "Đơn đang chờ thanh toán. Màn hình sẽ tự cập nhật sau khi ngân hàng xác nhận.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(18.dp))
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            SubcomposeAsyncImage(
                model = payment.qrUrl,
                contentDescription = "Mã QR thanh toán ${payment.referenceCode}",
                modifier = Modifier.size(500.dp).padding(12.dp),
                contentScale = ContentScale.Fit,
                loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } },
                error = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Không tải được mã QR", color = MaterialTheme.colorScheme.error) } }
            )
        }
        Spacer(Modifier.height(18.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onSave, modifier = Modifier.weight(1f).height(50.dp)) {
                Icon(Icons.Outlined.Download, contentDescription = null)
                Spacer(Modifier.size(6.dp))
                Text("Lưu ảnh")
            }
            OutlinedButton(onClick = onShare, modifier = Modifier.weight(1f).height(50.dp)) {
                Icon(Icons.Outlined.Share, contentDescription = null)
                Spacer(Modifier.size(6.dp))
                Text("Chia sẻ")
            }
        }
        Spacer(Modifier.height(10.dp))
        Button(onClick = onBackToHome, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Icon(Icons.Outlined.Home, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Về trang chủ", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PaymentRow(label: String, value: String, emphasized: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            modifier = Modifier.padding(start = 16.dp),
            textAlign = TextAlign.End,
            color = if (emphasized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Medium
        )
    }
}

private suspend fun loadQrBitmap(context: Context, payment: BorrowPayment): Bitmap = withContext(Dispatchers.IO) {
    val request = ImageRequest.Builder(context).data(payment.qrUrl).allowHardware(false).build()
    val result = context.imageLoader.execute(request)
    require(result is SuccessResult) { "Không tải được ảnh QR" }
    result.drawable.toBitmap()
}

private suspend fun saveQrImage(context: Context, payment: BorrowPayment): Result<Unit> = runCatching {
    val bitmap = loadQrBitmap(context, payment)
    withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "sepay-${payment.referenceCode}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/UTH Library")
        }
        val uri = requireNotNull(context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
        context.contentResolver.openOutputStream(uri).use { output ->
            requireNotNull(output)
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output))
        }
    }
}

private suspend fun shareQrImage(context: Context, payment: BorrowPayment): Result<Unit> = runCatching {
    val bitmap = loadQrBitmap(context, payment)
    val file = withContext(Dispatchers.IO) {
        val directory = File(context.cacheDir, "shared_qr").apply { mkdirs() }
        File(directory, "sepay-${payment.referenceCode}.png").also { target ->
            FileOutputStream(target).use { output -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, output) }
        }
    }
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, "Thanh toán đơn ${payment.referenceCode} - ${money(payment.amount)} - ${payment.paymentCode}")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Chia sẻ mã QR thanh toán"))
}
