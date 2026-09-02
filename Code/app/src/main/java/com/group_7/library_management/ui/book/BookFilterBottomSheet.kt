package com.group_7.library_management.ui.book

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.group_7.library_management.ui.theme.LibrarySpacing

enum class SortOption(val label: String) {
    NEWEST("Mới nhất"),
    POPULAR("Phổ biến"),
    PRICE_ASC("Giá thấp - cao"),
    PRICE_DESC("Giá cao - thấp"),
}

enum class UploadTimeOption(val label: String) {
    ANY_TIME("Bất kỳ lúc nào"),
    THIS_WEEK("Tuần này"),
    THIS_MONTH("Tháng này"),
    THIS_YEAR("Năm nay"),
}

enum class PriceRangeOption(val label: String) {
    ALL("Tất cả giá"),
    FREE("Miễn phí"),
    UNDER_100K("Dưới 100.000đ"),
    FROM_100K_TO_200K("100.000đ - 200.000đ"),
    OVER_200K("Trên 200.000đ"),
}

/**
 * BookFilterState
 * Toàn bộ lựa chọn lọc hiện tại. BookListScreen giữ state này, truyền
 * xuống bottom sheet; bấm "Áp dụng" mới thật sự lọc lại danh sách.
 */
data class BookFilterState(
    val sort: SortOption = SortOption.NEWEST,
    val selectedGenres: Set<String> = emptySet(),
    val minRating: Int = 0, // 0 = không lọc theo đánh giá
    val uploadTime: UploadTimeOption = UploadTimeOption.ANY_TIME,
    val priceRange: PriceRangeOption = PriceRangeOption.ALL,
) {
    val activeCount: Int
        get() = listOfNotNull(
            selectedGenres.takeIf { it.isNotEmpty() },
            minRating.takeIf { it > 0 },
            priceRange.takeIf { it != PriceRangeOption.ALL },
        ).size
}

private val AVAILABLE_GENRES = listOf("Công nghệ", "Kinh tế", "Văn học", "Khoa học", "Lịch sử")

/**
 * BookFilterBottomSheet
 * Popup lọc trượt từ dưới lên — KHÔNG phải trang/route riêng, khớp thiết kế
 * Stitch "Bộ lọc tìm kiếm": Sắp xếp / Thể loại / Đánh giá / Thời gian tải
 * lên / Khoảng giá mượn (thiết kế gốc ghi "Giá tiền cọc" — đổi tên nhãn
 * cho khớp field `borrowFee` trong model, ý nghĩa lọc giữ nguyên).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookFilterBottomSheet(
    initialFilter: BookFilterState,
    onDismiss: () -> Unit,
    onApply: (BookFilterState) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    var sort by remember { mutableStateOf(initialFilter.sort) }
    var genres by remember { mutableStateOf(initialFilter.selectedGenres) }
    var minRating by remember { mutableStateOf(initialFilter.minRating) }
    var uploadTime by remember { mutableStateOf(initialFilter.uploadTime) }
    var priceRange by remember { mutableStateOf(initialFilter.priceRange) }
    var uploadTimeMenuExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LibrarySpacing.Medium)
                .padding(bottom = LibrarySpacing.Large),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Bộ lọc tìm kiếm", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Đóng") }
            }
            Spacer(Modifier.height(LibrarySpacing.Small))

            SectionLabel("Sắp xếp")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SortOption.entries.toList()) { option ->
                    FilterChip(selected = sort == option, onClick = { sort = option }, label = { Text(option.label) })
                }
            }

            Spacer(Modifier.height(LibrarySpacing.Medium))

            SectionLabel("Thể loại")
            // FlowRow chưa stable ở mọi phiên bản Compose đang dùng trong project,
            // dùng LazyRow cho chắc ăn (cuộn ngang thay vì tự xuống dòng).
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(AVAILABLE_GENRES) { genre ->
                    FilterChip(
                        selected = genre in genres,
                        onClick = { genres = if (genre in genres) genres - genre else genres + genre },
                        label = { Text(genre) },
                    )
                }
            }

            Spacer(Modifier.height(LibrarySpacing.Medium))

            SectionLabel("Đánh giá")
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (star in 1..5) {
                    IconButton(onClick = { minRating = if (minRating == star) 0 else star }) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "$star sao",
                            tint = if (star <= minRating) Color(0xFFF5B301) else MaterialTheme.colorScheme.outlineVariant,
                        )
                    }
                }
                Text("Trở lên", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(LibrarySpacing.Medium))

            SectionLabel("Thời gian tải lên")
            Box {
                OutlinedButton(onClick = { uploadTimeMenuExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(uploadTime.label, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Start)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(expanded = uploadTimeMenuExpanded, onDismissRequest = { uploadTimeMenuExpanded = false }) {
                    UploadTimeOption.entries.forEach { option ->
                        DropdownMenuItem(text = { Text(option.label) }, onClick = {
                            uploadTime = option
                            uploadTimeMenuExpanded = false
                        })
                    }
                }
            }

            Spacer(Modifier.height(LibrarySpacing.Medium))

            SectionLabel("Khoảng giá mượn")
            Column {
                PriceRangeOption.entries.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        RadioButton(selected = priceRange == option, onClick = { priceRange = option })
                        Text(option.label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(Modifier.height(LibrarySpacing.Large))

            Row(horizontalArrangement = Arrangement.spacedBy(LibrarySpacing.Small)) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        sort = SortOption.NEWEST
                        genres = emptySet()
                        minRating = 0
                        uploadTime = UploadTimeOption.ANY_TIME
                        priceRange = PriceRangeOption.ALL
                    },
                ) { Text("Thiết lập lại") }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { onApply(BookFilterState(sort, genres, minRating, uploadTime, priceRange)) },
                ) { Text("Áp dụng") }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(LibrarySpacing.ExtraSmall))
}
