# 資料庫設計

目前 MySQL `bookmark` 資料庫已由專案維護者建立，但尚無本專案建立的資料表或 Migration；以下為預定設計，尚未實作或驗證。

本次本機環境檢查無法連線至 `127.0.0.1:3306`，因此尚未驗證 MySQL Server 是否啟動或資料庫憑證是否可用。`mysql` CLI 也不在 PATH。

## 預定 ERD

```text
users 1 ─── * bookmarks
users 1 ─── * tags
bookmarks * ─── * tags
             via bookmark_tags
```

## 預定資料表

- `users`：`id`、唯一 `email`、`password_hash`、`created_at`、`updated_at`。
- `bookmarks`：`id`、`user_id`、`url`、`title`、`source`、`note`、`is_read`、時間欄位。
- `tags`：`id`、`user_id`、`name`、`created_at`；預計限制同一使用者的標籤名稱唯一。
- `bookmark_tags`：`bookmark_id`、`tag_id` 複合主鍵；書籤或標籤刪除時清除關聯。

正式欄位長度、外鍵、索引、刪除規則與 Flyway Migration 將於資料庫實作階段定案。每個資料查詢均需依目前登入者限制資料範圍。
