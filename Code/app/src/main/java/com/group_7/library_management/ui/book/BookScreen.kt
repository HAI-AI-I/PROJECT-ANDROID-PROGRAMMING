package com.group_7.library_management.ui.book

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun BookScreen(bookId: String = "1") {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = BookRoute.Detail.createRoute(bookId)) {
        composable(BookRoute.Detail.route, listOf(navArgument("bookId") { type = NavType.StringType })) { entry ->
            val currentBookId = entry.arguments?.getString("bookId") ?: bookId
            BookDetailScreen(
                onBack = { navController.popBackStack() },
                onNavigateToReviews = { navController.navigate(BookRoute.Reviews.createRoute(currentBookId)) },
                onNavigateToBorrow = { navController.navigate(BookRoute.ConfirmBorrow.createRoute(currentBookId)) }
            )
        }
        composable(BookRoute.Reviews.route, listOf(navArgument("bookId") { type = NavType.StringType })) {
            BookReviewsScreen(onBack = { navController.popBackStack() })
        }
        composable(BookRoute.ConfirmBorrow.route, listOf(navArgument("bookId") { type = NavType.StringType })) {
            BorrowConfirmScreen(
                onSuccess = { orderId -> navController.navigate(BookRoute.Success.createRoute(orderId)) },
                onBookUnavailable = { navController.navigate(BookRoute.Failure.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(BookRoute.Success.route, listOf(navArgument("orderId") { type = NavType.LongType })) {
            BorrowSuccessScreen(
                onViewQrCode = { orderId -> navController.navigate(BookRoute.QRCode.createRoute(orderId)) },
                onBackToHome = { navController.popBackStack(BookRoute.Detail.route, inclusive = true) }
            )
        }
        composable(BookRoute.QRCode.route, listOf(navArgument("orderId") { type = NavType.LongType })) {
            BorrowQrScreen(
                onBack = { navController.popBackStack() },
                onBackToHome = { navController.popBackStack(BookRoute.Detail.route, inclusive = true) }
            )
        }
        composable(BookRoute.Failure.route) {
            BorrowFailureScreen(
                onTryAgain = { navController.popBackStack() },
                onBackToHome = { navController.popBackStack(BookRoute.Detail.route, inclusive = true) }
            )
        }
    }
}
