package com.personal.bookmark_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.personal.bookmark_app.data.remote.RetrofitClient
import com.personal.bookmark_app.data.remote.api.BookmarkApi
import com.personal.bookmark_app.data.remote.api.FolderApi
import com.personal.bookmark_app.data.remote.api.FolderNameRequest
import com.personal.bookmark_app.data.remote.api.UserApi
import com.personal.bookmark_app.data.remote.dto.response.folder.FolderResponse
import com.personal.bookmark_app.data.remote.dto.response.user.UserProfileResponse
import com.personal.bookmark_app.data.local.UserPreferencesRepository
import com.personal.bookmark_app.data.repository.user.ApiUserRepository
import com.personal.bookmark_app.data.repository.user.UserRepository
import com.personal.bookmark_app.ui.model.FolderDisplayMode
import com.personal.bookmark_app.ui.model.FolderUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val userPreferencesRepository =
        UserPreferencesRepository(application)
    private val retrofit = RetrofitClient.authenticated(application)
    private val api = retrofit.create(FolderApi::class.java)
    private val bookmarkApi = retrofit.create(BookmarkApi::class.java)
    private val userRepository: UserRepository = ApiUserRepository(
        retrofit.create(UserApi::class.java),
        application.contentResolver
    )
    private val _folders = MutableStateFlow<List<FolderUiModel>>(emptyList())
    val folders = _folders.asStateFlow()
    private val _isRefreshingFolders = MutableStateFlow(false)
    val isRefreshingFolders = _isRefreshingFolders.asStateFlow()
    private val _profile = MutableStateFlow<UserProfileResponse?>(null)
    val profile = _profile.asStateFlow()

    init {
        refreshFolders()
    }

    fun updateAvatar(uri: android.net.Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { userRepository.updateAvatar(uri) }
                .onSuccess { _profile.value = it }
        }
    }

    suspend fun linkGoogle(idToken: String, password: String): Result<Unit> =
        userRepository.linkGoogle(idToken, password)
            .map { updatedProfile ->
                _profile.value = updatedProfile
            }

    fun refreshFolders() {
        if (_isRefreshingFolders.value) return
        _isRefreshingFolders.value = true

        viewModelScope.launch {
            try {
                runCatching {
                    withContext(Dispatchers.IO) {
                        api.getFolders().map { folder ->
                            async(Dispatchers.IO) {
                                folder.toUiModel(bookmarkApi.getFolderBookmarks(folder.id).size)
                            }
                        }.awaitAll().sortedWith(compareByDescending<FolderUiModel> { it.title == "未分類" })
                    }
                }.onSuccess { _folders.value = it }
            } finally {
                _isRefreshingFolders.value = false
            }
        }
    }

    fun renameFolder(id: Long, name: String) = mutateFolders {
        api.renameFolder(id, FolderNameRequest(name))
    }

    fun deleteFolder(id: Long) = mutateFolders {
        api.deleteFolder(id, deleteBookmarks = true)
    }

    fun moveFolderBookmarksToUnclassified(id: Long) = mutateFolders {
        api.deleteFolder(id, deleteBookmarks = false)
    }

    suspend fun createFolder(name: String) {
        api.createFolder(FolderNameRequest(name))
        refreshFolders()
    }

    private fun mutateFolders(action: suspend () -> Any?) {
        viewModelScope.launch {
            runCatching { withContext(Dispatchers.IO) { action() } }
                .onSuccess { refreshFolders() }
        }
    }

    // 目前收藏夾顯示方式
    val folderDisplayMode =
        userPreferencesRepository.folderDisplayMode.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FolderDisplayMode.GRID
        )

    // 更新收藏夾顯示方式
    fun updateFolderDisplayMode(
        mode: FolderDisplayMode
    ) {
        viewModelScope.launch {
            userPreferencesRepository.saveFolderDisplayMode(mode)
        }
    }
}

private fun FolderResponse.toUiModel(count: Int) = FolderUiModel(
    id = id,
    title = name,
    count = count,
    imageUrl = imageUrl.orEmpty(),
    latestSavedAt = null
)
