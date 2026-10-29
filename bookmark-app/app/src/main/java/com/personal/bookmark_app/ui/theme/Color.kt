package com.personal.bookmark_app.ui.theme

import androidx.compose.ui.graphics.Color

// ── 聚藏品牌色 ──
val BookmarkSage = Color(0xFF5E715D)       // 鼠尾草綠：品牌主色
val BookmarkTerracotta = Color(0xFFB88578) // 陶土粉棕：點綴色

// ── 基礎介面色 ──
val BookmarkBackground = Color(0xFFFBF9F3) // 暖紙白：頁面背景
val BookmarkSurface = Color(0xFFFFFDF8)    // 卡片、輸入框表面
val BookmarkText = Color(0xFF2B2C28)       // 主要文字
val BookmarkTextSecondary = Color(0xFF77766F) // 次要文字
val BookmarkBorder = Color(0xFFE5E0D5)     // 邊框、分隔線

// ── 淺色主題文字與狀態色 ──
val BookmarkOnPrimary = Color(0xFFFFFFFF)
val BookmarkOnBackground = BookmarkText
val BookmarkOnSurface = BookmarkText
val BookmarkError = Color(0xFFBA3F38)
val BookmarkOnError = Color(0xFFFFFFFF)

// ── 深色主題色 ──
val BookmarkDarkBackground = Color(0xFF22241F)
val BookmarkDarkSurface = Color(0xFF2B2E28)
val BookmarkDarkText = Color(0xFFEAE7DE)
val BookmarkDarkTextSecondary = Color(0xFFB8B5AA)
val BookmarkDarkBorder = Color(0xFF45483F)
val BookmarkDarkSage = Color(0xFFB4C6AA)
val BookmarkDarkTerracotta = Color(0xFFD6A294)

// ── 舊名稱相容別名 ──
// 若其他畫面仍使用這些名稱，暫時不必全部修改。
val Purple80 = BookmarkDarkSage
val PurpleGrey80 = BookmarkDarkTextSecondary
val Pink80 = BookmarkDarkTerracotta

val Purple40 = BookmarkSage
val PurpleGrey40 = BookmarkTextSecondary
val Pink40 = BookmarkTerracotta
