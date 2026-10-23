# 開發規格與階段狀態

## 產品目標

聚藏集中保存使用者從瀏覽器、Threads、Dcard、Instagram 等來源分享或手動輸入的網址，並提供備註、標籤、閱讀狀態、搜尋與跨裝置雲端同步。接收分享網址不代表匯入平台的私人或原生收藏清單。

## MVP 範圍

- 註冊、登入、登出、Token 驗證及登入狀態處理。
- 書籤新增、查詢、編輯、刪除與閱讀狀態。
- 標籤 CRUD 及書籤多標籤關聯。
- Android 系統分享網址與手動輸入。
- 關鍵字搜尋及來源、標籤、閱讀狀態篩選。
- 輸入驗證、錯誤／空資料／載入狀態、單元與整合測試。
- README、架構、資料庫、API、測試及環境設定文件。

不包含第三方平台私人收藏匯入、未授權爬蟲、社交功能、複雜離線佇列或未確認的付費雲端部署。

## 重要設計決策

- Monorepo 內以 `bookmark-app` 與 `bookmark-backend` 分開建置。
- Android 使用 Kotlin、Jetpack Compose、MVVM；後端使用 Java、Spring Boot、JPA 與 MySQL。
- 密碼採安全雜湊；JWT 密鑰與資料庫憑證使用環境設定。
- MySQL schema 以版本化 Migration 建立；不靠 Hibernate 自動建立正式資料表。
- MVP 只保存使用者提供的 URL，不主動向 URL 發送伺服器端請求。
- 開源授權尚未確認，暫不建立 `LICENSE`。

## 階段追蹤

| Phase | 目標 | 狀態 |
|---|---|---|
| 0 | 環境、Git、工具與安全盤點 | 已盤點；開發環境仍缺 Android SDK、PATH 中的 JDK、Docker/MySQL CLI 與 `gh` |
| 1 | Monorepo 文件、忽略規則、環境範本及骨架建置驗證 | 已完成；後端測試、Android Debug APK 與 Android 單元測試通過；MySQL 連線尚未驗證 |
| 2 | 認證及 Android 登入流程 | 尚未開始 |
| 3 | 書籤 CRUD | 尚未開始 |
| 4 | 標籤、搜尋及篩選 | 尚未開始 |
| 5 | Android 分享網址 | 尚未開始 |
| 6 | 整合測試、CI 與作品集整理 | 尚未開始 |

## Git 工作方式

維持 `main` 為穩定分支，功能以 `feature/<name>`、修正以 `fix/<name>` 命名。使用簡潔的 Conventional Commit，例如 `docs: add project architecture and setup guide`。不在未審閱使用者變更時代為提交或推送。

## 尚待決定

- 是否採用 MIT License。
- 本機 MySQL 是否以 Docker Compose 管理。
- GitHub Actions 工作流程將在 Phase 6 核心功能穩定後建立。
