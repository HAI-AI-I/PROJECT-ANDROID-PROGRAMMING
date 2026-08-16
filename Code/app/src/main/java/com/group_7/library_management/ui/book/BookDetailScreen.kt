package com.group_7.library_management.ui.book

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.group_7.library_management.R
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
                shadowElevation = 12.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Nút Chuông góc trái
                    OutlinedIconButton(
                        onClick = { },
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = Color(0xFFF2F2F7))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notification",
                            tint = Color.Black
                        )
                    }

                    // Nút Mượn sách
                    Button(
                        onClick = onNavigateToBorrow,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF060B50))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Bookmark,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mượn sách",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
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
                .background(Color(0xFFF8F8FC))
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bìa Sách
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
                    .background(Color(0xFFEBEBF2)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = Color.White
                        )
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.cleancode),
                    contentDescription = "Cover",
                    modifier = Modifier
                        .height(240.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            // Phần thông tin chi tiết (Bo góc trắng)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = Color(0xFFF8F8FC)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "Clean Code",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Robert C. Martin",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF202773)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Đánh giá
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateToReviews() }
                    ) {
                        repeat(5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "4.8/5 ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "(128 lượt đánh giá)", fontSize = 12.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Thông tin 2 cột
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailChip(title = "THỂ LOẠI", value = "Lập trình", modifier = Modifier.weight(1f))
                        DetailChip(title = "NHÀ XUẤT BẢN", value = "Prentice Hall", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailChip(title = "NGÀY XUẤT BẢN", value = "01/08/2008", modifier = Modifier.weight(1f))
                        DetailChip(title = "NGÔN NGỮ", value = "Tiếng Anh", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailChip(title = "ISBN", value = "9780132350884", modifier = Modifier.weight(1f))
                        DetailChip(title = "SỐ TRANG", value = "464 trang", modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Thẻ Giá tiền cọc (Màu tím nhạt)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        HighlightChip(title = "GIÁ TIỀN CỌC", value = "150.000đ", modifier = Modifier.weight(0.48f))
                        Spacer(modifier = Modifier.weight(0.52f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Thẻ Trạng thái Status (Màu tím nhạt)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE2E4FA))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "STATUS", fontSize = 10.sp, color = Color(0xFF4C55B4), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color(0xFF0A1268), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sẵn có (3 / 10 bản)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0A1268)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Description
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFF202773), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Description", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Mã sạch là mã có thể đọc được và dễ bảo trì. Cuốn sách này cung cấp các nguyên tắc, mẫu và thực tiễn để viết mã sạch, giúp các nhà phát triển phần mềm nâng cao chất lượng công việc của họ. Nó chia sẻ những hiểu biết sâu sắc về cách định dạng, đặt tên, cấu trúc và kiểm thử mã nguồn một cách hiệu quả trong môi trường làm việc thực tế.",
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sách liên quan
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.MenuBook, contentDescription = null, tint = Color(0xFF202773), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Sách liên quan", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    val related = listOf(
                        Triple("The Pragmatic...", "Andrew Hunt", "https://m.media-amazon.com/images/I/41as+Tjg13L.jpg"),
                        Triple("Refactoring", "Martin Fowler", "https://m.media-amazon.com/images/I/41AptR55cFL.jpg"),
                        Triple("Test Driven...", "Kent Beck", "https://m.media-amazon.com/images/I/5113Xm9+KGL.jpg")
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(related) { item ->
                            Column(modifier = Modifier.width(110.dp)) {
                                AsyncImage(
                                    model = item.third,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(135.dp)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = item.first, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text(text = item.second, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F6))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun HighlightChip(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE2E4FA))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontSize = 9.sp, color = Color(0xFF4C55B4), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0A1268))
        }
    }
}