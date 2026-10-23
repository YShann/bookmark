# 測試策略

目前只有 Android Studio 與 Spring Initializr 產生的範例測試；Backend Context 測試已通過（Gradle 9.8.2、JDK 21、測試專用 H2）。這不代表 MVP 功能測試已完成。

## 預計驗證

- Backend：註冊與登入、Token 驗證、書籤與標籤 CRUD、輸入驗證、搜尋篩選、跨使用者資料隔離及標籤所有權。
- Android：Repository 錯誤轉換、ViewModel 狀態、登入狀態、列表搜尋篩選、分享網址解析及主要 UI 狀態。
- 整合：使用隔離的 MySQL 測試環境及版本化 Migration。

## Phase 1 執行紀錄

- Backend `./gradlew.bat test`：通過。初次執行因沒有資料庫連線而失敗；加入 test-only H2 後重跑成功。
- Android `./gradlew.bat assembleDebug`：通過，Debug APK 產生於忽略的 `bookmark-app/app/build/outputs/apk/debug/`。
- Android `./gradlew.bat testDebugUnitTest`：通過（目前為範例單元測試）。
- MySQL：未驗證資料庫登入；本次檢查 `127.0.0.1:3306` 無法連線，且 `mysql` CLI 不在 PATH。

## 本機命令

```powershell
cd bookmark-backend
./gradlew.bat test
```

Android 單元測試可在 Android Studio Gradle 視窗執行 `testDebugUnitTest`，或在 `bookmark-app` 目錄執行 `./gradlew.bat testDebugUnitTest`。實際命令需待工具環境安裝並驗證後更新。
