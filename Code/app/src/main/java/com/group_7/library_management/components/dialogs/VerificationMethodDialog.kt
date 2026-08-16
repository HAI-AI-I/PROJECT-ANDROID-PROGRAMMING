package com.group_7.library_management.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.group_7.library_management.ui.theme.LibrarySpacing

@Composable
fun VerificationMethodDialog(
    onDismiss: () -> Unit = {},
    onConfirm: (selectedMethod:String) -> Unit = {}
) {
    var selectedMethod by remember { mutableStateOf("Email") }


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(LibrarySpacing.Large),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(LibrarySpacing.Large)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Xác nhận gửi mã",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Chọn phương thức bạn muốn nhận mã",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(Modifier.selectableGroup()) {
                    MethodItem(
                        icon = Icons.Default.Email,
                        label = "Gửi qua Email",
                        selected = selectedMethod == "Email",
                        onClick = { selectedMethod = "Email"},
                        primaryColor = MaterialTheme.colorScheme.primary,
                        onSurfaceColor = MaterialTheme.colorScheme.onSurface,
                        outlineColor = MaterialTheme.colorScheme.outlineVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    MethodItem(
                        icon = Icons.Default.Smartphone,
                        label = "Gửi qua Số điện thoại",
                        selected = selectedMethod == "Sdt",
                        onClick = { selectedMethod = "Sdt" },
                        primaryColor = MaterialTheme.colorScheme.primary,
                        onSurfaceColor = MaterialTheme.colorScheme.onSurface,
                        outlineColor = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Hủy",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.primary,
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onConfirm(selectedMethod) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(LibrarySpacing.Medium),
                        contentPadding = PaddingValues(horizontal = LibrarySpacing.Large, vertical = LibrarySpacing.Small)
                    ) {
                        Text(
                            text = "Xác nhận",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MethodItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    primaryColor: Color,
    onSurfaceColor: Color,
    outlineColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        shape = RoundedCornerShape(LibrarySpacing.Medium),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (selected) primaryColor else outlineColor.copy(alpha = 0.5f)
        ),
        color = if (selected) primaryColor.copy(alpha = 0.05f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(LibrarySpacing.Medium)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = primaryColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) primaryColor else onSurfaceColor.copy(alpha = 0.7f),
                modifier = Modifier.size(LibrarySpacing.Large)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = onSurfaceColor,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                )
            )
        }
    }
}
