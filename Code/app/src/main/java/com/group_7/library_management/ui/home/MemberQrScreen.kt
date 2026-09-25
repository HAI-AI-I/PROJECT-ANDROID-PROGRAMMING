package com.group_7.library_management.ui.home

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
fun MemberQrScreen(
    onBack: () -> Unit,
    viewModel: MemberQrViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mã QR thành viên", fontWeight = FontWeight.Bold) },
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
                state.errorMessage != null -> Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(requireNotNull(state.errorMessage), color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = viewModel::loadMember) { Text("Thử lại") }
                }
                state.user != null -> {
                    val user = requireNotNull(state.user)
                    val memberCode = "UTH-MEMBER-${user.id.toString().padStart(8, '0')}"
                    QrDisplayCard(
                        qrContent = memberCode,
                        code = memberCode,
                        title = user.name,
                        description = "Xuất trình mã này cho thủ thư để nhận diện tài khoản thành viên.",
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                            .wrapContentHeight(Alignment.CenterVertically),
                        onSave = { bitmap ->
                            scope.launch {
                                val result = saveMemberQrBitmap(context, bitmap, memberCode)
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
                                shareMemberQrBitmap(context, bitmap, memberCode)
                                    .onFailure { snackbarHostState.showSnackbar("Không thể chia sẻ mã QR.") }
                            }
                        }
                    ) {
                        Text(user.email, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (user.phone.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(user.phone, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

private suspend fun saveMemberQrBitmap(context: Context, bitmap: Bitmap, code: String): Result<Unit> = runCatching {
    withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "member-$code.png")
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

private suspend fun shareMemberQrBitmap(context: Context, bitmap: Bitmap, code: String): Result<Unit> = runCatching {
    val file = withContext(Dispatchers.IO) {
        val directory = File(context.cacheDir, "shared_qr").apply { mkdirs() }
        File(directory, "member-$code.png").also { target ->
            FileOutputStream(target).use { output -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, output) }
        }
    }
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, "Mã QR thành viên thư viện UTH: $code")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Chia sẻ mã QR thành viên"))
}
