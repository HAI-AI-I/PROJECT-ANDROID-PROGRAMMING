package com.group_7.library_management.ui.book

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ConfirmBorrowDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Xác nhận mượn sách", fontWeight = FontWeight.Bold) },
        text = { Text("Bạn có chắc chắn muốn xác nhận mượn cuốn sách này không?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Xác nhận mượn")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}