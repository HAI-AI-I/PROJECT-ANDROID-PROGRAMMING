package com.group_7.library_management.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group_7.library_management.models.Book
import com.group_7.library_management.models.NotificationItem
import com.group_7.library_management.models.NotificationType
import com.group_7.library_management.models.User
import com.group_7.library_management.ui.theme.AccentBlue
import com.group_7.library_management.ui.theme.BackgroundLight
import com.group_7.library_management.ui.theme.DarkBlue
import com.group_7.library_management.ui.theme.ErrorColor
import com.group_7.library_management.ui.theme.InfoColor
import com.group_7.library_management.ui.theme.SuccessColor
import com.group_7.library_management.ui.theme.SurfaceGray
import com.group_7.library_management.ui.theme.TextGray
import com.group_7.library_management.ui.theme.WarningColor
import kotlinx.coroutines.launch

// ==========================================
// 3. MÀN HÌNH CHÍNH (CONTAINER QUẢN LÝ ĐIỀU HƯỚNG)
// ==========================================
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()


    var currentRoute by remember { mutableStateOf("home") }
    var selectedBottomTab by remember { mutableIntStateOf(0) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppNavigationDrawer(
                user = uiState.currentUser,
                currentRoute = currentRoute,
                onItemClick = { route ->
                    currentRoute = route
                    scope.launch { drawerState.close() }
                },
                onLogout = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            containerColor = BackgroundLight,
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                    modifier = Modifier.height(80.dp)
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Home", style = MaterialTheme.typography.labelSmall) },
                        selected = selectedBottomTab == 0 && currentRoute == "home",
                        onClick = {
                            selectedBottomTab = 0
                            currentRoute = "home"
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Book, contentDescription = null) },
                        label = { Text("Books", style = MaterialTheme.typography.labelSmall) },
                        selected = selectedBottomTab == 1,
                        onClick = { selectedBottomTab = 1 }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.LibraryBooks, contentDescription = null) },
                        label = { Text("My Borrowing", style = MaterialTheme.typography.labelSmall) },
                        selected = selectedBottomTab == 2,
                        onClick = { selectedBottomTab = 2 }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Profile", style = MaterialTheme.typography.labelSmall) },
                        selected = selectedBottomTab == 3,
                        onClick = { selectedBottomTab = 3 }
                    )
                }
            }
        ) { paddingValues ->
            // ĐIỀU HƯỚNG HIỂN THỊ NỘI DUNG
            when (currentRoute) {
                "home" -> {
                    HomeContent(
                        paddingValues = paddingValues,
                        uiState = uiState,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onNotificationClick = { currentRoute = "notifications" }
                    )
                }
                "notifications" -> {
                    NotificationsContent(
                        paddingValues = paddingValues,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                }
                else -> {
                    HomeContent(
                        paddingValues = paddingValues,
                        uiState = uiState,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onNotificationClick = { currentRoute = "notifications" }
                    )
                }
            }
        }
    }
}

// ==========================================
// 4. GIAO DIỆN TRANG CHỦ (HOME CONTENT)
// ==========================================
@Composable
fun HomeContent(
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    uiState: HomeUiState, // Giả sử model state của bạn tên là HomeUiState
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            TopBarSection(
                onMenuClick = onMenuClick,
                onNotificationClick = onNotificationClick
            )
        }

        item { GreetingSection(user = uiState.currentUser) }
        item { SearchSection() }
        item {
            BookListSection(
                title = "Sách phổ biến",
                actionText = "Tất cả thể loại >",
                books = uiState.popularBooks
            )
        }
        item {
            BookListSection(
                title = "Sách mới",
                actionText = "Xem tất cả",
                books = uiState.popularBooks
            )
        }
        item {
            BookListSection(
                title = "Sách dành cho bạn",
                actionText = "",
                books = uiState.popularBooks
            )
        }
        item { BorrowStatusSection() }
        item { QRCheckInCard() }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

