package com.personal.bookmark_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.personal.bookmark_app.data.local.UserPreferencesRepository
import com.personal.bookmark_app.data.remote.api.BookmarkApi
import com.personal.bookmark_app.data.remote.api.UserApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// 導航層透過這個 ViewModel 取得資料，不必在 Composable 內建立 Repository 或 API。
@HiltViewModel
class NavigationViewModel @Inject constructor(
    preferences: UserPreferencesRepository,
    private val userApi: UserApi,
    private val bookmarkApi: BookmarkApi
) : ViewModel() {
    val hasSession = preferences.hasSession

    suspend fun getProfile() = userApi.getProfile().also {
        check(!it.email.isNullOrBlank())
    }

    suspend fun getBookmarkSources() = bookmarkApi.getBookmarkSources()
}
