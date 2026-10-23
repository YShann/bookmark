# 系統架構

## 目標架構

```text
Android App
  ├── Compose UI
  ├── ViewModel / StateFlow
  ├── Repository
  └── Retrofit / OkHttp
          |
          | HTTPS / JSON / JWT
          v
Spring Boot REST API
  ├── Controller
  ├── Service
  ├── Security
  ├── Repository
  └── JPA
          |
          v
        MySQL
```

上述為目標設計；目前只存在 Android Compose 初始畫面與 Spring Boot 啟動骨架，其他層尚未實作。

## 責任分工

- **Compose UI：**呈現畫面狀態並回報使用者操作，不直接呼叫 HTTP。
- **ViewModel：**管理畫面狀態，以 Coroutine／StateFlow 協調 UI 與資料操作。
- **Repository：**封裝資料來源，轉換 API 結果及錯誤供畫面使用。
- **Network：**以 Retrofit／OkHttp 呼叫 REST API，附加登入 Token；不記錄密碼或 Token。
- **Controller：**接收及驗證 HTTP 請求，回傳 DTO 與適當狀態碼。
- **Service：**執行商業規則，從已驗證身分取得使用者並檢查資源所有權。
- **Repository／JPA：**透過使用者條件查詢及持久化資料。
- **MySQL：**保存帳號、書籤、標籤與關聯；正式結構由版本化 Migration 管理。

## 資料流與錯誤處理

Android ViewModel 發出操作後由 Repository 呼叫 API。後端先驗證輸入與 Token，再由 Service 執行授權及資料操作。API 以一致的錯誤格式回應；Android Repository 將 HTTP、驗證與網路錯誤轉為 UI 狀態。登入逾期時清除本機登入狀態並引導重新登入。

## 安全邊界

- 使用者識別由後端驗證 Token 取得，不信任客戶端傳來的 `userId`。
- 書籤、標籤及關聯操作都需限制為目前登入使用者。
- JWT 簽章密鑰與資料庫密碼由部署環境提供，不放入原始碼或 Android App。
- MVP 儲存原始網址並依網域辨識來源，不由後端抓取網頁中繼資料，以避免引入 SSRF 風險。
