package com.group_7.library_management.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.group_7.library_management.components.MemberBottomBar
import com.group_7.library_management.components.MemberTopBar
import com.group_7.library_management.navigation.Routes
import com.group_7.library_management.ui.book.BookListScreen
import com.group_7.library_management.ui.borrowing.BorrowRecordListContent
import com.group_7.library_management.ui.borrowing.BorrowTab
import com.group_7.library_management.ui.favorite.FavoriteScreen
import com.group_7.library_management.ui.profile.ProfileContent
import kotlinx.coroutines.launch

@Composable
fun UserScreen(
    userViewModel: UserRootViewModel = viewModel(),
    userNavController: NavHostController = rememberNavController(),
    onLogout: () -> Unit = {}
) {
    val userState by userViewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by userNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.HOME

    val navigateTab: (String) -> Unit = { route ->
        userNavController.navigate(route) {
            popUpTo(route) {
                inclusive = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close() // Đóng menu lại khi bấm Back
        }
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppNavigationDrawer(
                user = userState.currentUser,
                currentRoute = currentRoute,
                onItemClick = { route ->
                    scope.launch { drawerState.close() }
                    navigateTab(route)
                },
                onLogout = {
                    scope.launch { drawerState.close() }
                    onLogout()
                }
            )
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                MemberTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onNotificationClick = { navigateTab(Routes.NOTIFICATIONS) },
                    showNotificationBadge = true
                )
            },
            bottomBar = {
                MemberBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navigateTab(route)
                    }
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = userNavController,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Routes.HOME) {
                    HomeScreen(
                        onBorrowStatusClick = { tabIndex ->
                            userNavController.navigate("${Routes.MY_BOOKS}?tab=$tabIndex") {
                                launchSingleTop = true
                            }
                        },
                        onViewAllClick = { filter ->
                            userNavController.navigate("${Routes.BOOKS}?filter=$filter") {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Routes.NOTIFICATIONS) {
                    NotificationsContent()
                }
                composable(
                    route = "${Routes.BOOKS}?filter={filter}",
                    arguments = listOf(
                        navArgument("filter") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val filter = backStackEntry.arguments?.getString("filter")
                    BookListScreen(initialFilter = filter)
                }
                composable(
                    route = "${Routes.MY_BOOKS}?tab={tab}",
                    arguments = listOf(
                        navArgument("tab") {
                            type = NavType.StringType
                            defaultValue = BorrowTab.ALL.name
                        }
                    )
                ) { backStackEntry ->
                    val tabName = backStackEntry.arguments?.getString("tab") ?: BorrowTab.ALL.name
                    val tab = try { BorrowTab.valueOf(tabName) } catch (e: Exception) { BorrowTab.ALL }
                    BorrowRecordListContent(initialTab = tab)
                }
                composable(Routes.HISTORY) {
                    BorrowRecordListContent()
                }
                composable(Routes.PROFILE) {
                    ProfileContent(onLogoutClick = onLogout)
                }
                composable(Routes.FAVORITE) {
                    FavoriteScreen()
                }
            }
        }
    }
}