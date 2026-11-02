package com.personal.bookmark_app.navigation

import android.net.Uri

object Screen {

    // Auth
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val VERIFY_EMAIL = "verifyEmail"
    const val FORGOT_PASSWORD = "forgotPassword"

    // Main
    const val HOME = "home"
    const val DAILY_PICKS = "dailyPicks"
    const val SEARCH_BOOKMARK = "searchBookmark"
    const val ADD_BOOKMARK = "addBookmark"

    // Folder Bookmark
    const val FOLDER_BOOKMARK = "folderBookmark/{folderId}"

    fun folderBookmark(folderName: String): String {
        return "folderBookmark/${Uri.encode(folderName)}"
    }
}