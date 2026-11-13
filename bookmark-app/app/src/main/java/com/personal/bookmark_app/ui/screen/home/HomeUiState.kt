package com.personal.bookmark_app.ui.screen.home

import com.personal.bookmark_app.data.remote.dto.response.user.UserProfileResponse
import com.personal.bookmark_app.ui.model.FolderDisplayMode

data class HomeUiState(
    val folderCount: Long = 0,
    val displayMode: FolderDisplayMode = FolderDisplayMode.GRID,
    val updatedProfile: UserProfileResponse? = null
)
