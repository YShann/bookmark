package com.personal.bookmark_app.ui.mock_data

import com.personal.bookmark_app.ui.model.FolderBookmarkUiModel

object FolderDetailMockData {

    val availableTags = listOf(
        "生活",
        "閱讀",
        "美食",
        "旅遊",
        "料理",
        "設計",
        "工具"
    )

    val bookmarks = listOf(
        FolderBookmarkUiModel(
            id = "1",
            source = "Threads",
            date = "2024/06/12",
            title = "台北巷弄裡的質感咖啡廳",
            description = "在這座城市裡，總有一些不經意走進去...",
            imageUrl = "https://images.unsplash.com/photo-1445116572660-236099ec97a0?w=240",
            tags = listOf("美食", "生活"),
            note = "老闆推薦手沖耶加雪菲，二樓窗邊位置採光最好，平日不限時，適合帶筆電或閱讀一整個下午。"
        ),
        FolderBookmarkUiModel(
            id = "2",
            source = "Dcard",
            date = "2024/06/10",
            title = "讓生活變美好的 5 個習慣",
            description = "從日常小事開始，慢慢變成更喜歡的自己...",
            imageUrl = "https://images.unsplash.com/photo-1497250681960-ef046c08a56e?w=240",
            tags = listOf("生活", "閱讀")
        ),
        FolderBookmarkUiModel(
            id = "3",
            source = "Instagram",
            date = "2024/06/08",
            title = "京都小旅行｜必去的 5 個景點",
            description = "這次的京都行，記錄了幾個很喜歡的地...",
            imageUrl = "https://images.unsplash.com/photo-1516483638261-f4dbaf036963?w=240",
            tags = listOf("旅遊", "生活")
        ),
        FolderBookmarkUiModel(
            id = "4",
            source = "YouTube",
            date = "2024/06/05",
            title = "簡單好吃的家常料理｜番茄燉飯",
            description = "食材簡單、步驟不難，在家也能輕鬆做...",
            imageUrl = "https://images.unsplash.com/photo-1476224203421-9ac39bcb3327?w=240",
            tags = listOf("美食", "料理")
        ),
        FolderBookmarkUiModel(
            id = "5",
            source = "網頁",
            date = "2024/06/02",
            title = "如何建立自己的數位資料庫",
            description = "整理收藏、管理資訊，讓靈感不再從指尖流失...",
            imageUrl = "https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=240",
            tags = listOf("設計", "工具")
        )
    )
}
