package com.personal.bookmark_app.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.personal.bookmark_app.data.remote.api.BookmarkApi
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkSourceResponse
import com.personal.bookmark_app.data.repository.auth.ApiAuthRepository
import com.personal.bookmark_app.data.repository.auth.AuthRepository
import com.personal.bookmark_app.ui.screen.SearchBookmarkScreen
import com.personal.bookmark_app.ui.screen.addbookmark.AddBookmarkScreen
import com.personal.bookmark_app.ui.screen.addbookmark.BackgroundBookmarkImport
import com.personal.bookmark_app.ui.screen.addbookmark.BookmarkBrowserImportContract
import com.personal.bookmark_app.ui.screen.dailypicks.DailyPicksScreen
import com.personal.bookmark_app.ui.screen.folderbookmark.FolderBookmarkScreen
import com.personal.bookmark_app.ui.screen.forgotpassword.ForgotPasswordScreen
import com.personal.bookmark_app.ui.screen.forgotpassword.PasswordResetStep
import com.personal.bookmark_app.ui.screen.home.HomeScreen
import com.personal.bookmark_app.ui.screen.login.LoginScreen
import com.personal.bookmark_app.ui.screen.register.RegisterScreen
import com.personal.bookmark_app.ui.screen.resetpassword.ResetPasswordScreen
import com.personal.bookmark_app.ui.screen.verifyemail.VerifyEmailScreen
import com.personal.bookmark_app.ui.screen.verifyemail.VerifyEmailUiState
import com.personal.bookmark_app.ui.viewmodel.AuthEvent
import com.personal.bookmark_app.ui.viewmodel.AuthViewModel
import com.personal.bookmark_app.ui.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.CancellationException

