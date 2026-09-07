package com.group_7.library_management

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.group_7.library_management.data.local.dao.BookDAO
import com.group_7.library_management.data.local.dao.NotificationDAO
import com.group_7.library_management.data.local.entity.BookEntity
import com.group_7.library_management.data.local.entity.NotificationEntity
import com.group_7.library_management.navigation.AppNavHost
import com.group_7.library_management.ui.theme.Library_managementTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var bookDao: BookDAO

    @Inject
    lateinit var notificationDao: NotificationDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

            val sampleNotifications = listOf(
                NotificationEntity(
                    title = "Clean Architecture còn 2 ngày nữa đến hạn trả",
                    message = "Vui lòng sắp xếp thời gian trả sách để tránh bị phạt phí quá hạn.",
                    time = "15 phút trước",
                    date = "04/09/2026",
                    type = "WARNING",
                    isRead = false,
                    createdAt = System.currentTimeMillis()-20
                ),
                NotificationEntity(
                    title = "Bạn đã mượn sách thành công",
                    message = "Sách \"Design Patterns\" đã được thêm vào tài khoản của bạn. Hạn trả: 19/09/2026.",
                    time = "2 giờ trước",
                    date = "04/09/2026",
                    type = "SUCCESS",
                    isRead = false,
                    createdAt = System.currentTimeMillis() - 22
                ),
                NotificationEntity(
                    title = "Sách Kotlin in Action đã quá hạn 3 ngày",
                    message = "Tài khoản của bạn đang bị tính phí phạt. Vui lòng hoàn trả sách ngay lập tức.",
                    time = "14:30",
                    date = "03/09/2026",
                    type = "ERROR",
                    isRead = true,
                    createdAt = System.currentTimeMillis() - 30
                ),
                NotificationEntity(
                    title = "Bảo trì hệ thống",
                    message = "Hệ thống thư viện sẽ tạm ngưng hoạt động từ 22:00 đến 02:00 ngày mai để bảo trì định kỳ.",
                    time = "09:00",
                    date = "01/09/2026",
                    type = "INFO",
                    isRead = true,
                    createdAt = System.currentTimeMillis() - 50
                ),
                NotificationEntity(
                    title="new book ",
                    message="new book is code with me",
                    time="now",
                    date="05/09/2006",
                    type="BOOK",
                    isRead=false,
                    createdAt=System.currentTimeMillis()-1
                ),
                NotificationEntity(
                    title="example notification",
                    message="this is example notification",
                    time="now",
                    date="05/09/2006",
                    type="INFO",
                    isRead=false,
                    createdAt=System.currentTimeMillis()
                )
            )
            //notificationDao.insertNotifications(sampleNotifications)
        }

        enableEdgeToEdge()
        setContent {
            Library_managementTheme {
                AppNavHost()
            }
        }
    }
}
