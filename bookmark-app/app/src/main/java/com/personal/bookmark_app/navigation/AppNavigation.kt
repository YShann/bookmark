package com.personal.bookmark_app.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.personal.bookmark_app.SharedBookmark
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkSourceResponse
import com.personal.bookmark_app.data.remote.dto.response.user.UserProfileResponse
import com.personal.bookmark_app.ui.screen.addbookmark.AddBookmarkScreen
import com.personal.bookmark_app.ui.screen.addbookmark.BackgroundBookmarkImport
import com.personal.bookmark_app.ui.screen.addbookmark.BookmarkBrowserImportContract
import com.personal.bookmark_app.ui.screen.dailypicks.DailyPicksScreen
import com.personal.bookmark_app.ui.screen.folderbookmark.FolderBookmarkScreen
import com.personal.bookmark_app.ui.screen.home.HomeScreen
import com.personal.bookmark_app.ui.screen.login.LoginScreen
import com.personal.bookmark_app.ui.screen.register.RegisterScreen
import com.personal.bookmark_app.ui.screen.searchbookmark.SearchBookmarkScreen
import com.personal.bookmark_app.ui.screen.verifyemail.VerifyEmailScreen
import com.personal.bookmark_app.ui.viewmodel.AddBookmarkViewModel
import com.personal.bookmark_app.ui.viewmodel.AuthEvent
import com.personal.bookmark_app.ui.viewmodel.AuthViewModel
import com.personal.bookmark_app.ui.viewmodel.DailyPicksViewModel
import com.personal.bookmark_app.ui.viewmodel.NavigationViewModel
import kotlinx.coroutines.CancellationException

@Composable
fun AppNavigation(
    sharedBookmark: SharedBookmark? = null,
    onShareHandled: (String) -> Unit = {},
    navigationViewModel: NavigationViewModel = hiltViewModel()
) {
    val signedIn by navigationViewModel.hasSession.collectAsStateWithLifecycle(initialValue = null)
    val sessionActive = signedIn
    if (sessionActive == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    key(sessionActive) {
        if (sessionActive) {
            var attempt by remember { mutableStateOf(0) }
            var result by remember(attempt) { mutableStateOf<Result<UserProfileResponse>?>(null) }
            LaunchedEffect(navigationViewModel, attempt) {
                result = runCatching {
                    navigationViewModel.getProfile()
                }.onFailure { if (it is CancellationException) throw it }
            }

            val profile = result?.getOrNull()
            if (profile != null) {
                SessionNavigation(profile, navigationViewModel, sharedBookmark, onShareHandled)
            } else {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp), contentAlignment = Alignment.Center
                ) {
                    if (result == null) {
                        CircularProgressIndicator()
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("無法確認登入狀態，請檢查連線後再試")
                            Button(onClick = { attempt++ }) { Text("重試") }
                        }
                    }
                }
            }
        } else {
            SessionNavigation(null, navigationViewModel, sharedBookmark, onShareHandled)
        }
    }
}

@Composable
private fun SessionNavigation(
    profile: UserProfileResponse?,
    navigationViewModel: NavigationViewModel,
    sharedBookmark: SharedBookmark?,
    onShareHandled: (String) -> Unit
) {
    val signedIn = profile != null
    val navController = rememberNavController()
    val context = LocalContext.current

    // 觀察目前所在的 Navigation 頁面
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    var bookmarkSources by remember { mutableStateOf(emptyList<BookmarkSourceResponse>()) }
    LaunchedEffect(currentBackStackEntry?.destination?.route) {
        when (currentBackStackEntry?.destination?.route) {
            Screen.SEARCH_BOOKMARK, Screen.FOLDER_BOOKMARK, Screen.DAILY_PICKS -> {
                try {
                    bookmarkSources = navigationViewModel.getBookmarkSources()
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

    val authViewModel: AuthViewModel = hiltViewModel()
    LaunchedEffect(signedIn) {
        if (signedIn) authViewModel.resetLoginState()
    }

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

    // Keep the share pending until the form closes, including across session invalidation.
    LaunchedEffect(sharedBookmark?.id, currentBackStackEntry?.destination?.route) {
        if (signedIn && sharedBookmark != null && currentBackStackEntry != null &&
            navController.currentDestination?.route != Screen.ADD_BOOKMARK
        ) {
            navController.navigate(Screen.ADD_BOOKMARK) { launchSingleTop = true }
        }
    }

    // ------------------------------------------------
    // 接收 AuthViewModel 的一次性事件
    // ------------------------------------------------

    LaunchedEffect(authViewModel) {
        authViewModel.authEvent.collect { event ->

            when (event) {

                // 登入或信箱驗證成功
                AuthEvent.Authenticated, AuthEvent.LoggedOut -> Unit

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
        startDestination = if (signedIn) Screen.HOME else Screen.LOGIN,
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

        composable(Screen.FORGOT_PASSWORD) {
            PasswordResetRoute(
                authViewModel = authViewModel,
                forgotPasswordUiState = forgotPasswordUiState,
                resetPasswordUiState = resetPasswordUiState,
                navController = navController
            )
        }

        // ============================================
        // Main
        // ============================================

        // Home
        composable(Screen.HOME) {
            if (profile == null) return@composable

            HomeScreen(
                initialProfile = profile,
                onSearchClick = { navController.navigate(Screen.SEARCH_BOOKMARK) },
                onAddBookmarkClick = { navController.navigate(Screen.ADD_BOOKMARK) },
                onDailyPicksClick = { navController.navigate(Screen.DAILY_PICKS) },
                onFolderClick = { folder ->
                    navController.navigate(Screen.folderBookmark(folder.id))
                },
                onLogoutClick = authViewModel::logout
            )
        }

        // Daily Picks
        composable(Screen.DAILY_PICKS) { entry ->
            // 返回首頁再進入時，沿用首頁返回堆疊中的今日拾藏資料。
            val homeEntry = remember(entry) { navController.getBackStackEntry(Screen.HOME) }
            val viewModel: DailyPicksViewModel = hiltViewModel(homeEntry)
            DailyPicksScreen(
                viewModel = viewModel,
                bookmarkSources = bookmarkSources,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddBookmarkClick = {
                    navController.navigate(Screen.ADD_BOOKMARK)
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

            val vm: AddBookmarkViewModel = hiltViewModel()
            val state by vm.uiState.collectAsStateWithLifecycle()
            val browserImport = rememberLauncherForActivityResult(
                BookmarkBrowserImportContract(),
                vm::completeBrowserImport
            )
            var receivedShareId by rememberSaveable { mutableStateOf<String?>(null) }
            LaunchedEffect(sharedBookmark?.id, state.isSaving, state.isSaved) {
                sharedBookmark?.takeUnless { state.isSaving || state.isSaved }?.let {
                    vm.receiveShare(it)
                    receivedShareId = it.id
                }
            }
            val closeForm = {
                receivedShareId?.let(onShareHandled)
                navigateBackSafely()
            }
            BackHandler {
                if (!state.isSaving) closeForm()
            }
            LaunchedEffect(state.isSaved) {
                if (state.isSaved) {
                    Toast.makeText(context, "收藏已新增", Toast.LENGTH_SHORT).show()
                    closeForm()
                }
            }
            Box(Modifier.fillMaxSize()) {
                state.automaticBrowserUrl?.let { url ->
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
                    onReparseClick = {
                        vm.reparseOrOpenBrowser()?.let { browserImport.launch(it) }
                    },
                    onClearTitleClick = { vm.edit { it.copy(title = "") } },
                    onSaveClick = vm::save,
                    onBackClick = { if (!state.isSaving) closeForm() }
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
