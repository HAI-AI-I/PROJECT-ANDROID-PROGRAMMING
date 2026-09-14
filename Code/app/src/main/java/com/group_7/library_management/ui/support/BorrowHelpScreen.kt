package com.group_7.library_management.ui.support

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.group_7.library_management.ui.theme.LibrarySpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowHelpScreen(
    topic: String = "Mượn sách",
    onBack: () -> Unit = {},
    onNavigateToFAQ: (String, String) -> Unit = { _, _ -> },
    onNavigateToContact: () -> Unit = {},
    onNavigateToOtherTopic: (String) -> Unit = {}
) {
    val supportViewModel: SupportViewModel = hiltViewModel()
    val uiState by supportViewModel.uiState.collectAsState()

    val faqList = uiState.categoryFaqs[topic] ?: uiState.categoryFaqs["Mượn sách"] ?: emptyList()

    val icon = when (topic) {
        "Trả sách" -> Icons.Default.AssignmentReturn
        "Gia hạn sách" -> Icons.Default.Update
        "Quá hạn / Mất / Hỏng" -> Icons.Default.Warning
        else -> Icons.AutoMirrored.Filled.MenuBook
    }

    val subtitleText = when (topic) {
        "Trả sách" -> "Các câu hỏi thường gặp về thủ tục trả sách"
        "Gia hạn sách" -> "Các câu hỏi thường gặp về gia hạn thời gian mượn"
        "Quá hạn / Mất / Hỏng" -> "Quy định xử lý quá hạn, mất sách và đền bù"
        else -> "Các câu hỏi thường gặp về mượn sách"
    }

    val otherTopics = listOf("Mượn sách", "Trả sách", "Gia hạn sách", "Quá hạn / Mất / Hỏng").filter { it != topic }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(topic, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(LibrarySpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(LibrarySpacing.Large),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Column {
                            Text(
                                text = topic,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subtitleText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Câu hỏi thường gặp về $topic",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(faqList, key = { it.id }) { faq ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToFAQ(topic, faq.id) },
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                text = faq.question,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = faq.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall))
                Text(
                    text = "Các chủ đề khác",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(otherTopics) { otherTopic ->
                val otherSubtitle = when (otherTopic) {
                    "Trả sách" -> "Thủ tục, thời gian và địa điểm trả"
                    "Gia hạn sách" -> "Điều kiện và số lần gia hạn tối đa"
                    "Quá hạn / Mất / Hỏng" -> "Quy định xử lý phạt và đền bù"
                    else -> "Quy trình, thủ tục, điều kiện mượn"
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToOtherTopic(otherTopic) },
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                text = otherTopic,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = otherSubtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(LibrarySpacing.Medium))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(LibrarySpacing.Large),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Vẫn chưa tìm được câu trả lời?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Liên hệ thủ thư để được hỗ trợ trực tiếp",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(LibrarySpacing.Medium))
                        Button(
                            onClick = onNavigateToContact,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Liên hệ ngay")
                        }
                    }
                }
            }
        }
    }
}
