package com.group_7.library_management.ui.support

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.group_7.library_management.ui.theme.LibrarySpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSupportRequestScreen(
    viewModel: SupportViewModel,
    onBack: () -> Unit = {},
    onSubmitSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val problems = uiState.supportProblems

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gửi yêu cầu hỗ trợ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(LibrarySpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium)
        ) {
            item {
                Text(
                    text = "Chọn sách (tùy chọn)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(LibrarySpacing.Small))
            }

            if (uiState.isLoading && uiState.borrowedBooks.isEmpty()) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            } else if (uiState.borrowedBooks.isEmpty()) {
                item {
                    Text(
                        text = "Bạn không có sách đang mượn. Bạn vẫn có thể gửi yêu cầu chung.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(uiState.borrowedBooks) { book ->
                val isSelected = uiState.selectedBook?.id == book.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isSelected) viewModel.selectBook(null)
                            else viewModel.selectBook(book)
                        },
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(LibrarySpacing.Medium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = book.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${book.author} · ${book.dueDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                if (checked) viewModel.selectBook(book)
                                else viewModel.selectBook(null)
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(LibrarySpacing.Small))
                Text(
                    text = "Vấn đề gặp phải *",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(LibrarySpacing.Small))
            }

            items(problems) { problem ->
                val isSelected = uiState.selectedProblem == problem
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isSelected) viewModel.selectProblem("")
                            else viewModel.selectProblem(problem)
                        },
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(LibrarySpacing.Medium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = problem,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                if (checked) viewModel.selectProblem(problem)
                                else viewModel.selectProblem("")
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(LibrarySpacing.Small))
                Text(
                    text = "Mô tả chi tiết",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(LibrarySpacing.Small))
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateDescription(it.take(2000)) },
                    placeholder = { Text("Vui lòng mô tả vấn đề của bạn...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = MaterialTheme.shapes.medium,
                    maxLines = 5
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${uiState.description.length}/2000 ký tự",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (uiState.error != null) {
                item {
                    Text(
                        text = uiState.error!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(LibrarySpacing.Medium))
                Button(
                    onClick = {
                        viewModel.createSupportRequest {
                            onSubmitSuccess()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !uiState.isSubmitting,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Gửi yêu cầu", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(LibrarySpacing.Large))
            }
        }
    }
}
