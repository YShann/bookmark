# API 規格

本文件記錄目標 API；目前後端尚未實作 API，以下路徑與行為尚未驗證。

## 前綴與認證

API 前綴為 `/api/v1`。受保護端點預計使用 `Authorization: Bearer <access-token>`。

## 預定端點

| Method | Path | 說明 |
|---|---|---|
| POST | `/auth/register` | 註冊 |
| POST | `/auth/login` | 登入並取得 Access Token |
| GET | `/auth/me` | 目前使用者 |
| GET / POST | `/bookmarks` | 書籤列表／新增 |
| GET / PUT / DELETE | `/bookmarks/{id}` | 書籤詳細／修改／刪除 |
| GET / POST | `/tags` | 標籤列表／新增 |
| PUT / DELETE | `/tags/{id}` | 標籤修改／刪除 |

書籤列表預計支援 `keyword`、`source`、`isRead`、`tagId` 與有上限的分頁；新增或修改書籤以標籤 ID 陣列指定標籤，並驗證所有權。確切 DTO、狀態碼、分頁預設及錯誤格式會在 API 實作階段補齊。
