package com.group_7.library_management.ui.book

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.group_7.library_management.components.QrDisplayCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowQrScreen(
    onBack: () -> Unit,
    onBackToHome: () -> Unit,
    viewModel: BorrowOrderViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                            .wrapContentHeight(Alignment.CenterVertically),
                        onSave = { bitmap ->
                            scope.launch {
                                val result = saveQrBitmap(context, bitmap, order.referenceCode)
                                snackbarHostState.showSnackbar(
                                    result.fold(
                                        onSuccess = { "Đã lưu mã QR vào thư viện ảnh." },
                                        onFailure = { "Không thể lưu mã QR." }
                                    )
                                )
                            }
                        },
                        onShare = { bitmap ->
                            scope.launch {
                                shareQrBitmap(context, bitmap, order.referenceCode)
                                    .onFailure { snackbarHostState.showSnackbar("Không thể chia sẻ mã QR.") }
                            }
                        },
                        onBackToHome = onBackToHome
                    ) {
                        Text("Số tiền cần trả: ${money(order.totalAmount)}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private suspend fun saveQrBitmap(context: Context, bitmap: Bitmap, code: String): Result<Unit> = runCatching {
    withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "qr-$code.png")
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

private suspend fun shareQrBitmap(context: Context, bitmap: Bitmap, code: String): Result<Unit> = runCatching {
    val file = withContext(Dispatchers.IO) {
        val directory = File(context.cacheDir, "shared_qr").apply { mkdirs() }
        File(directory, "qr-$code.png").also { target ->
            FileOutputStream(target).use { output -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, output) }
        }
    }
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, "Mã QR đơn mượn: $code")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Chia sẻ mã QR"))
}
