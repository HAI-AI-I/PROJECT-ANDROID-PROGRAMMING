package com.group_7.library_management.models


data class Book(
    val id: String,
    val title: String,
    val author: String,
    val category: String,          // vd: "Lập trình", "Cơ sở dữ liệu" — hiện dạng nhãn UPPERCASE trên card
    val coverImageUrl: String? = null,
    val borrowFee: Long = 0L,      // "Giá mượn" hiển thị trên card, đơn vị VNĐ
    val availableCopies: Int = 0,  // 0 = đang hết, "Sẵn có (N bản)" khi > 0
    val rating: Double = 0.0,
    val createdAt:Long=0,
    val viewCount:Int=0,
)
