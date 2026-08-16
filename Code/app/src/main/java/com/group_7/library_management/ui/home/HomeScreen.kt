package com.group_7.library_management.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group_7.library_management.models.Book
import com.group_7.library_management.models.User
import com.group_7.library_management.ui.theme.*
import kotlinx.coroutines.launch

// ==========================================
// 1. MÀN HÌNH CHÍNH (CONTAINER QUẢN LÝ ĐIỀU HƯỚNG)
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
            // Hàm này giờ lấy từ file AppNavigationDrawer.kt
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
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
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
                    // Gọi sang màn hình NotificationsScreen nằm ở file NotificationScreen.kt
                    NotificationsScreen(
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
// 2. GIAO DIỆN TRANG CHỦ (HOME CONTENT)
// ==========================================
@Composable
fun HomeContent(
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    uiState: HomeUiState,
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
// 3. CÁC THÀNH PHẦN GIAO DIỆN CON (COMPONENTS) CỦA HOME
// ==========================================

@Composable
fun TopBarSection(onMenuClick: () -> Unit, onNotificationClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClick, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onSurface)
        }

        Text(
            text = "Library UTH",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        IconButton(onClick = onNotificationClick, modifier = Modifier.size(36.dp)) {
            Box {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.onSurface)
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error)
                        .border(1.5.dp, MaterialTheme.colorScheme.background, CircleShape)
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
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 42.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Khám phá cuốn sách tiếp theo của bạn",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                Text("Tìm kiếm sách, tác giả, ISBN", color = TextTertiary, fontSize = 14.sp)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextTertiary)
            },
            shape = RoundedCornerShape(27.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            singleLine = true
        )

        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(SurfaceVariant)
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
                color = MaterialTheme.colorScheme.onSurface
            )
            if (actionText.isNotEmpty()) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
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
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, Border, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Book,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
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
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = book.author,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            count = "3",
            iconTint = MaterialTheme.colorScheme.secondary,
            bgColor = MaterialTheme.colorScheme.surfaceVariant
        )
        StatusRowItem(
            icon = Icons.Default.Event,
            title = "Sắp đến hạn",
            count = "1",
            iconTint = Warning,
            bgColor = MaterialTheme.colorScheme.surfaceVariant
        )
        StatusRowItem(
            icon = Icons.Default.Warning,
            title = "Quá hạn",
            count = "0",
            iconTint = TextTertiary,
            bgColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun StatusRowItem(icon: ImageVector, title: String, count: String, iconTint: Color, bgColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Border, RoundedCornerShape(20.dp))
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
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = count,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun QRCheckInCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "QR Check-in",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Surface
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Xuất trình mã QR của bạn tại quầy thủ thư hoặc cổng tự động để vào thư viện hoặc mượn sách.",
                style = MaterialTheme.typography.bodyMedium,
                color = Surface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Surface)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mở QR", color = Surface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Surface)
                    .padding(12.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Icon(
                    Icons.Default.QrCode2,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = TextPrimary
                )
            }
        }
    }
}