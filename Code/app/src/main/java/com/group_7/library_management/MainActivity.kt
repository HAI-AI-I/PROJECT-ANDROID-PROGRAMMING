package com.group_7.library_management

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.group_7.library_management.data.local.dao.NotificationDAO
import com.group_7.library_management.data.local.dao.SupportRequestDao
import com.group_7.library_management.data.local.entity.NotificationEntity
import com.group_7.library_management.data.local.entity.SupportRequestEntity
import com.group_7.library_management.navigation.AppNavHost
import com.group_7.library_management.ui.theme.Library_managementTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject
    lateinit var notificationDao: NotificationDAO

    @Inject
    lateinit var supportRequestDao: SupportRequestDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
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

            val sampleSupportRequests = listOf(
                SupportRequestEntity("1", "Clean Architecture", "Sách bị hỏng / lỗi", "Sách bị rách bìa, khó đọc.", "16/08/2026", "Đang xử lý"),
                SupportRequestEntity("2", "Design Patterns", "Không thể gia hạn sách", "Hệ thống báo lỗi khi bấm gia hạn lần 2.", "15/08/2026", "Đã giải quyết"),
                SupportRequestEntity("3", "Mạng máy tính căn bản", "Đã trả nhưng chưa cập nhật", "Đã trả sách tại quầy nhưng app vẫn hiện đang mượn.", "04/08/2026", "Đã giải quyết"),
                SupportRequestEntity("4", "Code Dạo Ký Sự", "Khác", "Yêu cầu đổi thời gian nhận sách.", "23/08/2026", "Đã giải quyết")
            )
            supportRequestDao.insertRequests(sampleSupportRequests)
        }

        enableEdgeToEdge()
        setContent {
            Library_managementTheme {
                AppNavHost()
            }
        }
    }
}
