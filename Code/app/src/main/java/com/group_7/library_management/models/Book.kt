package com.group_7.library_management.models

/**
 * Book
 * Model dùng chung cho toàn app: danh sách sách, lịch sử mượn, sách của tôi,
 * quản lý sách (Thủ thư)... Khớp theo thiết kế Stitch "Library UTH 13".
 */
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val category: String,          // vd: "Lập trình", "Cơ sở dữ liệu" — hiện dạng nhãn UPPERCASE trên card
    val coverImageUrl: String? = null,
    val borrowFee: Long = 0L,      // "Giá mượn" hiển thị trên card, đơn vị VNĐ
    val availableCopies: Int = 0,  // 0 = đang hết, "Sẵn có (N bản)" khi > 0
    val rating: Double = 0.0,
    val format: BookFormat = BookFormat.PHYSICAL,
)

enum class BookFormat {
    PHYSICAL,
    EBOOK,
}
