package com.personal.bookmark_app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// 標題：先使用系統襯線字體。
// 之後將 Noto Serif TC 字型檔加入 res/font 後，可替換成實際字型。
private val BookmarkHeadingFont = FontFamily.Serif

// 內文與介面：使用系統無襯線字體。
// 之後可替換成 Noto Sans TC。
private val BookmarkBodyFont = FontFamily.SansSerif

val Typography = Typography(
    // 大型頁面標題
    headlineLarge = TextStyle(
        fontFamily = BookmarkHeadingFont,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),

    // 一般頁面標題
    headlineMedium = TextStyle(
        fontFamily = BookmarkHeadingFont,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),

    // 區塊標題
    headlineSmall = TextStyle(
        fontFamily = BookmarkHeadingFont,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),

    // 卡片標題、收藏標題
    titleLarge = TextStyle(
        fontFamily = BookmarkHeadingFont,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),

    titleMedium = TextStyle(
        fontFamily = BookmarkBodyFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),

    titleSmall = TextStyle(
        fontFamily = BookmarkBodyFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),

    // 一般內文
    bodyLarge = TextStyle(
        fontFamily = BookmarkBodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = BookmarkBodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),

    bodySmall = TextStyle(
        fontFamily = BookmarkBodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    ),

    // 按鈕、標籤、輔助資訊
    labelLarge = TextStyle(
        fontFamily = BookmarkBodyFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),

    labelMedium = TextStyle(
        fontFamily = BookmarkBodyFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp
    ),

    labelSmall = TextStyle(
        fontFamily = BookmarkBodyFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp
    )
)
