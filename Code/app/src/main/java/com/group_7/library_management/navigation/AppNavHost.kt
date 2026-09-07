package com.group_7.library_management.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.ui.auth.ConfirmCodeRegisAuthScreen
import com.group_7.library_management.ui.auth.ConfirmCodeResetAuthScreen
import com.group_7.library_management.ui.auth.ForgotPasswordScreen
import com.group_7.library_management.ui.auth.LoginScreen
import com.group_7.library_management.ui.auth.RegisterScreen
import com.group_7.library_management.ui.auth.RestPassword
import com.group_7.library_management.ui.home.UserScreen
import com.group_7.library_management.ui.splash.SplashScreen


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val context= LocalContext.current
    val
            checkLogin= remember { CheckLogin(context) }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(route = Routes.SPLASH) {
            SplashScreen(
                onNext = {
                    if(checkLogin.isLogin()){
                        navController.navigate(Routes.HOME){
                            popUpTo (Routes.SPLASH){inclusive=true}
                        }
                    }
                    else{
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(route = Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                },
                onLoginSuccess = { userId ->
                    checkLogin.saveLogin(userId.toString())
                    navController.navigate(Routes.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
        composable(route = Routes.REGISTER) {
            RegisterScreen(
                onRegisterClick = {
                    navController.navigate(Routes.CONFIRM_CODE_REGIS_AUTH)
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }


        composable(route = Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSubmit = {
                    navController.navigate(Routes.CONFIRM_CODE_RESET_AUTH)
                }
            )
        }
        composable(route= Routes.CONFIRM_CODE_RESET_AUTH){
            ConfirmCodeResetAuthScreen(
                onNavigateBack={
                    navController.popBackStack()
                },
                onSubmit={
                    navController.navigate(Routes.RESET_PASSWORD_AUTH)
                }
            )
        }
        composable(route= Routes.CONFIRM_CODE_REGIS_AUTH){
            ConfirmCodeRegisAuthScreen(
                onNavigateBack = {navController.popBackStack()},
                onSubmit = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }
        composable ( route= Routes.RESET_PASSWORD_AUTH ){
            RestPassword(
                onNavigateBack = {navController.popBackStack()},
                onSubmit = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.RESET_PASSWORD_AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(route= Routes.HOME) {
            UserScreen(
                onLogout = {
                    checkLogin.clearLogin()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
