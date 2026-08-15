package com.group_7.library_management.ui.qrscan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * QrScannerScreen
 * Khớp thiết kế Stitch "Quét mã QR": nền camera (placeholder tối, chưa gắn
 * CameraX thật), khung quét vuông ở giữa, hướng dẫn phía dưới, nút đóng.
 *
 * TODO: project chưa có CameraX + ML Kit Barcode Scanning. Khi thêm:
 *   implementation("androidx.camera:camera-camera2:...")
 *   implementation("androidx.camera:camera-view:...")
 *   implementation("com.google.mlkit:barcode-scanning:...")
 * thay Box nền đen bằng CameraX PreviewView + phân tích khung hình qua ML Kit.
 */
@Composable
fun QrScannerScreen(
    onClose: () -> Unit = {},
    onCodeScanned: (String) -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // TODO: thay bằng CameraX PreviewView thật
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A1A)))

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.15f), MaterialTheme.shapes.small),
        ) {
            Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.White)
        }

        Text(
            "Quét mã QR",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp),
        )

        // Khung quét vuông ở giữa màn hình.
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(240.dp)
                .border(2.dp, Color.White, MaterialTheme.shapes.medium),
        )

        Text(
            "Đặt mã QR vào khung để quét",
            color = Color.White.copy(alpha = 0.85f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp),
        )
    }
}
