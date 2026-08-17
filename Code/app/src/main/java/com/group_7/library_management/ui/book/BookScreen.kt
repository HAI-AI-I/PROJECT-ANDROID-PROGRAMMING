package com.group_7.library_management.ui.book

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun BookScreen(
    bookId: String = "clean_code",
    viewModel: BookViewModel = viewModel()
) {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = BookRoute.Detail.createRoute(bookId)
        ) {
            // 1. Màn hình Chi tiết sách
            composable(BookRoute.Detail.route) {
                BookDetailScreen(
                    bookId = bookId,
                    onBack = { navController.popBackStack() },
                    onNavigateToReviews = {
                        navController.navigate(BookRoute.Reviews.createRoute(bookId))
                    },
                    onNavigateToBorrow = {
                        navController.navigate(BookRoute.ConfirmBorrow.createRoute(bookId))
                    }
                )
            }

            // 2. Màn hình Đánh giá & Bình luận
            composable(BookRoute.Reviews.route) {
                BookReviewsScreen(
                    onBack = { navController.popBackStack() },
                    onWriteReview = { }
                )
            }

            // 3. Màn hình Xác nhận mượn sách
            composable(BookRoute.ConfirmBorrow.route) {
                BorrowConfirmScreen(
                    bookId = bookId,
                    viewModel = viewModel,
                    onSuccess = { transactionId: String ->
                        navController.navigate(BookRoute.Success.createRoute(transactionId)) {
                            popUpTo(BookRoute.Detail.route) { inclusive = false }
                        }
                    },
                    onFailure = {
                        navController.navigate(BookRoute.Failure.route)
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // 4. Màn hình Mượn thành công
            composable(BookRoute.Success.route) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getString("id") ?: "TX-998823"
                BorrowSuccessScreen(
                    transactionId = transactionId,
                    onViewQrCode = { id: String ->
                        navController.navigate(BookRoute.QRCode.createRoute(id))
                    },
                    onBackToHome = {
                        navController.popBackStack(BookRoute.Detail.route, inclusive = true)
                    }
                )
            }

            // 5. Màn hình Mã QR Giao dịch
            composable(BookRoute.QRCode.route) {
                PlaceholderScreen("Màn hình Mã QR Giao dịch")
            }

            // 6. Màn hình Mượn thất bại
            composable(BookRoute.Failure.route) {
                PlaceholderScreen("Màn hình Mượn thất bại")
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}
