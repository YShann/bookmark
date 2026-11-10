package com.personal.bookmark_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.personal.bookmark_app.ui.components.LinkGoogleDialog
import com.personal.bookmark_app.ui.components.requestGoogleIdToken
import com.personal.bookmark_app.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
internal fun rememberGoogleSignIn(authViewModel: AuthViewModel): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pendingIdToken by remember { mutableStateOf<String?>(null) }

    pendingIdToken?.let { idToken ->
        LinkGoogleDialog(
            description = "此 Email 已有聚藏帳號。輸入原本的 App 密碼，即可綁定剛才選擇的 Google 帳號並登入。",
            confirmLabel = "綁定並登入",
            onClose = { pendingIdToken = null },
            onSubmit = { password -> authViewModel.confirmGoogleLink(idToken, password) }
        )
    }

    return {
        scope.launch {
            authViewModel.loginWithGoogle(
                requestIdToken = { requestGoogleIdToken(context) },
                onLinkRequired = { pendingIdToken = it }
            )
        }
    }
}
