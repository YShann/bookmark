
package com.personal.bookmark_app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = BookmarkSage,
    onPrimary = BookmarkOnPrimary,

    secondary = BookmarkTerracotta,
    onSecondary = BookmarkOnPrimary,

    tertiary = BookmarkTextSecondary,
    onTertiary = BookmarkOnPrimary,

    background = BookmarkBackground,
    onBackground = BookmarkOnBackground,

    surface = BookmarkSurface,
    onSurface = BookmarkOnSurface,

    error = BookmarkError,
    onError = BookmarkOnError,

    outline = BookmarkBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = BookmarkDarkSage,
    onPrimary = BookmarkDarkBackground,

    secondary = BookmarkDarkTerracotta,
    onSecondary = BookmarkDarkBackground,

    tertiary = BookmarkDarkTextSecondary,
    onTertiary = BookmarkDarkBackground,

    background = BookmarkDarkBackground,
    onBackground = BookmarkDarkText,

    surface = BookmarkDarkSurface,
    onSurface = BookmarkDarkText,

    error = BookmarkError,
    onError = BookmarkOnError,

    outline = BookmarkDarkBorder
)

@Composable
fun BookmarkappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),

    // 聚藏預設使用 DESIGN.md 定義的品牌配色。
    // 若未來想採用 Android 12+ 的系統動態色，可手動傳入 true。
    dynamicColor: Boolean = false,

    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> DarkColorScheme

        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
