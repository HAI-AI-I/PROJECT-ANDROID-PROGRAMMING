package com.group_7.library_management.ui.support

import android.net.Uri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.group_7.library_management.navigation.Routes

fun NavGraphBuilder.addSupportNavGraph(navController: NavController) {
    composable(Routes.HELP) {
        SupportScreen(
            onBack = { navController.popBackStack() },
            onNavigateToTopic = { topic ->
                navController.navigate("faq/${Uri.encode(topic)}")
            },
            onNavigateToContact = { navController.navigate(Routes.CONTACT_LIBRARIAN) },
            onNavigateToMyRequests = { navController.navigate(Routes.MY_SUPPORT_REQUESTS) }
        )
    }
    composable(
        route = "faq/{topic}?selectedId={selectedId}",
        arguments = listOf(
            navArgument("topic") { type = NavType.StringType },
            navArgument("selectedId") { type = NavType.StringType; nullable = true; defaultValue = null }
        )
    ) { backStackEntry ->
        val topic = backStackEntry.arguments?.getString("topic") ?: "Mượn sách"
        val selectedId = backStackEntry.arguments?.getString("selectedId")
        FAQScreen(
            topic = topic,
            initialSelectedId = selectedId,
            onBack = { navController.popBackStack() }
        )
    }
    composable(Routes.CREATE_SUPPORT_REQUEST) {
        val supportViewModel: SupportViewModel = hiltViewModel()
        CreateSupportRequestScreen(
            viewModel = supportViewModel,
            onBack = { navController.popBackStack() },
            onSubmitSuccess = { navController.navigate(Routes.MY_SUPPORT_REQUESTS) }
        )
    }
    composable(Routes.MY_SUPPORT_REQUESTS) {
        val supportViewModel: SupportViewModel = hiltViewModel()
        MySupportRequestsScreen(
            viewModel = supportViewModel,
            onBack = { navController.popBackStack() }
        )
    }
    composable(Routes.CONTACT_LIBRARIAN) {
        ContactLibrarianScreen(
            onBack = { navController.popBackStack() },
            onNavigateToCreateRequest = { navController.navigate(Routes.CREATE_SUPPORT_REQUEST) }
        )
    }
}
