package com.group_7.library_management.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.group_7.library_management.MainActivity
import com.group_7.library_management.R

object LibraryNotificationManager {
    const val CHANNEL_AVAILABILITY = "book_availability"
    const val CHANNEL_LOANS = "loan_reminders"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannels(
            listOf(
                NotificationChannel(
                    CHANNEL_AVAILABILITY,
                    "Sách có sẵn",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = "Báo khi sách bạn theo dõi đã có thể mượn" },
                NotificationChannel(
                    CHANNEL_LOANS,
                    "Nhắc hạn trả sách",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = "Nhắc sách sắp đến hạn hoặc đã quá hạn" }
            )
        )
    }

    fun showBookAvailable(context: Context, bookId: String, bookTitle: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return

        val openApp = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("book_id", bookId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            bookId.hashCode(),
            openApp,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_AVAILABILITY)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Sách đã có thể mượn")
            .setContentText("\"$bookTitle\" hiện đã có bản trống. Mở ứng dụng để mượn ngay.")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "\"$bookTitle\" hiện đã có bản trống. Mở ứng dụng để mượn ngay."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        NotificationManagerCompat.from(context).notify(bookId.hashCode(), notification)
    }

    fun showTrackingEnabled(context: Context, bookId: String, bookTitle: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return

        val notification = NotificationCompat.Builder(context, CHANNEL_AVAILABILITY)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Đã bật thông báo sách")
            .setContentText("Bạn sẽ được báo khi \"$bookTitle\" có bản trống.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context)
            .notify("tracking_$bookId".hashCode(), notification)
    }
}
