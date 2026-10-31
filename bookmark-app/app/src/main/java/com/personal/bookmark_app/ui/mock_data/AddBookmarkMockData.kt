package com.personal.bookmark_app.ui.mock_data

import com.personal.bookmark_app.ui.model.BookmarkSource
import com.personal.bookmark_app.ui.model.FolderUiModel
import com.personal.bookmark_app.ui.screen.addbookmark.AddBookmarkUiState
import com.personal.bookmark_app.ui.screen.addbookmark.BookmarkPreviewUiModel
import com.personal.bookmark_app.ui.screen.addbookmark.TagOptionUiModel

object AddBookmarkMockData {

    // ==========================================
    // 文章預覽假資料
    // ==========================================

    val preview = BookmarkPreviewUiModel(
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAc0UhWKR7yzzBJLrjr_NvAPuEaEmluA83gfl5sYhpgyXWeacRwbSmdvYvSsPZ5StI74u7lmSGgnvT04Acml3KSHGeQkWe_-yvc5t5gGKdIzKN7zJMNSnhBb9M0DgchNfC0MxVw-DEqtNdcbucCDO3DzTmIFfuWzXppwR4Zjj5crEGF_npWDAouTEpIP28sm12tzNAQwfgh2-VPfdVHQgGi1fs5Q0OuMFzJUokTDR8crlXlseMSJTA0zw",
        title = "東京神保町古書店的選書哲學與紙張香氣",
        sourceUrl = "https://www.threads.net/@tokyo_editorial...",
        source = BookmarkSource.THREADS,
        isParsed = true
    )

    // ==========================================
    // 收藏資料夾假資料
    // 沿用首頁的 FolderUiModel
    // ==========================================

    val folders = listOf(
        FolderUiModel(
            id = 1L,
            title = "未分類",
            count = 18,
            imageUrl = "",
            latestSavedAt = null
        ),

        FolderUiModel(
            id = 2L,
            title = "閱讀清單",
            count = 12,
            imageUrl = "",
            latestSavedAt = "2026-10-30"
        ),

        FolderUiModel(
            id = 3L,
            title = "旅行靈感",
            count = 8,
            imageUrl = "",
            latestSavedAt = "2026-10-29"
        ),

        FolderUiModel(
            id = 4L,
            title = "設計靈感",
            count = 6,
            imageUrl = "",
            latestSavedAt = "2026-10-28"
        )
    )

    // ==========================================
    // 索引標籤假資料
    // ==========================================

    val tags = listOf(
        TagOptionUiModel(
            id = "tag_1",
            name = "生活提案"
        ),

        TagOptionUiModel(
            id = "tag_2",
            name = "旅行靈感"
        ),

        TagOptionUiModel(
            id = "tag_3",
            name = "閱讀清單"
        ),

        TagOptionUiModel(
            id = "tag_4",
            name = "設計靈感"
        ),

        TagOptionUiModel(
            id = "tag_5",
            name = "值得研究"
        )
    )

    // ==========================================
    // 初始畫面狀態
    // ==========================================

    val initialState = AddBookmarkUiState(
        title = preview.title,
        note = "這家店專門收集 70 年代的建築雜誌，下次去東京一定要抽一個下午過去好好翻閱。",
        sourceUrl = preview.sourceUrl,
        selectedSource = BookmarkSource.THREADS,
        selectedFolderId = 1L,
        selectedTagIds = setOf(
            "tag_1",
            "tag_2"
        ),
        isAddedToDailyPicks = true
    )
}