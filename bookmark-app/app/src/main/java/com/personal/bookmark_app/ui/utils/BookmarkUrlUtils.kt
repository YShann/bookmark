package com.personal.bookmark_app.ui.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri

/**
 * 開啟收藏的原始網址
 */
fun openBookmarkUrl(
    context: Context,
    url: String
) {
    val trimmedUrl = url.trim()

    // 檢查網址是否為空
    if (trimmedUrl.isBlank()) {
        Toast.makeText(
            context,
            "此收藏沒有可開啟的網址",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    // 解析網址
    val uri = trimmedUrl.toUri()

    // 僅允許 HTTP / HTTPS 網址
    val isValidScheme = uri.scheme?.lowercase() in setOf("http", "https")
    val hasValidHost = !uri.host.isNullOrBlank()

    if (!isValidScheme || !hasValidHost) {
        Toast.makeText(
            context,
            "網址格式不正確",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    // 開啟外部網頁
    val intent = Intent(Intent.ACTION_VIEW, uri)

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            "找不到可以開啟網頁的應用程式",
            Toast.LENGTH_SHORT
        ).show()
    }
}