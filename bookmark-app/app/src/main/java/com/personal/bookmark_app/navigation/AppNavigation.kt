package com.personal.bookmark_app.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.personal.bookmark_app.data.repository.FakeAuthRepository
import com.personal.bookmark_app.ui.screen.DailyPicksScreen
import com.personal.bookmark_app.ui.screen.SearchBookmarkScreen
import com.personal.bookmark_app.ui.screen.addbookmark.AddBookmarkScreen
import com.personal.bookmark_app.ui.screen.addbookmark.AddBookmarkUiState
import com.personal.bookmark_app.ui.screen.folderbookmark.FolderBookmarkScreen
import com.personal.bookmark_app.ui.screen.forgotpassword.ForgotPasswordScreen
import com.personal.bookmark_app.ui.screen.home.HomeScreen
import com.personal.bookmark_app.ui.screen.login.LoginScreen
import com.personal.bookmark_app.ui.screen.register.RegisterScreen
import com.personal.bookmark_app.ui.screen.verifyemail.VerifyEmailScreen
import com.personal.bookmark_app.ui.viewmodel.AuthEvent
import com.personal.bookmark_app.ui.viewmodel.AuthViewModel
import com.personal.bookmark_app.ui.viewmodel.AuthViewModelFactory

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current

    // ------------------------------------------------
    // 建立 AuthViewModel
    // ------------------------------------------------

    val authRepository = remember {
        FakeAuthRepository()
    }

    val authViewModelFactory = remember {
        AuthViewModelFactory(authRepository)
    }

    val authViewModel: AuthViewModel = viewModel(
        factory = authViewModelFactory
    )

    // ------------------------------------------------
    // 收集 AuthViewModel 的 UiState
    // ------------------------------------------------

    val loginUiState by authViewModel.loginUiState
        .collectAsStateWithLifecycle()

    val registerUiState by authViewModel.registerUiState
        .collectAsStateWithLifecycle()

    val verifyEmailUiState by authViewModel.verifyEmailUiState
        .collectAsStateWithLifecycle()

    val forgotPasswordUiState by authViewModel.forgotPasswordUiState
        .collectAsStateWithLifecycle()

    // ------------------------------------------------
    // 其他頁面暫時保留原本的狀態管理
    // ------------------------------------------------

    var addBookmarkUiState by remember {
        mutableStateOf(AddBookmarkUiState())
    }

    // ------------------------------------------------
    // 共用導航操作
    // ------------------------------------------------

    fun navigateToHome() {
        navController.navigate(Screen.HOME) {
            popUpTo(Screen.LOGIN) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    // ------------------------------------------------
    // 接收 AuthViewModel 的一次性事件
    // ------------------------------------------------

    LaunchedEffect(authViewModel) {
        authViewModel.authEvent.collect { event ->

            when (event) {

                // 登入或信箱驗證成功
                AuthEvent.Authenticated -> {
                    navigateToHome()
                }

                // 註冊成功，前往信箱驗證
                AuthEvent.NavigateToVerifyEmail -> {
                    authViewModel.resetRegisterState()

                    navController.navigate(Screen.VERIFY_EMAIL)
                }

                // 忘記密碼信件寄送成功
                AuthEvent.PasswordResetLinkSent -> {
                    Toast.makeText(
                        context,
                        "重設密碼連結已寄出，請查看信箱",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // 顯示錯誤或提示訊息
                is AuthEvent.ShowMessage -> {
                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // ------------------------------------------------
    // Navigation
    // ------------------------------------------------

    NavHost(
        navController = navController,
        startDestination = Screen.LOGIN
    ) {

        // ============================================
        // Auth
        // ============================================

        // Login
        composable(Screen.LOGIN) {

            LoginScreen(
                uiState = loginUiState,

                onEmailChange = authViewModel::onLoginEmailChange,

                onPasswordChange = authViewModel::onLoginPasswordChange,

                onRememberMeChange = authViewModel::onRememberMeChange,

                onPasswordVisibilityChange =
                    authViewModel::onLoginPasswordVisibilityChange,

                onLoginClick = authViewModel::login,

                onGoogleLoginClick = {
                    // 暫時模擬 Google 登入成功
                    navigateToHome()
                },

                onForgotPasswordClick = {
                    navController.navigate(Screen.FORGOT_PASSWORD)
                },

                onRegisterClick = {
                    navController.navigate(Screen.REGISTER)
                }
            )
        }

        // Register
        composable(Screen.REGISTER) {

            RegisterScreen(
                uiState = registerUiState,

                onNicknameChange = authViewModel::onNicknameChange,

                onEmailChange = authViewModel::onRegisterEmailChange,

                onPasswordChange = authViewModel::onRegisterPasswordChange,

                onConfirmPasswordChange =
                    authViewModel::onConfirmPasswordChange,

                onPasswordVisibilityChange =
                    authViewModel::onRegisterPasswordVisibilityChange,

                onConfirmPasswordVisibilityChange =
                    authViewModel::onConfirmPasswordVisibilityChange,

                onTermsAcceptedChange =
                    authViewModel::onTermsAcceptedChange,

                onRegisterClick = authViewModel::register,

                onGoogleRegisterClick = {
                    // 暫時模擬 Google 註冊成功
                    navigateToHome()
                },

                onBackClick = {
                    navController.popBackStack()
                },

                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // Verify Email
        composable(Screen.VERIFY_EMAIL) {

            VerifyEmailScreen(
                uiState = verifyEmailUiState,

                onCodeChange =
                    authViewModel::onVerificationCodeChange,

                onVerifyClick = authViewModel::verifyEmail,

                onResendClick =
                    authViewModel::resendVerificationCode
            )
        }

        // Forgot Password
        composable(Screen.FORGOT_PASSWORD) {

            ForgotPasswordScreen(
                uiState = forgotPasswordUiState,

                onEmailChange =
                    authViewModel::onForgotPasswordEmailChange,

                onBackClick = {
                    navController.popBackStack()
                },

                onSendResetLinkClick =
                    authViewModel::sendPasswordResetLink
            )
        }

        // ============================================
        // Main
        // ============================================

        // Home
        composable(Screen.HOME) {

            HomeScreen(
                onSearchClick = {
                    navController.navigate(Screen.SEARCH_BOOKMARK)
                },

                onAddBookmarkClick = {
                    navController.navigate(Screen.ADD_BOOKMARK)
                },

                onDailyPicksClick = {
                    navController.navigate(Screen.DAILY_PICKS)
                },

                onEditFoldersClick = {
                    // 編輯收藏夾畫面尚未設計，暫時不處理
                },

                onFolderClick = { folder ->
                    navController.navigate(
                        Screen.folderBookmark(folder.title)
                    )
                }
            )
        }

        // Daily Picks
        composable(Screen.DAILY_PICKS) {

            DailyPicksScreen(
                onBackClick = {
                    navController.popBackStack()
                },

                onRefreshClick = {
                    // 第四階段再接上每日推薦邏輯
                }
            )
        }

        // Search Bookmark
        composable(Screen.SEARCH_BOOKMARK) {

            SearchBookmarkScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Add Bookmark
        composable(Screen.ADD_BOOKMARK) {

            AddBookmarkScreen(
                uiState = addBookmarkUiState,
                onTitleChange = { title ->
                    addBookmarkUiState = addBookmarkUiState.copy(
                        title = title
                    )
                },
                onNoteChange = { note ->
                    addBookmarkUiState = addBookmarkUiState.copy(
                        note = note
                    )
                },
                onSourceChange = { source ->
                    addBookmarkUiState = addBookmarkUiState.copy(
                        selectedSource = source
                    )
                },

//                onFolderClick = {
//                    addBookmarkUiState = addBookmarkUiState.copy(
//                        isFolderSelectorVisible = true
//                    )
//                },
                onFolderSelected = { folderId ->
                    addBookmarkUiState = addBookmarkUiState.copy(
                        selectedFolderId = folderId
                    )
                },
                onCreateFolderClick = { folderName ->
                    // TODO: 後續接上 ViewModel 與後端 API
                },

                onTagClick = { tagId ->
                    val currentTags = addBookmarkUiState.selectedTagIds

                    addBookmarkUiState = addBookmarkUiState.copy(
                        selectedTagIds = if (tagId in currentTags) {
                            currentTags - tagId
                        } else if (currentTags.size < 5) {
                            currentTags + tagId
                        } else {
                            currentTags
                        }
                    )
                },
                onDailyPicksChange = { isAdded ->
                    addBookmarkUiState = addBookmarkUiState.copy(
                        isAddedToDailyPicks = isAdded
                    )
                },
                onReparseClick = {
                    // 第三階段再接上網址解析邏輯
                },
                onClearTitleClick = {
                    addBookmarkUiState = addBookmarkUiState.copy(
                        title = ""
                    )
                },
                onSaveClick = {
                    // 暫時模擬儲存成功
                    addBookmarkUiState = AddBookmarkUiState()
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // ============================================
        // Folder Bookmark
        // ============================================

        composable(
            route = Screen.FOLDER_BOOKMARK,
            arguments = listOf(
                navArgument("folderName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val folderName = backStackEntry.arguments
                ?.getString("folderName")
                .orEmpty()

            FolderBookmarkScreen(
                folderName = folderName,

                onBackClick = {
                    navController.popBackStack()
                },

                onBatchManageClick = {
                    // 畫面目前自行管理批量模式
                },

                onBookmarkClick = { bookmarkId ->
                    // 收藏詳情頁尚未設計，暫時不處理
                }
            )
        }
    }
}