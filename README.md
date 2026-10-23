# 聚藏 Bookmark App

聚藏是一個個人雲端書籤管理專案，規劃以 Android App 接收及管理網址，並透過 Spring Boot API 與 MySQL 儲存使用者資料。

> 專案目前處於 Phase 1：已有 Android 與 Spring Boot 初始骨架。註冊登入、書籤 API、資料表與 Android 功能尚未實作；目前沒有線上 Demo。

## 目前狀態

- Android：Kotlin、Jetpack Compose 初始畫面；Debug APK 建置與單元測試通過。
- Backend：Spring Boot 啟動類與 Gradle 建置設定；範例 Context 測試使用測試專用 H2。
- Database：預定使用 MySQL；Migration 與資料表尚未建立。
- GitHub Actions：尚未設定。

## 目錄

```text
bookmark-app/       Android App
bookmark-backend/   Spring Boot REST API
docs/               架構與開發文件
```

## 架構

請參閱 [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)。資料庫 ERD 與 API 規格會在相應階段建立並記錄於 `docs/`。

## 開發環境

目前建議使用 JDK 21、Android Studio（含 Android SDK）、Git，以及 MySQL 8.4。各子專案附有 Gradle Wrapper。實際建置前請確認 Android SDK 已安裝，並依本機環境設定 `ANDROID_HOME`、`JAVA_HOME`。

### Backend

```powershell
cd bookmark-backend
./gradlew.bat test
./gradlew.bat bootRun
```

目前後端尚無 API 或資料庫 Migration，且未設定資料庫連線；啟動與測試尚待本機環境安裝後驗證。

### Android

使用 Android Studio 開啟 `bookmark-app`，設定 Android SDK 後即可嘗試 Sync／Build。模擬器或實體手機 API 位址會在 Android 串接階段說明。

## 設定與機密

`.env.example` 列出預計使用的環境變數名稱與不可直接使用的佔位值。請在本機自行設定真實的資料庫帳密及 JWT 密鑰；不要提交 `.env`、Token、密碼或簽署金鑰。

## 測試

Spring Boot Context 範例測試與 Android Debug APK／單元測試已在本機通過。目前測試仍是骨架範例，尚未涵蓋 MVP 驗收需求。後續測試方式與結果記錄於 [docs/TESTING.md](docs/TESTING.md)。

## 文件

- [開發規格與階段狀態](docs/DEVELOPMENT_SPEC.md)
- [系統架構](docs/ARCHITECTURE.md)
- [資料庫設計](docs/DATABASE.md)（待 Phase 2/3 建立）
- [API 規格](docs/API.md)（待實作時建立）
- [測試策略](docs/TESTING.md)（待建立）

## 授權

尚未選定開源授權，請在授權確認後再加入 `LICENSE`。

## Demo 與畫面

尚無已驗證的 Demo 連結或專案截圖。
