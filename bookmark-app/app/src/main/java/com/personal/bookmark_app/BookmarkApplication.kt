package com.personal.bookmark_app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// App 啟動時建立 Hilt 容器，統一管理需要共用的物件。
@HiltAndroidApp
class BookmarkApplication : Application()
