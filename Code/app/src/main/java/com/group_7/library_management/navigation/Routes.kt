package com.group_7.library_management.navigation

object Routes {
    const val SPLASH = "splash"

    // Auth group
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val CONFIRM_CODE_RESET_AUTH = "confirm_code_reset_auth"
    const val CONFIRM_CODE_REGIS_AUTH = "confirm_code_regis_auth"
    const val RESET_PASSWORD_AUTH = "reset_password_auth"

    // Main group
    const val HOME = "home"
    const val BOOKS = "books"
    const val MY_BOOKS = "my_books"
    const val HISTORY = "history"
    const val PROFILE = "profile"
    const val NOTIFICATIONS = "notifications"
    const val FAVORITE = "favorite"
    const val SETTINGS = "settings"
    const val HELP = "help"

    // Book Flow
    const val BOOK_DETAIL = "book_detail/{bookId}"
}
