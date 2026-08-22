package com.group_7.library_management.ui.book
sealed class BookRoute(val route: String) {
    // 1. Màn hình Chi tiết sách
    object Detail : BookRoute("book_detail/{bookId}") {
        fun createRoute(bookId: String) = "book_detail/$bookId"
    }

    // 2. Màn hình Đánh giá & Bình luận
    object Reviews : BookRoute("book_reviews/{bookId}") {
        fun createRoute(bookId: String) = "book_reviews/$bookId"
    }

    // 3. Màn hình Xác nhận mượn sách
    object ConfirmBorrow : BookRoute("confirm_borrow/{bookId}") {
        fun createRoute(bookId: String) = "confirm_borrow/$bookId"
    }

    // 5. Màn hình Mượn thành công
    object Success : BookRoute("borrow_success/{transactionId}") {
        fun createRoute(id: String) = "borrow_success/$id"
    }

    // 6. Màn hình Mã QR giao dịch
    object QRCode : BookRoute("qr_code/{transactionId}") {
        fun createRoute(id: String) = "qr_code/$id"
    }

    // 7. Màn hình Mượn thất bại
    object Failure : BookRoute("borrow_failure")
}