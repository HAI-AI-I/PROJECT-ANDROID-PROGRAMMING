package com.group_7.library_management.ui.book

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowConfirmScreen(
    bookId: String,
    viewModel: BookViewModel,
    onSuccess: (String) -> Unit,
    onFailure: () -> Unit,
    onBack: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val borrowState by viewModel.borrowState.collectAsState()

    LaunchedEffect(borrowState) {
        when (val state = borrowState) {
            is BorrowUiState.Success -> {
                viewModel.resetState()
                onSuccess(state.transactionId)
            }
            is BorrowUiState.Error -> {
                viewModel.resetState()
                onFailure()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xác Nhận Mượn Sách", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = "Nguyễn Văn Nam",
                    onValueChange = {},
                    label = { Text("NGƯỜI MƯỢN") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )
                OutlinedTextField(
                    value = "24/05/2024",
                    onValueChange = {},
                    label = { Text("NGÀY MƯỢN") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )
                OutlinedTextField(
                    value = "Thư viện UTH - Cơ sở 1",
                    onValueChange = {},
                    label = { Text("ĐỊA ĐIỂM NHẬN SÁCH") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )
            }

            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D1B2A))
            ) {
                Text("Xác Nhận Mượn", fontSize = 16.sp)
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Xác nhận mượn sách") },
            text = { Text("Bạn có chắc chắn muốn xác nhận mượn cuốn sách này?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        viewModel.confirmBorrowBook(bookId)
                    }
                ) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}