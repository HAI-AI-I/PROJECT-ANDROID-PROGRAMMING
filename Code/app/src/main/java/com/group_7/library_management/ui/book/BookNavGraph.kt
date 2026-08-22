package com.group_7.library_management.ui.book

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.addBookNavGraph(navController: NavController) {
    // 1. Màn hình Chi tiết sách
    composable(
        route = BookRoute.Detail.route,
        arguments = listOf(navArgument("bookId") { type = NavType.StringType })
    ) { backStackEntry ->
        val bookId = backStackEntry.arguments?.getString("bookId") ?: ""

        BookDetailScreen(
            bookId = bookId,
            onBack = {
                navController.popBackStack()
            },
            onNavigateToReviews = {
                navController.navigate(BookRoute.Reviews.createRoute(bookId))
            },
            onNavigateToBorrow = {
                navController.navigate(BookRoute.ConfirmBorrow.createRoute(bookId))
            }
        )
    }

    // 2. Màn hình Đánh giá & Bình luận
    composable(
        route = BookRoute.Reviews.route,
        arguments = listOf(navArgument("bookId") { type = NavType.StringType })
    ) { backStackEntry ->
        val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
        // TODO: BookReviewsScreen(bookId = bookId, onBack = { navController.popBackStack() })
    }

    // 3. Màn hình Xác nhận mượn sách
    composable(
        route = BookRoute.ConfirmBorrow.route,
        arguments = listOf(navArgument("bookId") { type = NavType.StringType })
    ) { backStackEntry ->
        val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
        // TODO: ConfirmBorrowScreen(bookId = bookId, ...)
    }

    // 5. Màn hình Mượn thành công
    composable(
        route = BookRoute.Success.route,
        arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
    ) { backStackEntry ->
        val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
        // TODO: BorrowSuccessScreen(transactionId = transactionId, ...)
    }

    // 6. Màn hình Mã QR giao dịch
    composable(
        route = BookRoute.QRCode.route,
        arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
    ) { backStackEntry ->
        val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
        // TODO: QRCodeScreen(transactionId = transactionId, ...)
    }

    // 7. Màn hình Mượn thất bại
    composable(route = BookRoute.Failure.route) {
        // TODO: BorrowFailureScreen(...)
    }
}