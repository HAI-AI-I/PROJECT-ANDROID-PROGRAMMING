package com.group_7.library_management.navigation

object Routes {
    const val SPLASH = "splash"

    // Auth group
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val CONFIRM_CODE_RESET_AUTH = "confirm_code_reset_auth"
    const val CONFIRM_CODE_REGIS_AUTH =
        "confirm_code_regis_auth/{registrationId}/{verificationMethod}"
    fun confirmRegistration(registrationId: String, verificationMethod: String) =
        "confirm_code_regis_auth/$registrationId/$verificationMethod"
    const val RESET_PASSWORD_AUTH = "reset_password_auth"

    // Main group
    const val HOME = "home"
    const val BOOKS = "books"
    const val SCAN_QR="scan_qr"
    const val BORROW = "borrow"
    const val HISTORY = "history"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val NOTIFICATIONS = "notifications"
    const val FAVORITE = "favorite"
    const val SETTINGS = "settings"
    const val HELP = "help"
    const val FAQ = "faq/{topic}"
    const val CREATE_SUPPORT_REQUEST = "create_support_request"
    const val MY_SUPPORT_REQUESTS = "my_support_requests"
    const val CONTACT_LIBRARIAN = "contact_librarian"

    // Book Flow
    const val BOOK_DETAIL = "book_detail/{bookId}"
    fun bookDetail(bookId: String) = "book_detail/$bookId"
    const val BOOK_REVIEWS = "book_reviews/{bookId}"
    fun bookReviews(bookId: String) = "book_reviews/$bookId"
    const val BOOK_BORROW_CONFIRM = "book_borrow_confirm/{bookId}"
    fun bookBorrowConfirm(bookId: String) = "book_borrow_confirm/$bookId"
    const val BORROW_SUCCESS = "borrow_success/{orderId}"
    fun borrowSuccess(orderId: Long) = "borrow_success/$orderId"
    const val BORROW_QR = "borrow_qr/{orderId}"
    fun borrowQr(orderId: Long) = "borrow_qr/$orderId"
    const val BORROW_FAILURE = "borrow_failure"

    const val DASHBOARD="dashboard"
}
