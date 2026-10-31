package com.personal.bookmark_app.ui.mock_data

import com.personal.bookmark_app.ui.model.FolderDisplayMode
import com.personal.bookmark_app.ui.model.FolderUiModel

val DisplayMode = FolderDisplayMode.LIST

val FolderMockData = listOf(
    // 固定排在第一個的未分類
    FolderUiModel(
        id = 0L,
        title = "未分類",
        count = 18,
        imageUrl = "https://placehold.co/600x420/png?text=Desk",
        latestSavedAt = null
    ),

    FolderUiModel(
        id = 1L,
        title = "工作",
        count = 21,
        imageUrl = "https://placehold.co/600x420/png?text=Work",
        latestSavedAt = "2026-10-30T15:30:00"
    ),

    FolderUiModel(
        id = 2L,
        title = "生活靈感",
        count = 9,
        imageUrl = "https://placehold.co/600x420/png?text=Life",
        latestSavedAt = "2026-10-28T18:20:00"
    ),

    FolderUiModel(
        id = 3L,
        title = "京都喫茶店與老鋪書店選書漫遊指南",
        count = 14,
        imageUrl = "https://placehold.co/600x420/png?text=Kyoto",
        latestSavedAt = "2026-10-25T12:15:00"
    ),

    FolderUiModel(
        id = 4L,
        title = "閱讀筆記",
        count = 0,
        imageUrl = "https://placehold.co/600x420/png?text=Reading",
        latestSavedAt = null
    )
)
