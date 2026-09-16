package com.group_7.library_management.ui.book

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowConfirmScreen(
    bookId: String,
    viewModel: BookViewModel,
    onSuccess: (String) -> Unit,
    onFailure: (String) -> Unit = {},
    onBack: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val borrowState by viewModel.borrowState.collectAsState()
    val formState by viewModel.borrowFormState.collectAsState()

    LaunchedEffect(borrowState) {
        when (val state = borrowState) {
            is BorrowUiState.Success -> {
                viewModel.resetBorrowState()
                onSuccess(state.transactionId)
            }
            is BorrowUiState.Error -> {
                errorMessage = state.message
                viewModel.resetBorrowState()
                onFailure(state.message)
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Xác nhận mượn sách",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 10.dp) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                        .height(54.dp),
                    enabled = borrowState !is BorrowUiState.Loading,
                    shape = MaterialTheme.shapes.large
                ) {
                    if (borrowState is BorrowUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    Text(
                        if (borrowState is BorrowUiState.Loading) "Đang xử lý" else "Xác nhận mượn",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Kiểm tra thông tin",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Vui lòng kiểm tra thông tin trước khi xác nhận. Hạn trả được hệ thống tự động tính.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            errorMessage?.let { message ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Info, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Text(message, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)) {
                    BorrowInfoRow(Icons.Default.Person, "Người mượn", formState.borrowerName)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    BorrowInfoRow(Icons.Default.CalendarMonth, "Ngày mượn", formState.borrowDate)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    BorrowInfoRow(
                        Icons.Default.EventAvailable,
                        "Hạn trả (${formState.loanDays} ngày)",
                        formState.dueDate,
                        emphasize = true
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    BorrowInfoRow(Icons.Default.LocationOn, "Nơi nhận sách", "Thư viện UTH - Cơ sở 1")
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Bạn có thể trả sách trước hạn tại quầy thư viện.",
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodySmall
                )
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

@Composable
private fun BorrowInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    emphasize: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Medium,
                color = if (emphasize) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
