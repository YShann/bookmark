package com.personal.bookmark_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.personal.bookmark_app.ui.screen.addbookmark.AddBookmarkScreen
import com.personal.bookmark_app.ui.screen.DailyPicksScreen
import com.personal.bookmark_app.ui.screen.folderdetail.FolderDetailScreen
import com.personal.bookmark_app.ui.screen.forgotpassword.ForgotPasswordScreen
import com.personal.bookmark_app.ui.screen.HomeScreen
import com.personal.bookmark_app.ui.screen.SearchBookmarkScreen
import com.personal.bookmark_app.ui.screen.login.LoginScreen
import com.personal.bookmark_app.ui.screen.register.RegisterScreen
import com.personal.bookmark_app.ui.screen.verifyemail.VerifyEmailScreen
import com.personal.bookmark_app.ui.theme.BookmarkappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookmarkappTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
//                            onNavigateToSearch = { navController.navigate("search") }
                        )
                    }
                    composable("dailyPicks") {
                        DailyPicksScreen(
//                            onNavigateToSearch = { navController.navigate("search") }
                        )
                    }
                    composable("folderDetail") {
                        FolderDetailScreen(
//                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("search") {
                        SearchBookmarkScreen(
//                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("addBookmark") {
                        AddBookmarkScreen(
//                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("login") {
                        LoginScreen(
//                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
//                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("verifyEmail") {
                        VerifyEmailScreen(
//                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("forgotPassword") {
                        ForgotPasswordScreen(
//                            onBack = { navController.popBackStack() }
                        )
                    }

                }
            }
        }
    }
}