package com.personal.bookmark_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.personal.bookmark_app.navigation.AppNavigation
import com.personal.bookmark_app.ui.theme.BookmarkappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookmarkappTheme {
                AppNavigation()
            }
        }
    }
}