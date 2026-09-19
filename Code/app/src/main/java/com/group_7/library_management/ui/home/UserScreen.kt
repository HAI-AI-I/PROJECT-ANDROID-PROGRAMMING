package com.group_7.library_management.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.group_7.library_management.components.MemberBottomBar
import com.group_7.library_management.components.MemberTopBar
import com.group_7.library_management.components.AppSnackbarHost
import com.group_7.library_management.navigation.Routes
import com.group_7.library_management.ui.book.BookDetailScreen
import com.group_7.library_management.ui.book.BookReviewsScreen
import com.group_7.library_management.ui.book.BorrowConfirmScreen
import com.group_7.library_management.ui.book.BorrowFailureScreen
import com.group_7.library_management.ui.book.BorrowQrScreen
import com.group_7.library_management.ui.book.BorrowSuccessScreen
import com.group_7.library_management.ui.qrscan.ScanScreen
import com.group_7.library_management.ui.home.MemberQrScreen
import com.group_7.library_management.ui.book.BookListScreen
import com.group_7.library_management.ui.borrowing.BorrowRecordListContent
import com.group_7.library_management.ui.borrowing.BorrowTab
import com.group_7.library_management.ui.borrowing.BorrowOrderDetailScreen
import com.group_7.library_management.ui.favorite.FavoriteScreen
import com.group_7.library_management.ui.profile.EditProfileScreen
import com.group_7.library_management.ui.profile.ProfileScreen
import com.group_7.library_management.ui.support.addSupportNavGraph
import com.group_7.library_management.ui.profile.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun UserScreen(
    userViewModel: UserRootViewModel = hiltViewModel(),
    userNavController: NavHostController = rememberNavController(),
    onLogout: () -> Unit = {}
) {
    val userState by userViewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by userNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.HOME
    val pendingBookFilter = remember { mutableStateOf<String?>(null) }
    val pendingBorrowTab = remember { mutableStateOf(BorrowTab.ALL) }
    val borrowScreenResetKey = remember { mutableIntStateOf(0) }

    val navigateTab: (String) -> Unit = { route ->
        userNavController.navigate(route) {
            popUpTo(userNavController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val mainTabs=listOf(
        Routes.HOME,
        Routes.BOOKS,
        Routes.BORROW,
        Routes.PROFILE,
        Routes.NOTIFICATIONS,
        Routes.FAVORITE,
        // Routes.SCAN_QR
    )
    val shouldShowBar=currentRoute in mainTabs

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
                unreadNotificationCount = userState.unreadNotificationCount,
                isBiometricEnabled = userState.isBiometricEnabled,
                onToggleBiometric = { enabled ->
                    userViewModel.toggleBiometricSetting(enabled)
                },
                currentRoute = currentRoute,
                onItemClick = { route ->
                    scope.launch { drawerState.close() }
                    if (route == Routes.BORROW) {
                        pendingBorrowTab.value = BorrowTab.ALL
                        borrowScreenResetKey.intValue++
                    }
                    navigateTab(route)
                },
                onLogout = {
                    scope.launch { drawerState.close() }
                    userViewModel.logout(onLogout)
                }
            )
        }
    ) {
        Scaffold(
            snackbarHost = { AppSnackbarHost(userViewModel.snackbarController) },
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                if(shouldShowBar){
                    MemberTopBar(
                        onLeftClick = { scope.launch { drawerState.open() } },
                        onRightClick = { navigateTab(Routes.NOTIFICATIONS) },
                        showNotificationBadge = userState.unreadNotificationCount > 0
                    )
                }
            },
            bottomBar = {
                if(shouldShowBar){
                    MemberBottomBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            if (route == Routes.HOME && currentRoute == Routes.FAVORITE) {
                                val returnedHome = userNavController.popBackStack(
                                    route = Routes.HOME,
                                    inclusive = false
                                )
                                if (!returnedHome) navigateTab(Routes.HOME)
                            } else {
                                navigateTab(route)
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = userNavController,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Routes.HOME) {
                    HomeScreen(
                        onBookClick = { book -> userNavController.navigate(Routes.bookDetail(book.id)) },
                        onViewAllClick = { filter ->
                            pendingBookFilter.value = filter
                            navigateTab(Routes.BOOKS)
                        },
                        onOpenQRClick = { userNavController.navigate(Routes.MEMBER_QR) },
                        onNavigateToBorrowTab = { tabKey ->
                            pendingBorrowTab.value = when (tabKey) {
                                "pending" -> BorrowTab.PENDING
                                "borrowing" -> BorrowTab.BORROWING
                                "due_soon" -> BorrowTab.DUE_SOON
                                "overdue" -> BorrowTab.OVERDUE
                                "returned" -> BorrowTab.RETURNED
                                "history" -> BorrowTab.ALL
                                else -> BorrowTab.ALL
                            }
                            navigateTab(Routes.BORROW)
                        },
                        onNavigateToFavorite = {
                            navigateTab(Routes.FAVORITE)
                        }
                    )
                }
                composable(
                    route = Routes.BOOK_DETAIL,
                    arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                    BookDetailScreen (
                        onBack={userNavController.popBackStack()},
                        onNavigateToReviews = {
                            userNavController.navigate(Routes.bookReviews(bookId))
                        },
                        onNavigateToBorrow = {
                            userNavController.navigate(Routes.bookBorrowConfirm(bookId))
                        },
                        onRelatedBookClick = { book ->
                            userNavController.navigate(Routes.bookDetail(book.id))
                        }
                    )
                }
                composable(
                    route = Routes.BOOK_REVIEWS,
                    arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                ) {
                    BookReviewsScreen(onBack = { userNavController.popBackStack() })
                }
                composable(
                    route = Routes.BOOK_BORROW_CONFIRM,
                    arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                ) {
                    BorrowConfirmScreen(
                        onSuccess = { orderId ->
                            userNavController.navigate(Routes.borrowSuccess(orderId)) {
                                popUpTo(Routes.BOOK_BORROW_CONFIRM) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onBookUnavailable = {
                            userNavController.navigate(Routes.BORROW_FAILURE) {
                                popUpTo(Routes.BOOK_BORROW_CONFIRM) { inclusive = false }
                            }
                        },
                        onBack = { userNavController.popBackStack() }
                    )
                }
                composable(
                    route = Routes.BORROW_SUCCESS,
                    arguments = listOf(navArgument("orderId") { type = NavType.LongType })
                ) {
                    BorrowSuccessScreen(
                        onViewQrCode = { orderId -> userNavController.navigate(Routes.borrowQr(orderId)) },
                        onBackToHome = {
                            userNavController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(
                    route = Routes.BORROW_QR,
                    arguments = listOf(navArgument("orderId") { type = NavType.LongType })
                ) {
                    BorrowQrScreen(
                        onBack = { userNavController.popBackStack() },
                        onBackToHome = {
                            userNavController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Routes.BORROW_FAILURE) {
                    BorrowFailureScreen(
                        onTryAgain = { userNavController.popBackStack() },
                        onBackToHome = {
                            userNavController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Routes.NOTIFICATIONS) {
                    NotificationsContent()
                }
                composable(Routes.BOOKS) {
                    BookListScreen(
                        initialFilter = pendingBookFilter.value,
                        onInitialFilterApplied = { pendingBookFilter.value = null },
                        onBookClick = { book -> userNavController.navigate(Routes.bookDetail(book.id)) }
                    )
                }
                composable(Routes.BORROW) {
                    androidx.compose.runtime.key(borrowScreenResetKey.intValue) {
                        BorrowRecordListContent(
                            initialTab = pendingBorrowTab.value,
                            onOrderClick = { orderId ->
                                userNavController.navigate(Routes.borrowOrderDetail(orderId))
                            }
                        )
                    }
                }
                composable(
                    route = Routes.BORROW_ORDER_DETAIL,
                    arguments = listOf(navArgument("orderId") { type = NavType.LongType })
                ) {
                    BorrowOrderDetailScreen(
                        onBack = { userNavController.popBackStack() },
                        onViewQrCode = { orderId ->
                            userNavController.navigate(Routes.borrowQr(orderId))
                        },
                        onBookClick = { bookId ->
                            userNavController.navigate(Routes.bookDetail(bookId.toString()))
                        }
                    )
                }
                composable(Routes.PROFILE) { backStackEntry ->
                    val profileViewModel: ProfileViewModel = hiltViewModel(backStackEntry)
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onEditProfileClick = { userNavController.navigate(Routes.EDIT_PROFILE) },
                        onLogoutClick = { userViewModel.logout(onLogout) }
                    )
                }
                composable(Routes.EDIT_PROFILE) {
                    val profileBackStackEntry = remember(userNavController) {
                        userNavController.getBackStackEntry(Routes.PROFILE)
                    }
                    val profileViewModel: ProfileViewModel = hiltViewModel(profileBackStackEntry)
                    EditProfileScreen(
                        viewModel = profileViewModel,
                        onNavigateBack = { userNavController.popBackStack() },
                        onSaved = { userNavController.popBackStack() }
                    )
                }
                composable(Routes.FAVORITE) {
                    FavoriteScreen(
                        onBookClick = { book ->
                            userNavController.navigate(Routes.bookDetail(book.id))
                        }
                    )
                }
                composable(Routes.SCAN_QR) {
                    ScanScreen(
                        onBack = { userNavController.popBackStack() }
                    )
                }
                composable(Routes.MEMBER_QR) {
                    MemberQrScreen(onBack = { userNavController.popBackStack() })
                }
                addSupportNavGraph(userNavController)
            }
        }
    }
}
