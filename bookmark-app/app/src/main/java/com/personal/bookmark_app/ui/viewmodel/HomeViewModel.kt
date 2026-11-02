package com.personal.bookmark_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.personal.bookmark_app.data.local.UserPreferencesRepository
import com.personal.bookmark_app.ui.model.FolderDisplayMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val userPreferencesRepository =
        UserPreferencesRepository(application)

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