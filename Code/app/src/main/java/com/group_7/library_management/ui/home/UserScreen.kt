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
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import com.group_7.library_management.navigation.Routes
import com.group_7.library_management.ui.book.BookDetailScreen
import com.group_7.library_management.ui.book.BookViewModel
import com.group_7.library_management.ui.book.BorrowConfirmScreen
import com.group_7.library_management.ui.book.BorrowSuccessScreen
import com.group_7.library_management.ui.qrscan.ScanScreen
import com.group_7.library_management.ui.book.BookListScreen
import com.group_7.library_management.ui.borrowing.BorrowRecordListContent
import com.group_7.library_management.ui.favorite.FavoriteScreen
import com.group_7.library_management.ui.profile.ChangePasswordScreen
import com.group_7.library_management.ui.profile.EditProfileScreen
import com.group_7.library_management.ui.profile.ProfileScreen
import com.group_7.library_management.ui.support.addSupportNavGraph
import com.group_7.library_management.ui.profile.ProfileViewModel
import com.group_7.library_management.models.Book
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
    val pendingBookFilter = remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    var selectedBook by remember { mutableStateOf<Book?>(null) }

    val openBookDetail: (Book) -> Unit = { book ->
        selectedBook = book
        userNavController.navigate(Routes.BOOK_DETAIL)
    }

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
        Routes.SCAN_QR
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
                            navigateTab(route)
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
                        onBookClick = openBookDetail,
                        onViewAllClick = { filter ->
                            pendingBookFilter.value = filter
                            navigateTab(Routes.BOOKS)
                        },
                        onOpenQRClick = { userNavController.navigate(Routes.SCAN_QR) },
                        onNavigateToBorrowTab = { tabKey ->
                            userNavController.navigate(Routes.BORROW)
                        },
                        onNavigateToFavorite = {
                            userNavController.navigate(Routes.FAVORITE)
                        }
                    )
                }
                composable(Routes.BOOK_DETAIL) {
                    BookDetailScreen (
                        book = selectedBook,
                        onBack={userNavController.popBackStack()},
                        onNavigateToBorrow = {
                            selectedBook?.let { book ->
                                userNavController.navigate(Routes.confirmBorrow(book.id))
                            }
                        }
                    )
                }
                composable(
                    route = Routes.CONFIRM_BORROW,
                    arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val bookId = requireNotNull(backStackEntry.arguments?.getString("bookId"))
                    val bookViewModel: BookViewModel = hiltViewModel()
                    BorrowConfirmScreen(
                        bookId = bookId,
                        viewModel = bookViewModel,
                        onSuccess = { transactionId ->
                            userNavController.navigate(Routes.borrowSuccess(transactionId)) {
                                popUpTo(Routes.CONFIRM_BORROW) { inclusive = true }
                            }
                        },
                        onBack = { userNavController.popBackStack() }
                    )
                }
                composable(
                    route = Routes.BORROW_SUCCESS,
                    arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
                ) { backStackEntry ->
                    BorrowSuccessScreen(
                        transactionId = requireNotNull(
                            backStackEntry.arguments?.getString("transactionId")
                        ),
                        onBackToHome = {
                            selectedBook = null
                            userNavController.navigate(Routes.HOME) {
                                popUpTo(userNavController.graph.findStartDestination().id)
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
                        onBookClick = openBookDetail
                    )
                }
                composable(Routes.BORROW) {
                    BorrowRecordListContent()
                }
                composable(Routes.HISTORY) {
                    BorrowRecordListContent()
                }
                composable(Routes.PROFILE) {
                    ProfileScreen(
                        onEditProfileClick = { userNavController.navigate(Routes.EDIT_PROFILE) },
                        onChangePasswordClick = { userNavController.navigate(Routes.CHANGE_PASSWORD) },
                        onLogoutClick = onLogout
                    )
                }
                composable(Routes.EDIT_PROFILE) {
                    EditProfileScreen(
                        onNavigateBack = { userNavController.popBackStack() },
                        onSaved = { userNavController.popBackStack() }
                    )
                }
                composable(Routes.CHANGE_PASSWORD) {
                    ChangePasswordScreen(
                        onNavigateBack = { userNavController.popBackStack() },
                        onSaveSuccess = { userNavController.popBackStack() }
                    )
                }
                composable(Routes.FAVORITE) {
                    FavoriteScreen()
                }
                composable(Routes.SCAN_QR) {
                    ScanScreen(
                        onBack = { userNavController.popBackStack() }
                    )
                }
                addSupportNavGraph(userNavController)
            }
        }
    }
}
