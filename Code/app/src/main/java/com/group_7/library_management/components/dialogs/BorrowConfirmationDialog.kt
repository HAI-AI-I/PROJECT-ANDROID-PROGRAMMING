package com.group_7.library_management.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.group_7.library_management.models.BorrowInfo
import com.group_7.library_management.ui.theme.LibrarySpacing

/**
 * Academic Nexus - Borrow Confirmation Dialog (Kotlin Jetpack Compose)
 * Design System: {{DATA:DESIGN_SYSTEM:DESIGN_SYSTEM_2}}
 */
@Composable
fun BorrowConfirmationDialog(
    borrowInfo: BorrowInfo= BorrowInfo(bookName = "Clean Code", borrowerName = "Lê Thanh Hải", borrowDate = "14/08/2006", returnDate = "23/08/2006", loanPeriod = "14 ngày"),
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(LibrarySpacing.Large),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = LibrarySpacing.Small)
        ) {
            Column(
                modifier = Modifier
                    .padding(LibrarySpacing.Large)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(LibrarySpacing.ExtraLarge)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Xác nhận mượn sách",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color =MaterialTheme.colorScheme.onSurface,
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DetailRow(label = "Sách", value = borrowInfo.bookName, MaterialTheme.colorScheme.onSurfaceVariant,
                        MaterialTheme.colorScheme.onSurface)
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    DetailRow(label = "Người mượn", value = borrowInfo.borrowerName, MaterialTheme.colorScheme.onSurfaceVariant,
                        MaterialTheme.colorScheme.onSurface)
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    DetailRow(label = "Ngày mượn", value = borrowInfo.borrowDate, MaterialTheme.colorScheme.onSurfaceVariant,
                        MaterialTheme.colorScheme.onSurface)
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    DetailRow(label = "Ngày trả", value = borrowInfo.returnDate, MaterialTheme.colorScheme.onSurfaceVariant,
                        MaterialTheme.colorScheme.onSurface)
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Thời hạn",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(LibrarySpacing.Small)
                        ) {
                            Text(
                                text = borrowInfo.loanPeriod,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Hủy",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(LibrarySpacing.Large)
                    ) {
                        Text(
                            text = "Xác nhận mượn",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, labelColor: Color, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = labelColor)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = valueColor,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
