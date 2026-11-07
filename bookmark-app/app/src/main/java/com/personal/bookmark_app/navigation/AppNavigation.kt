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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.personal.bookmark_app.data.local.UserPreferencesRepository
import com.personal.bookmark_app.data.remote.RetrofitClient
import com.personal.bookmark_app.data.repository.ApiAuthRepository
import com.personal.bookmark_app.data.repository.AuthRepository
import com.personal.bookmark_app.ui.mockdata.FolderMockData
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

    // 觀察目前所在的 Navigation 頁面
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    // 防止返回操作被連續執行
    var isBackNavigationInProgress by remember {
        mutableStateOf(false)
    }

    // 當頁面成功切換後，解除返回鎖定
    LaunchedEffect(currentBackStackEntry) {
        isBackNavigationInProgress = false
    }

    // ------------------------------------------------
    // 建立 AuthViewModel
    // ------------------------------------------------

    val authRepository: AuthRepository = remember {
        ApiAuthRepository(
            api = RetrofitClient.authApi,
            preferences = UserPreferencesRepository(context.applicationContext)
        )
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

    // 安全返回上一頁，避免快速連點造成連續返回
    fun navigateBackSafely() {

        // 如果上一個返回操作還沒完成，就不再執行
        if (isBackNavigationInProgress) return

        // 如果沒有上一頁，就不執行返回
        if (navController.previousBackStackEntry == null) return

        // 鎖定返回操作
        isBackNavigationInProgress = true

        // 執行返回
        val isPopped = navController.popBackStack()

        // 如果返回失敗，解除鎖定
        if (!isPopped) {
            isBackNavigationInProgress = false
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
                onPasswordVisibilityChange = authViewModel::onLoginPasswordVisibilityChange,
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
                onEmailChange = authViewModel::onRegisterEmailChange,
                onPasswordChange = authViewModel::onRegisterPasswordChange,
                onConfirmPasswordChange = authViewModel::onConfirmPasswordChange,
                onPasswordVisibilityChange = authViewModel::onRegisterPasswordVisibilityChange,
                onConfirmPasswordVisibilityChange = authViewModel::onConfirmPasswordVisibilityChange,
                onRegisterClick = authViewModel::register,
                onGoogleRegisterClick = {
                    // 暫時模擬 Google 註冊成功
                    navigateToHome()
                },
                onBackClick = { navigateBackSafely() },
                onLoginClick = { navigateBackSafely() }
            )
        }

        // Verify Email
        composable(Screen.VERIFY_EMAIL) {

            VerifyEmailScreen(
                uiState = verifyEmailUiState,
                onCodeChange = authViewModel::onVerificationCodeChange,
                onVerifyClick = authViewModel::verifyEmail,
                onResendClick = authViewModel::resendVerificationCode
            )
        }

        // Forgot Password
        composable(Screen.FORGOT_PASSWORD) {

            ForgotPasswordScreen(
                uiState = forgotPasswordUiState,
                onEmailChange = authViewModel::onForgotPasswordEmailChange,
                onBackClick = { navigateBackSafely() },
                onSendResetLinkClick = authViewModel::sendPasswordResetLink
            )
        }

        // ============================================
        // Main
        // ============================================

        // Home
        composable(Screen.HOME) {

            HomeScreen(
                onSearchClick = { navController.navigate(Screen.SEARCH_BOOKMARK) },
                onAddBookmarkClick = { navController.navigate(Screen.ADD_BOOKMARK) },
                onDailyPicksClick = { navController.navigate(Screen.DAILY_PICKS) },
                onEditFoldersClick = {
                    // 編輯收藏夾畫面尚未設計，暫時不處理
                },
                onFolderClick = { folder ->
                    navController.navigate(Screen.folderBookmark(folder.id))
                }
            )
        }

        // Daily Picks
        composable(Screen.DAILY_PICKS) {

            DailyPicksScreen(
                onBackClick = { navigateBackSafely() },
                onRefreshClick = {
                    // 第四階段再接上每日推薦邏輯
                }
            )
        }

        // Search Bookmark
        composable(Screen.SEARCH_BOOKMARK) {

            SearchBookmarkScreen(
                onBackClick = { navigateBackSafely() }
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
                    navigateBackSafely()
                },
                onBackClick = { navigateBackSafely() }
            )
        }

        // ============================================
        // Folder Bookmark
        // ============================================

        composable(
            route = Screen.FOLDER_BOOKMARK,
            arguments = listOf(
                navArgument("folderId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val folderId = backStackEntry.arguments?.getLong("folderId") ?: 0L
            val folderName = FolderMockData
                .firstOrNull { it.id == folderId }
                ?.title
                ?: "資料夾 $folderId"

            FolderBookmarkScreen(
                folderName = folderName,
                onBackClick = { navigateBackSafely() },
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