@Composable
fun AppNavigation(
    sharedBookmark: com.personal.bookmark_app.SharedBookmark? = null,
    onShareHandled: () -> Unit = {}
) {

    val navController = rememberNavController()
    val context = LocalContext.current

    // 觀察目前所在的 Navigation 頁面
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val bookmarkApi = remember(context) {
        RetrofitClient.authenticated(context.applicationContext).create(BookmarkApi::class.java)
    }
    var bookmarkSources by remember { mutableStateOf(emptyList<BookmarkSourceResponse>()) }
    LaunchedEffect(currentBackStackEntry?.destination?.route) {
        when (currentBackStackEntry?.destination?.route) {
            Screen.SEARCH_BOOKMARK, Screen.FOLDER_BOOKMARK, Screen.DAILY_PICKS -> {
                try {
                    bookmarkSources = bookmarkApi.getBookmarkSources()
                } catch (error: Exception) {
                    if (error is CancellationException) throw error
                }
            }
            Screen.LOGIN -> bookmarkSources = emptyList()
            else -> Unit
        }
    }

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

    val resetPasswordUiState by authViewModel.resetPasswordUiState.collectAsStateWithLifecycle()

    // 離開整個忘記密碼流程才清除；旋轉螢幕保留 ViewModel，程序重建則從 Email 開始。
    LaunchedEffect(currentBackStackEntry?.destination?.route) {
        val route = currentBackStackEntry?.destination?.route
        if (route != null && route != Screen.FORGOT_PASSWORD) authViewModel.resetForgotPasswordState()
    }

    // ------------------------------------------------
    // 其他頁面暫時保留原本的狀態管理
    // ------------------------------------------------

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

    // Keep the share pending through login; consume it only after the form receives it.
    LaunchedEffect(sharedBookmark?.id, currentBackStackEntry?.destination?.route) {
        if (sharedBookmark != null && currentBackStackEntry != null &&
            !UserPreferencesRepository(context.applicationContext).accessToken().isNullOrBlank()) {
            if (currentBackStackEntry?.destination?.route == Screen.LOGIN) navigateToHome()
            if (navController.currentDestination?.route != Screen.ADD_BOOKMARK) {
                navController.navigate(Screen.ADD_BOOKMARK) { launchSingleTop = true }
            }
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

                AuthEvent.LoggedOut -> {
                    authViewModel.resetLoginState()
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                // 註冊成功，前往信箱驗證
                AuthEvent.NavigateToVerifyEmail -> {
                    authViewModel.resetRegisterState()

                    navController.navigate(Screen.VERIFY_EMAIL)
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
        startDestination = Screen.LOGIN,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
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
                onGoogleLoginClick = rememberGoogleSignIn(authViewModel),
                onForgotPasswordClick = {
                    authViewModel.resetForgotPasswordState()
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
                onGoogleRegisterClick = rememberGoogleSignIn(authViewModel),
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

        // 三個畫面在同一導航流程中切換，不將密碼或 Token 放入路由。
        composable(Screen.FORGOT_PASSWORD) {
            val leaveFlow = {
                authViewModel.resetForgotPasswordState()
                navController.navigate(Screen.LOGIN) {
                    popUpTo(Screen.LOGIN) { inclusive = true }
                    launchSingleTop = true
                }
            }
            BackHandler {
                if (forgotPasswordUiState.step == PasswordResetStep.PASSWORD) {
                    if (!resetPasswordUiState.isLoading) authViewModel.returnToResetCode("請重新寄送驗證碼")
                } else leaveFlow()
            }
            LaunchedEffect(forgotPasswordUiState.step) {
                if (forgotPasswordUiState.step == PasswordResetStep.SUCCESS) {
                    Toast.makeText(context, "密碼重設成功，請重新登入", Toast.LENGTH_LONG).show()
                    leaveFlow()
                }
            }
            when (forgotPasswordUiState.step) {
                PasswordResetStep.EMAIL -> ForgotPasswordScreen(
                    uiState = forgotPasswordUiState,
                    onEmailChange = authViewModel::onForgotPasswordEmailChange,
                    onBackClick = leaveFlow,
                    onSendCodeClick = authViewModel::forgotPassword
                )
                PasswordResetStep.CODE -> VerifyEmailScreen(
                    // 僅轉換給共用 UI；註冊與重設的 ViewModel 狀態完全分開。
                    uiState = VerifyEmailUiState(
                        email = forgotPasswordUiState.email,
                        verificationCode = forgotPasswordUiState.code,
                        resendCountdown = forgotPasswordUiState.resendCountdown,
                        errorMessage = forgotPasswordUiState.errorMessage,
                        isLoading = forgotPasswordUiState.isLoading
                    ),
                    onCodeChange = authViewModel::onResetCodeChange,
                    onVerifyClick = authViewModel::verifyResetCode,
                    onResendClick = authViewModel::resendResetCode,
                    title = "驗證密碼重設",
                    description = "若信箱已註冊，將會收到六位數驗證碼",
                    expiryText = "請於 5 分鐘內輸入驗證碼；未收到時請檢查垃圾郵件",
                    buttonText = "驗證並設定新密碼",
                )
                PasswordResetStep.PASSWORD -> ResetPasswordScreen(
                    uiState = resetPasswordUiState,
                    onPasswordChange = authViewModel::onNewPasswordChange,
                    onConfirmPasswordChange = authViewModel::onNewPasswordConfirmationChange,
                    onPasswordVisibilityChange = authViewModel::toggleNewPasswordVisibility,
                    onConfirmPasswordVisibilityChange = authViewModel::toggleNewPasswordConfirmationVisibility,
                    onSaveClick = authViewModel::resetPassword,
                    onBackClick = { authViewModel.returnToResetCode("請重新寄送驗證碼") }
                )
                PasswordResetStep.SUCCESS -> Unit
            }
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
                },
                onLogoutClick = authViewModel::logout
            )
        }

        // Daily Picks
        composable(Screen.DAILY_PICKS) {

            DailyPicksScreen(
                bookmarkSources = bookmarkSources,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddBookmarkClick = {
                    navController.navigate("addBookmark")
                }
            )
        }

        // Search Bookmark
        composable(Screen.SEARCH_BOOKMARK) {

            SearchBookmarkScreen(
                bookmarkSources = bookmarkSources,
                onBackClick = { navigateBackSafely() }
            )
        }

        // Add Bookmark
        composable(Screen.ADD_BOOKMARK) {

            val vm: com.personal.bookmark_app.ui.viewmodel.AddBookmarkViewModel = viewModel()
            val state by vm.state.collectAsStateWithLifecycle()
            val automaticBrowserUrl by vm.automaticBrowserImport.collectAsStateWithLifecycle()
            val browserImport = rememberLauncherForActivityResult(BookmarkBrowserImportContract(), vm::completeBrowserImport)
            LaunchedEffect(sharedBookmark?.id, state.isSaving) {
                sharedBookmark?.takeUnless { state.isSaving }?.let {
                    vm.receiveShare(it)
                    onShareHandled()
                }
            }
            LaunchedEffect(state.isSaved) {
                if (state.isSaved) {
                    Toast.makeText(context, "收藏已新增", Toast.LENGTH_SHORT).show()
                    navigateBackSafely()
                }
            }
            Box(Modifier.fillMaxSize()) {
                automaticBrowserUrl?.let { url ->
                    BackgroundBookmarkImport(url, Modifier.matchParentSize()) { result ->
                        vm.completeAutomaticBrowserImport(url, result)
                    }
                }
                AddBookmarkScreen(
                    uiState = state,
                    onTitleChange = { value -> vm.edit { it.copy(title = value) } },
                    onNoteChange = { value -> vm.edit { it.copy(note = value) } },
                    onSourceUrlChange = vm::changeUrl,
                    onFolderSelected = vm::selectFolder,
                    onCreateFolderClick = vm::createFolder,
                    onTagClick = vm::toggleTag,
                    onCreateTag = vm::addTag,
                    onRetry = vm::loadFolders,
                    onDailyPicksChange = { value -> vm.edit { it.copy(isAddedToDailyPicks = value) } },
                    onReparseClick = { vm.reparseOrOpenBrowser()?.let { browserImport.launch(it) } },
                    onClearTitleClick = { vm.edit { it.copy(title = "") } },
                    onSaveClick = vm::save,
                    onBackClick = { if (!state.isSaving) navigateBackSafely() }
                )
            }
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
            FolderBookmarkScreen(
                folderId = folderId,
                bookmarkSources = bookmarkSources,
                onBackClick = { navigateBackSafely() }
            )
        }
    }
}
