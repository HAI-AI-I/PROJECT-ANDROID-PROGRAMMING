package com.group_7.library_management.ui.book

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.group_7.library_management.ui.theme.Border
import com.group_7.library_management.ui.theme.LibrarySpacing
import java.util.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionQrScreen(
    transactionId: String = "#LIB-2024-0524-001",
    bookTitle: String = "Clean Code",
    borrowerName: String = "Nguyễn Văn Nam",
    onBackClick: () -> Unit = {},
    onSaveQrClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    // Trích xuất Tokens trực tiếp từ MaterialTheme
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mã QR Giao dịch",
                        style = typography.titleMedium,
                        color = colors.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = colors.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colors.background
                )
            )
        },
        containerColor = colors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = LibrarySpacing.Medium, vertical = LibrarySpacing.Small)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = LibrarySpacing.Large),
                shape = shapes.large,
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(LibrarySpacing.Medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MƯỢN SÁCH THÀNH CÔNG",
                        style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall))

                    Text(
                        text = bookTitle,
                        style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall))

                    Text(
                        text = "Người mượn: $borrowerName",
                        style = typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

                    // Khung chứa Mã QR
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        shape = shapes.large,
                        color = colors.surface,
                        border = BorderStroke(1.dp, Border)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(LibrarySpacing.Medium)
                                .background(colors.surfaceVariant, shapes.medium),
                            contentAlignment = Alignment.Center
                        ) {
                            QrCodeCanvas(
                                content = transactionId,
                                modifier = Modifier
                                    .fillMaxSize(0.85f)
                                    .aspectRatio(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

                    Text(
                        text = "Mã giao dịch",
                        style = typography.bodyMedium,
                        color = colors.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall))

                    Surface(
                        shape = shapes.small,
                        color = colors.surfaceVariant
                    ) {
                        Text(
                            text = transactionId,
                            modifier = Modifier.padding(
                                horizontal = LibrarySpacing.Medium,
                                vertical = LibrarySpacing.Small
                            ),
                            style = typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = shapes.medium,
                        color = colors.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(LibrarySpacing.Medium),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(LibrarySpacing.Small))
                            Text(
                                text = "Vui lòng đưa mã này cho thủ thư để quét khi nhận hoặc trả sách.",
                                style = typography.bodyMedium,
                                color = colors.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Nút Lưu mã QR (Primary Button)
            Button(
                onClick = onSaveQrClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LibrarySpacing.Huge),
                shape = shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(LibrarySpacing.Small))
                Text(
                    text = "Lưu mã QR",
                    style = typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

            // Nút Chia sẻ (Secondary Outlined Button)
            OutlinedButton(
                onClick = onShareClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LibrarySpacing.Huge),
                shape = shapes.extraLarge,
                border = BorderStroke(1.dp, Border),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colors.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(LibrarySpacing.Small))
                Text(
                    text = "Chia sẻ",
                    style = typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(LibrarySpacing.Small))

            TextButton(
                onClick = onHomeClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Quay về Trang chủ",
                    style = typography.bodyLarge.copy(
                        color = colors.primary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
fun QrCodeCanvas(
    content: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val sizePx = size.minDimension
        val matrixSize = 23
        val cellSize = sizePx / matrixSize

        drawRect(color = Color.White)

        val random = Random(content.hashCode().toLong())

        for (row in 0 until matrixSize) {
            for (col in 0 until matrixSize) {
                val isCorner1 = row < 7 && col < 7
                val isCorner2 = row < 7 && col >= matrixSize - 7
                val isCorner3 = row >= matrixSize - 7 && col < 7

                if (isCorner1 || isCorner2 || isCorner3) {
                    val r = if (row >= matrixSize - 7) row - (matrixSize - 7) else row
                    val c = if (col >= matrixSize - 7) col - (matrixSize - 7) else col
                    if (r == 0 || r == 6 || c == 0 || c == 6 || (r in 2..4 && c in 2..4)) {
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(col * cellSize, row * cellSize),
                            size = Size(cellSize, cellSize)
                        )
                    }
                } else if (random.nextBoolean()) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(col * cellSize, row * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }
            }
        }
    }
}