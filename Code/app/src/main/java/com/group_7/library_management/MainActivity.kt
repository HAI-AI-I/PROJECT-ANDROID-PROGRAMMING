package com.group_7.library_management

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.group_7.library_management.navigation.AppNavHost
import com.group_7.library_management.ui.theme.Library_managementTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.group_7.library_management.data.local.dao.BookDAO
import com.group_7.library_management.data.local.entity.BookEntity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var bookDao: BookDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Thêm dữ liệu mẫu
        lifecycleScope.launch {
            val sampleBooks = listOf(
                BookEntity("1", "Clean Architecture", "Robert C. Martin", "Lập trình", 180000, 2, 4.8, System.currentTimeMillis(), 100),
                BookEntity("2", "Design Patterns", "Gang of Four", "Lập trình", 150000, 5, 4.7, System.currentTimeMillis() - 86400000, 80),
                BookEntity("3", "Kotlin in Action", "Dmitry Jemerov", "Lập trình", 120000, 1, 4.9, System.currentTimeMillis() - 172800000, 120),
                BookEntity("4", "Cấu trúc dữ liệu và giải thuật nâng cao", "Nguyễn Văn A", "Lập trình", 150000, 3, 4.6, System.currentTimeMillis(), 50),
                BookEntity("5", "Hệ quản trị cơ sở dữ liệu quan hệ", "Trần Thị B", "Cơ sở dữ liệu", 120000, 0, 4.3, System.currentTimeMillis(), 30),
                BookEntity("6", "Mạng máy tính căn bản", "Lê Văn C", "Mạng máy tính", 100000, 5, 4.5, System.currentTimeMillis(), 40)
            )
            bookDao.insertBooks(sampleBooks)
        }

        enableEdgeToEdge()
        setContent {
            Library_managementTheme {
                AppNavHost()
            }
        }
    }
}