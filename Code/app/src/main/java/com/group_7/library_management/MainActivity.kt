package com.group_7.library_management

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.group_7.library_management.components.MemberBottomBar
import com.group_7.library_management.components.dialogs.BorrowConfirmationDialog
import com.group_7.library_management.components.screens.ConfirmCodeScreen
import com.group_7.library_management.navigation.AppNavHost
import com.group_7.library_management.ui.auth.ForgotPasswordScreen
import com.group_7.library_management.ui.auth.LoginScreen
import com.group_7.library_management.ui.auth.RegisterScreen
import com.group_7.library_management.ui.auth.RestPassword
import com.group_7.library_management.ui.borrowing.BorrowingScreen
import com.group_7.library_management.ui.favorite.FavoriteScreen
import com.group_7.library_management.ui.home.HomeScreen
import com.group_7.library_management.ui.home.NotificationsScreen
import com.group_7.library_management.ui.splash.SplashScreen
import com.group_7.library_management.ui.theme.Library_managementTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Library_managementTheme {


//                    SplashScreen()
//                LoginScreen()
//                BorrowConfirmationDialog()
//                RegisterScreen()
//                BorrowingScreen()
//                ForgotPasswordScreen()
                AppNavHost()
//                FavoriteScreen()
//                ConfirmCodeScreen()
//                RestPassword()
//                HomeScreen()
//                MemberBottomBar("home")
            }
        }
    }
}