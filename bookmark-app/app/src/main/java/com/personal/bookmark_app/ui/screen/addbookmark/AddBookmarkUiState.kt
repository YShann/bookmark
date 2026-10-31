package com.personal.bookmark_app.ui.screen.addbookmark

import com.personal.bookmark_app.ui.model.BookmarkSource

data class AddBookmarkUiState(

    // 基本收藏資料
    val title: String = "",
    val note: String = "",
    val sourceUrl: String = "",

    // 來源平台
    val selectedSource: BookmarkSource = BookmarkSource.WEB,

    // 收藏資料夾 ID
    val selectedFolderId: Long = 1L,

    // 已選取的標籤 ID
    val selectedTagIds: Set<String> = emptySet(),

    // 是否加入今日拾藏備選庫
    val isAddedToDailyPicks: Boolean = true,

    // 網址解析狀態
    val isParsing: Boolean = false,

    // 彈出視窗狀態
    val isFolderSelectorVisible: Boolean = false,
    val isTagCreatorVisible: Boolean = false,

    // 錯誤訊息
    val errorMessage: String? = null

) {

    // 標題是否有效
    val isTitleValid: Boolean
        get() = title.isNotBlank()

    // 是否達到標籤選取上限
    val isTagLimitReached: Boolean
        get() = selectedTagIds.size >= 5

    // 已選取的標籤數量
    val selectedTagCount: Int
        get() = selectedTagIds.size

    // 表單是否可以儲存
    val isFormValid: Boolean
        get() = isTitleValid && !isParsing
}