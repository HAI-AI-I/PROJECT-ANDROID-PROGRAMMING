package com.group_7.library_management.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.group_7.library_management.data.local.preferences.CheckLogin
import com.group_7.library_management.data.remote.dto.RegistrationVerificationMethod
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
                    if (checkLogin.isLogin()) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    } else {
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
                onRegisterClick = { registrationId, method ->
                    navController.navigate(
                        Routes.confirmRegistration(registrationId, method.name)
                    )
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
                onCodeSent = { requestId, method ->
                    navController.navigate(
                        Routes.passwordResetCode(requestId, method.name, "forgot")
                    )
                }
            )
        }
        composable(
            route = Routes.PASSWORD_RESET_CODE,
            arguments = listOf(
                navArgument("requestId") { type = NavType.StringType },
                navArgument("verificationMethod") { type = NavType.StringType },
                navArgument("flow") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId").orEmpty()
            val method = runCatching {
                RegistrationVerificationMethod.valueOf(
                    backStackEntry.arguments?.getString("verificationMethod").orEmpty()
                )
            }.getOrDefault(RegistrationVerificationMethod.EMAIL)
            val flow = backStackEntry.arguments?.getString("flow") ?: "forgot"
            ConfirmCodeResetAuthScreen(
                requestId = requestId,
                method = method,
                onNavigateBack = { navController.popBackStack() },
                onVerified = { resetToken ->
                    navController.navigate(Routes.passwordReset(resetToken, flow))
                }
            )
        }
        composable(route = Routes.CONFIRM_CODE_REGIS_AUTH) { backStackEntry ->
            val registrationId = backStackEntry.arguments
                ?.getString("registrationId")
                .orEmpty()
            val verificationMethod = runCatching {
                RegistrationVerificationMethod.valueOf(
                    backStackEntry.arguments
                        ?.getString("verificationMethod")
                        .orEmpty()
                )
            }.getOrDefault(RegistrationVerificationMethod.EMAIL)
            ConfirmCodeRegisAuthScreen(
                registrationId = registrationId,
                method = verificationMethod,
                onNavigateBack = {navController.popBackStack()},
                onSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = Routes.PASSWORD_RESET,
            arguments = listOf(
                navArgument("resetToken") { type = NavType.StringType },
                navArgument("flow") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val resetToken = backStackEntry.arguments?.getString("resetToken").orEmpty()
            RestPassword(
                resetToken = resetToken,
                onNavigateBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(route= Routes.HOME) {
            UserScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
