package com.group_7.library_management.ui.book

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.group_7.library_management.ui.theme.LibrarySpacing
import com.group_7.library_management.ui.theme.TextSecondary

/**
 * EmptyBooksState
 * Khớp thiết kế Stitch "Không tìm thấy kết quả": khung ảnh minh họa (ở đây
 * dùng icon hộp rỗng thay ảnh thật vì chưa có asset), tiêu đề, mô tả, nút
 * "Xem sách phổ biến". Không phải trang riêng — BookListScreen gọi thay
 * cho LazyColumn khi danh sách lọc/tìm ra rỗng.
 */
@Composable
fun EmptyBooksState(
    onViewPopularClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(LibrarySpacing.ExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            // TODO: thay bằng ảnh minh họa thật (Image/AsyncImage) khi có asset từ thiết kế.
            Icon(
                Icons.Outlined.Inventory2,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = TextSecondary,
            )
        }
        Spacer(Modifier.height(LibrarySpacing.Large))
        Text("Không tìm thấy sách", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(LibrarySpacing.ExtraSmall))
        Text(
            "Thử tìm kiếm với từ khóa khác hoặc kiểm tra lại chính tả.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(LibrarySpacing.Large))
        Button(onClick = onViewPopularClick) {
            Icon(Icons.Default.NorthEast, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Xem sách phổ biến")
        }
    }
}
