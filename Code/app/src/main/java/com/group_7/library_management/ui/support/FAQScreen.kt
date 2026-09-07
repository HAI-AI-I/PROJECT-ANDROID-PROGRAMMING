package com.group_7.library_management.ui.support

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import com.group_7.library_management.components.SearchBar
import com.group_7.library_management.ui.theme.LibrarySpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(
    onBack: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val faqs = remember {
        listOf(
            FaqItem("1", "Làm thế nào để mượn sách?", "Bạn có thể mượn sách bằng cách tìm kiếm sách trên ứng dụng, quét mã QR tại quầy thư viện hoặc liên hệ thủ thư để được hướng dẫn chi tiết.", "Mượn sách", ""),
            FaqItem("2", "Điều kiện mượn sách là gì?", "Thẻ thành viên thư viện còn hiệu lực, tài khoản không có sách quá hạn chưa thanh toán tiền phạt.", "Mượn sách", ""),
            FaqItem("3", "Số lượt mượn sách?", "Mỗi thành viên được mượn tối đa 5 cuốn sách trong thời gian 14 ngày cho mỗi đợt mượn.", "Mượn sách", ""),
            FaqItem("4", "Làm sao biết sách còn hay không?", "Trang chi tiết sách trên ứng dụng hiển thị trực quan số lượng bản sao sẵn có tại thư viện theo thời gian thực.", "Mượn sách", ""),
            FaqItem("5", "Không mượn được sách", "Kiểm tra xem bạn đã vượt quá giới hạn số sách mượn hoặc tài khoản đang bị khóa tạm thời do quá hạn.", "Mượn sách", ""),
            FaqItem("6", "Sách đang được người khác mượn", "Nếu sách đã hết bản sao, hệ thống cho phép bạn thực hiện đặt chỗ (reservation) để được ưu tiên mượn khi sách được trả.", "Mượn sách", ""),
            FaqItem("7", "Đặt chỗ sách như thế nào?", "Truy cập trang chi tiết cuốn sách bạn muốn và nhấn nút 'Đặt chỗ'. Bạn sẽ nhận thông báo ngay khi sách có sẵn.", "Mượn sách", "")
        )
    }

    var expandedIds by remember { mutableStateOf(setOf<String>()) }

    val filteredFaqs = if (searchQuery.isBlank()) {
        faqs
    } else {
        faqs.filter { it.question.contains(searchQuery, ignoreCase = true) || it.answer.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mượn sách", fontWeight = FontWeight.Bold) },
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
                .padding(innerPadding)
                .padding(LibrarySpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium)
        ) {
            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Tìm kiếm câu hỏi...",
                    onFilterClick = null,
                    showMic = false
                )
            }

            items(filteredFaqs, key = { it.id }) { faq ->
                val isExpanded = expandedIds.contains(faq.id)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedIds = if (isExpanded) {
                                expandedIds - faq.id
                            } else {
                                expandedIds + faq.id
                            }
                        },
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(LibrarySpacing.Medium)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = faq.question,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(LibrarySpacing.Small))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(LibrarySpacing.Small))
                                Text(
                                    text = faq.answer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
