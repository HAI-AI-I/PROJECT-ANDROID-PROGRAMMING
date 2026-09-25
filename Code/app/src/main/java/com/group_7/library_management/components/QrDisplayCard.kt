package com.group_7.library_management.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

/**
 * Hiển thị mã QR kèm thông tin và các nút hành động.
 *
 * Component này chỉ chịu trách nhiệm về UI. Toàn bộ logic lưu ảnh và
 * chia sẻ được xử lý bởi màn hình gọi thông qua [onSave] và [onShare],
 * giữ đúng nguyên tắc separation of concerns của dự án.
 *
 * @param qrContent Nội dung được mã hóa trong mã QR.
 * @param code      Chuỗi mã hiển thị bên dưới ảnh QR (ví dụ: mã đơn mượn, mã thành viên).
 * @param title     Tiêu đề chính (ví dụ: tên sách, tên thành viên).
 * @param description Mô tả hướng dẫn người dùng.
 * @param onSave    Callback khi nhấn "Lưu ảnh". Truyền [Bitmap] QR đã render để Screen xử lý.
 * @param onShare   Callback khi nhấn "Chia sẻ". Truyền [Bitmap] QR đã render để Screen xử lý.
 * @param onBackToHome Callback về trang chủ. Nếu null, nút sẽ không hiển thị.
 * @param details   Slot nội dung bổ sung, hiển thị bên dưới phần mô tả.
 */
@Composable
fun QrDisplayCard(
    qrContent: String,
    code: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onSave: ((Bitmap) -> Unit)? = null,
    onShare: ((Bitmap) -> Unit)? = null,
    onBackToHome: (() -> Unit)? = null,
    details: @Composable ColumnScope.() -> Unit = {}
) {
    val bitmap = remember(qrContent) { createQrBitmap(qrContent) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Mã QR $code",
                modifier = Modifier.size(280.dp).padding(18.dp).background(Color.White)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = code,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(14.dp))

        details()

        // Chỉ hiển thị khu vực action buttons nếu có ít nhất 1 callback
        if (onSave != null || onShare != null || onBackToHome != null) {
            Spacer(Modifier.height(22.dp))

            if (onSave != null || onShare != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (onSave != null) {
                        OutlinedButton(
                            onClick = { onSave(bitmap) },
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            Icon(Icons.Outlined.Download, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Lưu ảnh")
                        }
                    }
                    if (onShare != null) {
                        OutlinedButton(
                            onClick = { onShare(bitmap) },
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            Icon(Icons.Outlined.Share, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Chia sẻ")
                        }
                    }
                }
            }

            if (onBackToHome != null) {
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = onBackToHome,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Outlined.Home, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Về trang chủ", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun createQrBitmap(content: String, size: Int = 720): Bitmap {
    val matrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
    val pixels = IntArray(size * size)
    for (y in 0 until size) {
        for (x in 0 until size) {
            pixels[y * size + x] = if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
        }
    }
    return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
        setPixels(pixels, 0, size, 0, 0, size, size)
    }
}
