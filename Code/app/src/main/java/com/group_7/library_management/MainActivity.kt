package com.group_7.library_management

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.group_7.library_management.ui.book.BookScreen
import com.group_7.library_management.ui.theme.Library_managementTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Library_managementTheme {
                BookScreen()
            }
        }
    }
}