package com.group_7.library_management

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import com.group_7.library_management.navigation.AppNavHost
import com.group_7.library_management.ui.theme.Library_managementTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Library_managementTheme {
                AppNavHost()
            }
        }
    }
}
