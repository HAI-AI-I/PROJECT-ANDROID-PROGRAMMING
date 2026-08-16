package com.group_7.library_management.ui.book

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.group_7.library_management.R
import com.group_7.library_management.ui.theme.LibrarySpacing

@Composable
fun BookDetailScreen(
    bookId: String = "clean_code",
    onBack: () -> Unit = {},
    onNavigateToReviews: () -> Unit = {},
    onNavigateToBorrow: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            Surface(
                shadowElevation = LibrarySpacing.Small,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = LibrarySpacing.Medium, vertical = LibrarySpacing.Small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(LibrarySpacing.Small)
                ) {
                    // Nút Chuông góc trái
                    OutlinedIconButton(
                        onClick = { },
                        modifier = Modifier.size(LibrarySpacing.Huge),
                        shape = CircleShape,
                        colors = IconButtonDefaults.outlinedIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notification",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Nút Mượn sách
                    Button(
                        onClick = onNavigateToBorrow,
                        modifier = Modifier
                            .weight(1f)
                            .height(LibrarySpacing.Huge),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Bookmark,
                                contentDescription = null,
                                modifier = Modifier.size(LibrarySpacing.Medium),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(LibrarySpacing.Small))
                            Text(
                                text = "Mượn sách",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bìa Sách
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LibrarySpacing.Huge * 6.5f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = LibrarySpacing.Medium, vertical = LibrarySpacing.ExtraSmall)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.cleancode),
                    contentDescription = "Cover",
                    modifier = Modifier
                        .height(LibrarySpacing.Huge * 5f)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Fit
                )
            }

            // Phần thông tin chi tiết (Bo góc)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = -LibrarySpacing.Large),
                shape = RoundedCornerShape(
                    topStart = LibrarySpacing.ExtraLarge,
                    topEnd = LibrarySpacing.ExtraLarge
                ),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = LibrarySpacing.Medium, vertical = LibrarySpacing.Large)
                ) {
                    Text(
                        text = "Clean Code",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Robert C. Martin",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(LibrarySpacing.Small))

                    // Đánh giá
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateToReviews() }
                    ) {
                        repeat(5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(LibrarySpacing.Medium)
                            )
                        }
                        Spacer(modifier = Modifier.width(LibrarySpacing.ExtraSmall))
                        Text(
                            text = "4.8/5 ",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "(128 lượt đánh giá)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

                    // Thông tin 2 cột
                    Row(horizontalArrangement = Arrangement.spacedBy(LibrarySpacing.Small)) {
                        DetailChip(title = "THỂ LOẠI", value = "Lập trình", modifier = Modifier.weight(1f))
                        DetailChip(title = "NHÀ XUẤT BẢN", value = "Prentice Hall", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(LibrarySpacing.Small))
                    Row(horizontalArrangement = Arrangement.spacedBy(LibrarySpacing.Small)) {
                        DetailChip(title = "NGÀY XUẤT BẢN", value = "01/08/2008", modifier = Modifier.weight(1f))
                        DetailChip(title = "NGÔN NGỮ", value = "Tiếng Anh", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(LibrarySpacing.Small))
                    Row(horizontalArrangement = Arrangement.spacedBy(LibrarySpacing.Small)) {
                        DetailChip(title = "ISBN", value = "9780132350884", modifier = Modifier.weight(1f))
                        DetailChip(title = "SỐ TRANG", value = "464 trang", modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(LibrarySpacing.Small))

                    // Thẻ Giá tiền cọc
                    Row(horizontalArrangement = Arrangement.spacedBy(LibrarySpacing.Small)) {
                        HighlightChip(title = "GIÁ TIỀN CỌC", value = "150.000đ", modifier = Modifier.weight(0.48f))
                        Spacer(modifier = Modifier.weight(0.52f))
                    }

                    Spacer(modifier = Modifier.height(LibrarySpacing.Small))

                    // Thẻ Trạng thái Status
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(LibrarySpacing.Medium)) {
                            Text(
                                text = "STATUS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(LibrarySpacing.Small)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(LibrarySpacing.Small))
                                Text(
                                    text = "Sẵn có (3 / 10 bản)",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(LibrarySpacing.Large))

                    // Description
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(LibrarySpacing.Medium)
                        )
                        Spacer(modifier = Modifier.width(LibrarySpacing.Small))
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(LibrarySpacing.Small))
                    Text(
                        text = "Mã sạch là mã có thể đọc được và dễ bảo trì. Cuốn sách này cung cấp các nguyên tắc, mẫu và thực tiễn để viết mã sạch, giúp các nhà phát triển phần mềm nâng cao chất lượng công việc của họ. Nó chia sẻ những hiểu biết sâu sắc về cách định dạng, đặt tên, cấu trúc và kiểm thử mã nguồn một cách hiệu quả trong môi trường làm việc thực tế.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(LibrarySpacing.Large))

                    // Sách liên quan
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(LibrarySpacing.Medium)
                        )
                        Spacer(modifier = Modifier.width(LibrarySpacing.Small))
                        Text(
                            text = "Sách liên quan",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

                    val related = listOf(
                        Triple("The Pragmatic...", "Andrew Hunt", "https://m.media-amazon.com/images/I/41as+Tjg13L.jpg"),
                        Triple("Refactoring", "Martin Fowler", "https://m.media-amazon.com/images/I/41AptR55cFL.jpg"),
                        Triple("Test Driven...", "Kent Beck", "https://m.media-amazon.com/images/I/5113Xm9+KGL.jpg")
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(LibrarySpacing.Medium)) {
                        items(related) { item ->
                            Column(modifier = Modifier.width(LibrarySpacing.ExtraLarge * 2.5f)) {
                                AsyncImage(
                                    model = item.third,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(LibrarySpacing.ExtraLarge * 3f)
                                        .fillMaxWidth()
                                        .clip(MaterialTheme.shapes.small),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall))
                                Text(
                                    text = item.first,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = item.second,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailChip(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(LibrarySpacing.Medium)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun HighlightChip(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(LibrarySpacing.Medium)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(LibrarySpacing.ExtraSmall))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}