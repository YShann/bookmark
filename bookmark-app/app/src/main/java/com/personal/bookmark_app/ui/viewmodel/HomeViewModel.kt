package com.personal.bookmark_app.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.personal.bookmark_app.data.remote.PageLoader
import com.personal.bookmark_app.data.remote.api.FolderApi
import com.personal.bookmark_app.data.remote.api.FolderNameRequest
import com.personal.bookmark_app.data.local.UserPreferencesRepository
import com.personal.bookmark_app.data.repository.user.UserRepository
import com.personal.bookmark_app.data.repository.FolderChanges
import com.personal.bookmark_app.ui.model.FolderDisplayMode
import com.personal.bookmark_app.ui.model.toUiModel
import com.personal.bookmark_app.ui.screen.home.HomeUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val api: FolderApi,
    private val userRepository: UserRepository,
    folderChanges: FolderChanges
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = combine(_uiState, userPreferencesRepository.folderDisplayMode) { state, mode ->
        state.copy(displayMode = mode)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    private val pages = PageLoader { page, size ->
        api.getFolderPage(page, size).also { pageResponse ->
            _uiState.update { it.copy(folderCount = pageResponse.totalElements) }
        }
    }
    val folders = pages.flow.map { page -> page.map { it.toUiModel() } }.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            folderChanges.changes.collect { refreshFolders() }
        }
    }

    fun updateAvatar(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { userRepository.updateAvatar(uri) }
                .onSuccess { profile -> _uiState.update { it.copy(updatedProfile = profile) } }
        }
    }

    suspend fun linkGoogle(idToken: String, password: String): Result<Unit> =
        userRepository.linkGoogle(idToken, password)
            .map { updatedProfile ->
                _uiState.update { it.copy(updatedProfile = updatedProfile) }
            }

    fun refreshFolders() = pages.refresh()

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

    // 更新收藏夾顯示方式
    fun updateFolderDisplayMode(
        mode: FolderDisplayMode
    ) {
        viewModelScope.launch {
            userPreferencesRepository.saveFolderDisplayMode(mode)
        }
    }
}
