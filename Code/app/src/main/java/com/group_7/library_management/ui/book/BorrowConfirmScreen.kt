package com.group_7.library_management.ui.book

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.group_7.library_management.components.BookCoverImage
import com.group_7.library_management.models.Book
import com.group_7.library_management.ui.theme.Border
import com.group_7.library_management.ui.theme.SuccessColor
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowConfirmScreen(
    onSuccess: (Long) -> Unit,
    onBookUnavailable: () -> Unit,
    onBack: () -> Unit,
    detailViewModel: BorrowConfirmViewModel = hiltViewModel()
) {
    val uiState by detailViewModel.uiState.collectAsStateWithLifecycle()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var borrowDaysText by remember { mutableStateOf(DEFAULT_BORROW_DAYS.toString()) }
    val borrowDays = borrowDaysText.toIntOrNull()?.coerceIn(1, MAX_BORROW_DAYS)
    val borrowDate = remember { LocalDate.now() }
    val expectedReturnDate = borrowDate.plusDays((borrowDays ?: DEFAULT_BORROW_DAYS).toLong())
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Xác nhận mượn sách",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            if (!uiState.isLoading && uiState.book != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Button(
                        onClick = { showConfirmDialog = true },
                        enabled = borrowDays != null &&
                                uiState.book?.availableCopies?.let { it > 0 } == true &&
                                !uiState.isSubmitting,
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(19.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("Xác nhận mượn", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when {
            uiState.isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            uiState.errorMessage != null -> Box(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = requireNotNull(uiState.errorMessage),
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = detailViewModel::loadData) { Text("Tải lại") }
                }
            }
            else -> uiState.book?.let { book ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).imePadding()
                        .verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    BorrowBookCard(book)

                    InformationField(
                        label = "NGƯỜI MƯỢN",
                        value = uiState.borrowerName,
                        icon = Icons.Default.PersonOutline
                    )
                    InformationField(
                        label = "NGÀY MƯỢN",
                        value = borrowDate.format(dateFormatter),
                        icon = Icons.Default.CalendarMonth
                    )

                    InformationField(
                        label = "ĐỊA ĐIỂM NHẬN SÁCH",
                        value = PICKUP_LOCATION,
                        icon = Icons.Default.LocationOn
                    )

                    Column {
                        FieldLabel("THỜI HẠN MƯỢN (NGÀY)")
                        OutlinedTextField(
                            value = borrowDaysText,
                            onValueChange = { value ->
                                if (value.length <= 2 && value.all(Char::isDigit)) borrowDaysText = value
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            supportingText = if (borrowDays == null) {
                                { Text("Nhập thời hạn từ 1 đến $MAX_BORROW_DAYS ngày") }
                            } else null,
                            isError = borrowDays == null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.EventAvailable,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Ngày trả dự kiến: ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = expectedReturnDate.format(dateFormatter),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    BorrowCostCard(book.borrowFee)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Xác nhận mượn sách") },
            text = {
                Text(
                    "Bạn muốn mượn “${uiState.book?.title.orEmpty()}” trong " +
                            "$borrowDays ngày và nhận tại $PICKUP_LOCATION?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        detailViewModel.confirmBorrow(
                            borrowDays = requireNotNull(borrowDays),
                            onSuccess = onSuccess,
                            onBookUnavailable = onBookUnavailable
                        )
                    }
                ) { Text("Xác nhận") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Hủy") }
            }
        )
    }
}

@Composable
private fun BorrowBookCard(book: Book) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            BookCoverImage(
                coverImageUrl = book.coverImageUrl,
                contentDescription = "Bìa sách ${book.title}",
                modifier = Modifier.size(width = 62.dp, height = 82.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = book.author,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Còn ${book.availableCopies}/${book.totalCopies} bản",
                    color = SuccessColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun InformationField(label: String, value: String, icon: ImageVector) {
    Column {
        FieldLabel(label)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, Border)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(value, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 2.dp, bottom = 6.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.6.sp
    )
}

@Composable
private fun BorrowCostCard(borrowFee: Long) {
    val currency = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CostRow(
                label = "Phí mượn",
                value = if (borrowFee == 0L) "Miễn phí" else "${currency.format(borrowFee)} đ"
            )
            CostRow(label = "Tiền cọc (Hoàn lại)", value = "${currency.format(DEPOSIT_AMOUNT)} đ")
            Text(
                text = "Tiền cọc sẽ được hoàn trả khi sách được trả đúng hạn và không hư hỏng.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun CostRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
    }
}

private const val DEFAULT_BORROW_DAYS = 14
private const val MAX_BORROW_DAYS = 30
private const val DEPOSIT_AMOUNT = 150_000L
private const val PICKUP_LOCATION = "Thư viện UTH"
