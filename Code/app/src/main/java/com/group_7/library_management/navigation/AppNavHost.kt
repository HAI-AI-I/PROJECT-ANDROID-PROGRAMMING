package com.group_7.library_management.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.group_7.library_management.ui.auth.ConfirmCodeRegisAuthScreen
import com.group_7.library_management.ui.auth.ConfirmCodeResetAuthScreen
import com.group_7.library_management.ui.auth.ForgotPasswordScreen
import com.group_7.library_management.ui.auth.LoginScreen
import com.group_7.library_management.ui.auth.RegisterScreen
import com.group_7.library_management.ui.auth.RestPassword


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(route = Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                }
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
                onSubmit = {navController.navigate(Routes.LOGIN)}
            )
        }
        composable ( route= Routes.RESET_PASSWORD_AUTH ){
            RestPassword(
                onNavigateBack = {navController.popBackStack()},
                onSubmit = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }
    }
}