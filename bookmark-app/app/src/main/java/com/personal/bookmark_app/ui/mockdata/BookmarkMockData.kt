package com.personal.bookmark_app.ui.mockdata

import com.personal.bookmark_app.ui.model.DaliyBookmarkUiModel

val bookmarkMockData = listOf(

    DaliyBookmarkUiModel(
        id = 1,
        source = "INSTAGRAM",
        tags = listOf("設計靈感", "字體設計", "復古風格"),
        date = "2024/04/08",
        title = "昭和復古喫茶店的招牌字體特輯",
        description = "手繪圓體、金屬壓印與暖黃霓虹管...",
        imageUrl = null,
        originalUrl = "https://www.instagram.com/",
        note = "這種復古字體很適合未來的設計參考"
    ),

    DaliyBookmarkUiModel(
        id = 2,
        source = "OTHER",
        tags = listOf("深度閱讀", "專注力", "生活"),
        date = "2025/03/11",
        title = "專注的技術：擺脫演算法餵養的慢讀練習",
        description = "重新找回專注閱讀的節奏...",
        imageUrl = null,
        originalUrl = "https://www.theverge.com/",
        note = null
    ),

    DaliyBookmarkUiModel(
        id = 3,
        source = "YOUTUBE",
        tags = listOf("生活提案", "京都旅行", "書店"),
        date = "2025/04/30",
        title = "京都巷弄裡的老書店巡禮：只賣一本書的店",
        description = "藏身在石板小徑後的窄窄木屋，每週僅精選一冊主題讀物，在微光與茶香中感受...",
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDOlyQg8DRF63oSYFbqOlXiAdqMqQes07uZnzbKWyMGO31KVw6Be0r0nweJINfiRj7-QtufYJ-1bkP413-Bz2bvfGomr_bybaGyFgrCIU2k7ckwHTdqMVeCFhxPAwi4cDStTfMlNO03xsCCDh4KjMc8uzLDWGjK7f2me2rXDWsjt1G7Llyt4KiW_ZKFzzbrP7_tJV4OSVEMJI7UGVWeYXgcBLCMkmiXNqQewe9qPXVBkoy57JCl_yIPDg",
        originalUrl = "https://www.youtube.com/",
        note = null
    )
)