// ==========================================
// 5. GIAO DIỆN THÔNG BÁO (NOTIFICATIONS CONTENT)
// ==========================================
@Composable
fun NotificationsContent(
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    onMenuClick: () -> Unit
) {
    val notifications = listOf(
        NotificationItem(
            id = "1",
            title = "Clean Code còn 2 ngày nữa đến hạn trả",
            message = "Vui lòng sắp xếp thời gian trả sách để tránh bị phạt phí quá hạn.",
            time = "10 phút trước",
            type = NotificationType.WARNING
        ),
        NotificationItem(
            id = "2",
            title = "Bạn đã mượn sách thành công",
            message = "Sách \"Design Patterns\" đã được thêm vào tài khoản của bạn. Hạn trả: 15/11/2023.",
            time = "2 giờ trước",
            type = NotificationType.SUCCESS
        ),
        NotificationItem(
            id = "3",
            title = "Sách Kotlin in Action đã quá hạn 3 ngày",
            message = "Tài khoản của bạn đang bị tính phí phạt. Vui lòng hoàn trả sách ngay lập tức.",
            time = "Hôm qua",
            type = NotificationType.ERROR
        ),
        NotificationItem(
            id = "4",
            title = "Bảo trì hệ thống",
            message = "Hệ thống thư viện sẽ tạm ngưng hoạt động từ 22:00 đến 02:00 ngày mai để bảo trì định kỳ.",
            time = "3 ngày trước",
            type = NotificationType.INFO
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            // Dùng chung TopBarSection với icon thông báo (không có chấm đỏ)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenuClick, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.Black)
                }

                Text(
                    text = "Library UTH",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue
                )

                IconButton(onClick = { }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = DarkBlue)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Thông báo",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue
                )

                Row(
                    modifier = Modifier.clickable { /* Xử lý đánh dấu đã đọc */ },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "Mark as read",
                        tint = InfoColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Đánh dấu đã đọc tất cả",
                        style = MaterialTheme.typography.labelLarge,
                        color = InfoColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(4.dp)) }

        items(notifications) { notification ->
            NotificationCard(notification)
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

// ==========================================
// 6. CÁC THÀNH PHẦN GIAO DIỆN CON (COMPONENTS)
// ==========================================
@Composable
fun TopBarSection(onMenuClick: () -> Unit, onNotificationClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClick, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.Black)
        }

        Text(
            text = "Library UTH",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = DarkBlue
        )

        IconButton(onClick = onNotificationClick, modifier = Modifier.size(36.dp)) {
            Box {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.Black)
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .border(1.5.dp, BackgroundLight, CircleShape)
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Composable
fun GreetingSection(user: User?) {
    Column {
        Text(
            text = "Chào bạn,\n${user?.name ?: "Nam"} \uD83D\uDC4B",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 34.sp),
            fontWeight = FontWeight.ExtraBold,
            color = DarkBlue,
            lineHeight = 42.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Khám phá cuốn sách tiếp theo của bạn",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.DarkGray
        )
    }
}

@Composable
fun SearchSection() {
    var searchQuery by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .weight(1f)
                .height(54.dp),
            placeholder = {
                Text("Tìm kiếm sách, tác giả, ISBN", color = Color.Gray, fontSize = 14.sp)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.DarkGray)
            },
            shape = RoundedCornerShape(27.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedContainerColor = SurfaceGray,
                focusedContainerColor = SurfaceGray
            ),
            singleLine = true
        )

        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(SurfaceGray)
        )
    }
}

@Composable
fun BookListSection(title: String, actionText: String, books: List<Book>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            if (actionText.isNotEmpty()) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge,
                    color = DarkBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(books) { book ->
                BookItemCard(book)
            }
        }
    }
}

@Composable
fun BookItemCard(book: Book) {
    Column(modifier = Modifier.width(130.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Book,
                    contentDescription = null,
                    tint = DarkBlue.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = book.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = book.author,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            maxLines = 1
        )
    }
}

@Composable
fun BorrowStatusSection() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        StatusRowItem(
            icon = Icons.Default.LibraryBooks,
            title = "Đang mượn",
            count = "2",
            iconTint = Color(0xFF4255C8),
            bgColor = Color(0xFFEEF0FC)
        )
        StatusRowItem(
            icon = Icons.Default.Event,
            title = "Sắp đến hạn",
            count = "1",
            iconTint = Color(0xFFE53935),
            bgColor = Color(0xFFFDECEB)
        )
        StatusRowItem(
            icon = Icons.Default.Warning,
            title = "Quá hạn",
            count = "0",
            iconTint = Color.Gray,
            bgColor = Color(0xFFF5F5F5)
        )
    }
}

@Composable
fun StatusRowItem(icon: ImageVector, title: String, count: String, iconTint: Color, bgColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
        Text(
            text = count,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = DarkBlue
        )
    }
}

@Composable
fun QRCheckInCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "QR Check-in",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Xuất trình mã QR của bạn tại quầy thủ thư hoặc cổng tự động để vào thư viện hoặc mượn sách.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mở QR", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color.White)
                    .padding(12.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Icon(
                    Icons.Default.QrCode2,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.Black
                )
            }
        }
    }
}