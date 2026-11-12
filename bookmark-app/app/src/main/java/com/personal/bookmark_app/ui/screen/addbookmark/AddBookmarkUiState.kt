package com.personal.bookmark_app.ui.screen.addbookmark

data class AddBookmarkUiState(

    // 基本收藏資料
    val title: String = "",
    val note: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val parseError: String? = null,
    val sourceUrl: String = "",

    // 收藏資料夾 ID
    val selectedFolderId: Long = 0L,

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
    val folders: List<com.personal.bookmark_app.ui.model.FolderUiModel> = emptyList(),
    val tags: List<com.personal.bookmark_app.ui.model.TagOptionUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
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
        get() = isTitleValid && title.trim().length <= 255 && sourceUrl.length <= 2048 &&
            runCatching { java.net.URI(sourceUrl.trim()).let {
                it.scheme?.lowercase() in setOf("http", "https") && !it.host.isNullOrBlank()
            } }.getOrDefault(false) && selectedFolderId > 0 && !isLoading && !isSaving && !isSaved && !isParsing
}
