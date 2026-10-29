# DESIGN.md — 聚藏 Bookmark App 統一設計規格

> 本文件整合 Stitch 視覺設計系統與 Android / Jetpack Compose 實作規格。若舊稿描述不一致，以本文件的統一規範為準。

> **文件版本**：1.0.0  
> **目標平台**：Android (API 26+)  
> **開發語言**：Kotlin 2.0+  
> **UI 框架**：Jetpack Compose (Compose BOM 2024.02+) / Jetpack Glance (App Widget)  
> **設計基礎規範**：Material Design 3 (M3)  
> **資料來源基礎**：Stitch 專案中已定稿之「聚藏 Bookmark App」高保真 UI Screens 與 Design System Tokens。

---

## 1. Project Overview

### 1.1 App 名稱
**聚藏 Bookmark App** (英文識別：Ju Cang / Gather & Bookmark)

### 1.2 產品定位與核心用途
- **產品定位**：專注於「重新發現被遺忘的收藏」的日系雜誌編輯感、個人數位書籤手帳 App。
- **核心用途**：
    1. 跨平台（Instagram、Threads、YouTube、Dcard、Web 等）內容快速收錄與分類管理。
    2. 自動／自訂標籤抽取與快速檢索。
    3. 「今日拾藏（Daily Pick）」每日抽樣喚醒與回顧機制。
    4. 隨附個人深度手帳筆記（札記備註）。
    5. 搭配 Android 桌面小工具（Widget），讓收藏融入日常生活桌面。

### 1.3 主要使用情境
1. **收錄與編輯典藏**：使用者從外部分享或於 App 貼上 URL，系統自動抓取標題、封面圖、摘要，使用者選擇分類夾、勾選/新增標籤、記錄札記筆記。
2. **分類瀏覽與批次整理**：透過多層級分類夾（區塊網格 / 條列檢視）或標籤進行 OR/AND 複合篩選；支援批次刪除與移動。
3. **今日拾藏喚醒**：每日由系統抽樣 3 則收藏，鼓勵使用者重讀舊時光，點擊外連原網址或翻閱筆記。
4. **桌面常駐體驗**：透過 4x2 / 4x1 桌面 Widget 顯示最新拾藏金句與待回顧清單，提供快速新增與喚醒入口。

---

## 2. Design Principles

### 2.1 整體設計風格與視覺氛圍
- **日系生活選物 × 暖調紙本雜誌編輯風格**：
    - 放棄冰冷科技感的純灰或藍色基調，採用 **暖米白紙本色（#FBF9F3）** 作為全域畫布底色。
    - 主調使用 **深炭灰（#2B2C28 / #1C1C1A）** 印刷墨色文字，搭配 **沉穩鼠尾草綠（#5E715D）** 與 **陶土棕（#B88578）** 作為重點與標籤微徽章。
    - 卡片容器採用純白紙質面（#FFFFFF）搭配 1px 極細邊框（#E6E2D8），無厚重投影，呈現紙質層疊感。

### 2.2 品牌視覺與介面層級原則

- **整體氣質**：Modern Editorial Minimalist（現代編輯式極簡），結合日系獨立刊物、溫潤生活風格誌與紙本手帳感。視覺安靜、溫柔、克制，讓收藏內容成為焦點。
- **紙本質感**：以平整暖米白色塊、低對比細線、微弱暖調陰影建立層次；不使用鮮豔漸層、霓虹效果或 Glassmorphism。
- **文字層級**：標題以襯線字體營造編輯感；正文與操作資訊使用清晰無襯線字體。避免以大量粗體建立層級，優先使用字級、留白與位置。
- **卡片與控制項**：一般卡片與按鈕使用 4–12dp 微圓角；標籤 Chip 可使用膠囊形以符合 Material 3 操作習慣，但避免把所有容器都做成膠囊。
- **無侵入性瀏覽**：
- **無侵入性沈浸瀏覽（No Fixed Toolbar）**：
    - 最新版本已移除各 Screen 頂部生硬的固定 Toolbar Header，改以頁面內大標題左側內嵌 `<`（Chevron Left）返回鍵，頂部自然保留 `WindowInsets.statusBars` 安全留白。
- **純粹操作聚焦（不設全域 Bottom Tab Bar）**：
    - 首頁、收藏清單、編輯、管理、搜尋與小工具等畫面不加入全域底部導覽列，操作直接聚焦於頁面內容與必要的 Bottom Sheet。
- **觸控與可見性層次**：
    - 重要標題使用具人文氣息的襯線體（Noto Serif CJK TC）；引言金句使用楷書手寫手帳字體；數字、編目與操作項目使用幾何無襯線體。

---


### 2.3 視覺一致性規範
- 背景與容器以暖米白、紙白及低彩度暖灰建立層次；避免大面積高飽和色。
- 主色鼠尾草綠只用於主要操作、選取狀態與必要的焦點提示。
- 陶土棕作為少量點綴，例如今日拾藏標記、提示圓點與日期印記，不取代主要操作色。
- 邊框優先使用 1dp 暖灰細線；一般卡片不使用明顯陰影，僅懸浮元件或 Bottom Sheet 使用低對比陰影。
- 來源平台標記以中性色文字與簡潔線性圖示呈現，不直接套用各社群平台的高飽和品牌色。
- 介面留白要充足，但不可為了裝飾而壓縮可用內容區域；小工具優先保持精簡、易讀。

## 3. Design Tokens

### 3.1 色彩系統 (Color System)

| Token 名稱 | 十六進位值 (HEX) | Compose Color 定義 | 用途說明 |
|---|---|---|---|
| `Surface` | `#FBF9F3` | `Color(0xFFFBF9F3)` | 應用程式全域背景、紙本主色 |
| `SurfaceContainerLow` | `#F5F4ED` | `Color(0xFFF5F4ED)` | 次級紙質容器、篩選器背景 |
| `SurfaceContainer` | `#F0EFE8` | `Color(0xFFF0EFE8)` | 搜尋輸入框、未選取膠囊背景 |
| `SurfaceContainerHigh` | `#EBE8DF` | `Color(0xFFEBE8DF)` | 彈窗遮罩底層、點擊選取暫態 |
| `SurfaceContainerLowest` | `#FFFFFF` | `Color(0xFFFFFFFF)` | 收藏卡片主背景、輸入欄位底色 |
| `OnSurface` | `#2B2C28` | `Color(0xFF2B2C28)` | 主文字、文章標題、主圖示 |
| `OnSurfaceVariant` | `#767871` | `Color(0xFF767871)` | 次要文字、日期、來源平台標記、字數提示 |
| `Outline` | `#E6E2D8` | `Color(0xFFE6E2D8)` | 卡片邊框、分隔線、輸入框框線 (1dp) |
| `OutlineVariant` | `#D6D3C8` | `Color(0xFFD6D3C8)` | 強化邊框、微型控制項邊界 |
| `Primary` | `#5E715D` | `Color(0xFF5E715D)` | 鼠尾草綠：主操作按鈕、已選中標籤、重點指示 |
| `OnPrimary` | `#FBF9F3` | `Color(0xFFFBF9F3)` | 主操作按鈕內文字與圖示色 |
| `PrimaryContainer` | `#E3E9E1` | `Color(0xFFE3E9E1)` | 淺綠色輔助底色、標籤選取微底 |
| `OnPrimaryContainer`| `#2A3529` | `Color(0xFF2A3529)` | 標籤選取文字色 |
| `Secondary` | `#B88578` | `Color(0xFFB88578)` | 陶土棕 / 灰粉陶土色：自訂新標籤、提示圓點、日期章 |
| `SecondaryContainer`| `#F7EBE8` | `Color(0xFFF7EBE8)` | 淺陶土粉背景（如已選自訂標籤背景） |
| `OnSecondaryContainer`| `#5E3027` | `Color(0xFF5E3027)` | 陶土色標籤文字 |
| `Error` | `#BA1A1A` | `Color(0xFFBA1A1A)` | 刪除警示文字、垃圾桶強調色 |
| `ErrorContainer` | `#FFDAD6` | `Color(0xFFFFDAD6)` | 刪除警示徽章背景 |
| `Scrim` | `#000000` (40%~60%)| `Color(0x66000000)`| 底部抽屜或 Dialog 展開時的背景壓暗遮罩 |

### 3.2 字體系統 (Typography System)

字體族群配置：
- **人文襯線字體**：優先使用 `Noto Serif TC` / `Noto Serif CJK`，用於品牌「聚藏」、頁面大標題與收藏標題。
- **現代無襯線字體**：優先使用 `Noto Sans TC`，必要時使用 Android 系統預設 Sans Serif，供正文、標籤、來源、日期、按鈕與輸入欄使用。
- **手帳引言字體**：若專案確實提供相容字型資源，可在「今日拾藏」引言使用楷書；若無可靠字型資源，改用襯線字體，不可依賴裝置上不一定存在的字型。

| Token | 字級 (sp) | 字重 (FontWeight) | 行高 (sp) | 字型族群 | 對應元件 |
|---|---|---|---|---|---|
| `HeadlineLarge` | 24sp | Bold (700) | 32sp | Serif | 首頁標題「聚藏」 |
| `HeadlineMedium` | 20sp | SemiBold (600) | 28sp | Serif | 頁面大標題（「新增典藏」、「今日拾藏」） |
| `TitleLarge` | 18sp | Medium (500) | 26sp | Serif | 彈窗標題、空分類夾提示大標 |
| `TitleMedium` | 15sp~16sp | SemiBold (600) | 22sp | Sans/Serif | 收藏卡片文章標題（神保町古書店...） |
| `BodyLarge` | 14sp | Normal (400) | 22sp | Sans | 札記內文、說明對話框正文 |
| `BodyMedium` | 13sp | Normal (400) | 18sp | Sans | 收藏摘要內文、分類名稱 (篇數) |
| `BodySmall` | 12sp | Normal (400) | 16sp | Sans/Serif | 桌面小工具文字、備註摺疊預覽 |
| `LabelLarge` | 14sp | Medium (500) | 20sp | Sans | 主要動作按鈕（「儲存」、「完成管理」） |
| `LabelMedium` | 12sp | Medium (500) | 16sp | Sans | 標籤膠囊（「#生活提案」）、來源平台膠囊 |
| `LabelSmall` | 11sp | Normal (400) | 14sp | Sans | URL 來源、日期戳記、字數計數器（38/500） |
| `QuoteCalligraphy`| 14sp~15sp | Normal (400) | 24sp | Kaiti | 今日拾藏手帳引言「重讀舊時光」 |

### 3.3 幾何與維度系統 (Shapes, Elevation & Spacing)

- **圓角半徑 (Corner Radius)**：
    - 微圓角 `Small`：`4dp`（URL 框、微按鈕標籤）
    - 容器圓角 `Medium`：`8dp` ~ `12dp`（文章卡片封面圖、輸入框、小型彈窗卡片）
    - 卡片圓角 `Large`：`16dp`（主收藏卡片容器、分類夾卡片、桌面 Widget 外框）
    - 抽屜頂部圓角 `ExtraLarge`：`20dp` ~ `24dp`（Bottom Sheet 頂部圓角 `rounded-t-2xl`）
    - 膠囊完全圓角 `Full`：`999dp`（標籤 Chip、平台識別按鈕、圓形返回鍵）
- **間距尺度 (Spacing)**：
    - `SpacingXXS`: `2dp`
    - `SpacingXS`: `4dp`
    - `SpacingS`: `8dp`
    - `SpacingM`: `12dp`
    - `SpacingL`: `16dp`（標準水平頁面 Padding）
    - `SpacingXL`: `20dp` ~ `24dp`（區塊垂直間距）
    - `SpacingXXL`: `32dp`（區塊主間隔）
- **邊框與陰影 (Borders & Shadows)**：
    - 邊框線寬：標準一律為 `1dp`，使用 `Color(0xFFE6E2D8)`。
    - 陰影（Elevation）：遵循低對比柔和紙本風。一般卡片 `0dp` ~ `1dp`；底部抽屜 / 對話框採用 `Elevation 6dp` ~ `8dp` 搭配 `Color(0x1A000000)` 擴散陰影。
- **圖示風格與規格 (Icons)**：
    - 線條風格（Outlined Stroke 1.5dp ~ 1.75dp），端點圓潤（Round Cap）。
    - 返回鍵統一使用 Chevron Left：`<` 造型（20dp ~ 24dp 觸控熱區 40dp）。
    - 操作圖示：直立式三點 `⋮`（More Vert）、書本圖示（Open Book）、橡皮擦（Eraser）、垃圾桶（Trash）、外部連結（Link / Chain）。

---

## 4. Screen Specifications

本專案 Screens 採模組化狀態梳理，依照核心架構與各流程具體規格整理如下：

---

### 4.1 典藏新增與編輯模組

#### Screen: `新增與編輯收藏 (常規表單檢視)` (SCREEN_71 / SCREEN_69)
- **用途**：貼入或分享文章後，檢視擷取成果並設定分類、標籤與札記之核心表單。
- **版面結構**：
    1. **頂部操作列**：
        - 左側：`<` 返回圖示按鈕（點擊取消/退出）。
        - 中央標題：`新增典藏`（20sp SemiBold Serif）。
        - 右側：`儲存` 主要操作實心圓角按鈕（背景 `#5E715D`，文字白色，Padding 8dp 16dp）。
    2. **卡片一：文章預覽與來源**：
        - 頂部：封面圖片（16:9 或 2:1，圓角 12dp，含「已萃取文章封面與摘要」半透明膠囊標籤）。
        - 來源網址 URL 欄位：文字輸入框（含「重新解析」快捷按鈕）。
        - 收藏分類夾列：顯示當前指定資料夾，預設為「未分類 (18 篇收藏)」，右側帶有展開箭頭 `>`，點擊後展開底部選擇分類夾抽屜。
    3. **卡片二：中繼資料與個人札記**：
        - 來源平台識別：橫向單選膠囊（Threads、Instagram、Dcard、YouTube 等，選中項為深色墨底白字）。
        - 文章或影音標題：多行輸入框（右上角附「清空」按鈕）。
        - 個人拾藏札記：可多行輸入的手帳文字區（右上角附橡皮擦圖示「備註」按鈕，右下角帶「48 字」計數）。
        - 索引標籤分類：
            - 標題列：左側「索引標籤分類」，右側提示圖標（點擊彈出說明氣泡：說明標籤來自目前分類夾，亦可自訂），最右側顯示配額「已選 2 個 (上限 5 個)」。
            - 標籤列：已選中標籤（桃粉底色帶 `×` 取消）、可選標籤（`+ 閱讀清單` 等）、`+ 自訂新標籤` 按鈕。
    4. **卡片三：每日回顧設定**：
        - 「加入今日拾藏備選庫」：左側日曆圖示與文案，右側配置 Material 3 Switch 開關（預設開啟）。

#### Screen: `新增與編輯收藏 - 自訂新標籤彈出視窗` (SCREEN_40)
- **用途**：點擊「自訂新標籤」後喚起的底部抽屜輸入介面。
- **主要元件**：
    - 貼底半透明遮罩背景。
    - Bottom Sheet：頂部手柄條、標題「自訂新標籤」、輸入框（提示字：「輸入標籤名稱，例如：咖啡探索」）、字數限制指示、「確認新增」主要按鈕與「取消」次要按鈕。

#### Screen: `新增與編輯收藏 - 已建立自訂標籤狀態` (SCREEN_38)
- **用途**：自訂標籤新增完成後，該標籤立即呈現在索引標籤區並預設為選取狀態。
- **元件特徵**：
    - 新增之自訂標籤右側附帶微型「垃圾桶 🗑」圖示按鈕，使用者可直接點擊刪除該自訂標籤。

---

### 4.2 收藏分類夾與首頁模組 (Home & Taxonomy)

#### Screen: `V3 - Home 分類頁（多個分類）` (SCREEN_106 / SCREEN_20)
- **用途**：App 核心主頁面，展示今日拾藏推薦橫幅、所有分類夾與管理入口。
- **版面結構**：
    1. **頂部品牌列**：
        - 左側：使用者頭像按鈕（圓形，點擊開啟帳號資訊彈窗）。
        - 標題：`聚藏`（Serif HeadlineLarge）。
        - 右側：搜尋按鈕（放大鏡圖示）與 `+ 新增收藏` 實心操作鈕。
    2. **今日拾藏推薦橫幅 (Daily Pick Banner)**：
        - 暖色紙本卡片，左側深綠裝飾書背條。
        - 標題：「今日拾藏 · 3 則精選已抽出」。
        - 副標：「沉澱數位碎片，翻開為你採選的安靜篇章。」，點擊引導進入今日拾藏清單。
    3. **分類夾清單區塊**：
        - 分類標題列：左側「收藏分類夾 (3 個分類)」，右側「網格 / 條列切換開關」及「管理」按鈕。
        - 分類卡片（網格雙欄）：
            - 上方：封面照片（圓角 12dp）。
            - 下方：分類名稱與篇數（如「工作 (21)」、「未分類 (18)」；若為 0 篇則顯示「0」或特殊狀態）。
            - *長名稱顯示規範*：超過寬度時支援兩行優雅折行（如 SCREEN_20 實作）。

#### Screen: `V3 - Home 分類頁 (條列式顯示)` (SCREEN_16)
- **用途**：分類夾切換至條列清單檢視模式。
- **主要元件**：
    - 每個項目為單行/雙行卡片：左側 48dp 方形縮圖，中央為分類標題與篇數，右側附資料夾圖示與導引箭頭。

#### Screen: `V3 - Home 分類頁 - 點擊空分類夾提示` (SCREEN_14 / SCREEN_12)
- **用途**：使用者點擊 0 篇收藏的分類夾時的防呆提示。
- **兩種實作模式**：
    1. *中央對話框 (SCREEN_14)*：置中彈窗，文案說明「此分類夾尚無任何收藏」，附「＋ 立即新增收藏」與「稍後再說」按鈕。
    2. *底部抽屜 (SCREEN_12)*：貼底 Bottom Sheet，同樣提供直觀的新增引導。

#### Screen: `V3 - Home 分類頁 - 使用者帳號彈出視窗` (SCREEN_22)
- **用途**：點擊首頁頭像後的帳號設定彈窗。
- **主要元件**：
    - 置中卡片：頭像預覽（附相機圖示，支援自選圖片）、使用者名稱、Email、同步狀態、「登出帳號」按鈕及「關閉」按鈕。

---

### 4.3 收藏分類夾管理流程

#### Screen: `收藏分類夾管理 - 列表與編輯狀態` (SCREEN_51)
- **用途**：點擊首頁「管理」按鈕後喚起的分類夾維護面板。
- **主要元件**：
    - 置頂按鈕：`＋ 新增自訂分類夾`。
    - 分類夾列表：依最新收藏時間自動排序（未分類 0 篇時不顯示）。
    - 單元項目：分類名稱、右側編輯筆圖示（點擊後名稱變為輸入框並附帶打勾儲存按鈕）、刪除垃圾桶圖示。
    - 底部操作：`完成管理 ✓` 主要按鈕。

#### Screen: `收藏分類夾管理 - 新增自訂分類夾狀態` (SCREEN_46 / SCREEN_44)
- **用途**：點擊新增後於列表頂端展開輸入欄位，儲存後可接續新增。

#### Screen: `收藏分類夾管理 - 刪除分類夾確認對話框` (SCREEN_49 / SCREEN_42)
- **用途**：刪除分類夾時的資料流向防呆。
- **邏輯分支**：
    1. **有內容分類夾 (SCREEN_49)**：提供兩選項單選 Radio：
        - 選項 A：將所有內容移至「未分類」。
        - 選項 B：將分類夾內所有收藏一併刪除。
    2. **0 篇內容之空分類夾 (SCREEN_42)**：免除移動選項，直接確認「確定刪除此空分類夾？」。

---

### 4.4 收藏清單與篩選檢索模組 (Bookmark Detail)

#### Screen: `BookmarkDetail - 一般收藏清單` (SCREEN_101)
- **用途**：點擊進入分類夾後的完整收藏清單。
- **版面結構**：
    - 頂部導覽：左側 `<` 返回鍵，標題「未分類」（附文章總計），右側「批量管理」文字按鈕。
    - 檢索欄：內置「搜尋這個收藏夾的內容...」。
    - 標籤篩選列：圓角微卡片容器，包含「標籤篩選」標題、資訊圖示（點擊彈出說明氣泡）、橫向滑動標籤列表。
    - 排序狀態列：顯示「共 18 篇收藏」、右側「⇅ 時間排序」。
    - 收藏列表卡片：日系紙本卡片（左側方形 72dp 縮圖、來源與日期、粗體文章標題、一則摺疊備註/URL、標籤膠囊、右上角直立式三點 `⋮` 操作鈕）。

#### Screen: `BookmarkDetail - 折疊式標籤篩選（OR）(展開 / 收合狀態)` (SCREEN_85 / SCREEN_87)
- **用途**：解決標籤過多問題的垂直摺疊篩選面板。
- **展開狀態 (SCREEN_85)**：標籤以 Flex/FlowRow 完整鋪開多行展示，右上方提供「收合 ⌃」與「清除」按鈕。
- **收合狀態 (SCREEN_87)**：僅保留單行預覽，右上方提供「展開 ⌄」按鈕。

#### Screen: `BookmarkDetail - 批量管理模式` (SCREEN_103)
- **用途**：點擊「批量管理」後的勾選管理狀態。
- **主要元件**：
    - 頂部：左側 `<` 返回鍵，標題「未分類」，右側「完成」綠色按鈕。
    - 列表卡片左側：顯示圓形核取方塊（Checkbox，選中為墨綠底白勾）。
    - 貼底操作列：橫向平分三功能——`☑ 全選`、`🗑 刪除`、`↪ 移動到...`。

#### Screen: `BookmarkDetail - 點擊更多按鈕下拉選單` (SCREEN_36)
- **用途**：點擊卡片右上角 `⋮` 展開之 Floating Menu。
- **選單選項**：
    1. `編輯標題`（附鉛筆圖示）
    2. `編輯筆記`（附札記圖示）
    3. `編輯標籤`（附 Tag 圖示）
    4. `刪除這則收藏`（紅色警示字體與垃圾桶圖示）

#### Screen: 單則收藏專屬編輯抽屜組 (SCREEN_34, SCREEN_28, SCREEN_24, SCREEN_26)
- **編輯標題 (SCREEN_34)**：底部抽屜，標題預覽與輸入框（帶字數限制 12/60），「✓ 儲存標題變更」與「取消」。
- **編輯筆記 (SCREEN_28)**：底部抽屜，右上角橡皮擦「清空」鈕，多行輸入框（字數指示 38/500），「儲存筆記內容」。
- **編輯標籤 (SCREEN_24)**：底部抽屜，選取/反選標籤，上限 5 個標籤計數，「儲存標籤設定」。
- **刪除這則收藏 (SCREEN_26)**：底部抽屜，文章預覽卡片，醒目紅色/陶土色「確認刪除這則收藏」按鈕與「取消，保留收藏」。

---

### 4.5 搜尋與空狀態模組 (SearchScreen)

#### Screen: `SearchScreen - 整合篩選與簡化文案` 系列 (SCREEN_66, 64, 62, 60, 56, 58)
- **用途**：全域全文搜尋與複合條件檢索。
- **主要元件**：
    - 頂部：`<` 返回鍵 + 搜尋框（帶清除 `×` 鈕）。
    - 複合篩選面板：整合「來源平台」（Instagram/Threads 等）與「收藏分類夾」。
    - 空狀態設計：
        - *找不到符合結果 (SCREEN_58 / SCREEN_60)*：日系素雅手繪書本插圖，文案：「找不到符合的收藏」、「嘗試調整關鍵字或清除篩選條件」。
        - *尚無任何收藏 (SCREEN_56)*：引導使用者新增第一筆收藏，附帶「🧭 瀏覽靈感推薦」按鈕。

---

### 4.6 今日拾藏模組 (Daily Pick)

#### Screen: `今日拾藏 Daily Pick` (SCREEN_177)
- **用途**：每日抽樣 3 則收藏之專屬沈浸閱讀頁。
- **版面結構**：
    - 頂部：左側 `<` 返回鍵，標題 `今日拾藏`（20sp Serif）。
    - 今日手帳引言卡：楷書字體顯示「重讀舊時光」，搭配每日一句引言。
    - 3 則收藏卡片：每則包含平台圖示、收藏天數（如「12 天前」）、封面縮圖、標題、摘要。
    - 卡片底部：左側鏈結圖示與原網址 URL（單行省略），可點擊外連跳轉。
    - 底部操作：置底 `換一組推薦` 次要輪播按鈕。

---

### 4.7 帳號認證模組 (Authentication)

#### Screen: `聚藏 - 登入畫面` (SCREEN_10) / `註冊畫面` (SCREEN_8) / `忘記密碼畫面` (SCREEN_6) / `驗證電子信箱 (V2)` (SCREEN_2)
- **視覺規範**：無 Toolbar，頂部預留安全留白；正上方置入品牌標誌與「聚藏」書香印記。
- **欄位與操作**：
    - 輸入欄位使用圓角 8dp、極細邊框與柔和米白底色。
    - 第三方登入**僅保留 Google 快速登入按鈕**（白底極細邊框，置中 Google G-Logo）。
    - 主要動作按鈕採用全幅深綠按鈕（`#5E715D`）。
    - 驗證碼頁面（SCREEN_2）配置 6 碼分格輸入方塊與重發倒數計時器。

---

### 4.8 桌面小工具模組 (App Widget)

#### Screen: `Android 桌面小工具 Widget` (SCREEN_134 及後續版本)
- **用途**：Android 主畫面上的精簡回顧入口，點擊卡片可開啟 App 的「今日拾藏」頁。
- **優先版型**：以較矮、單張卡片的緊湊小工具為主，避免三列堆疊造成過高、佔用過多桌面空間。實際尺寸依 Android Launcher 可用網格與 Jetpack Glance 限制調整，不把特定 4x2 / 4x1 尺寸視為所有裝置都固定成立。
- **內容**：
    1. 小型品牌識別「聚藏」與必要的輕量操作。
    2. 顯示當日固定挑選的 3 則收藏標題，標題字級小、視覺權重低，最多以單行省略呈現；不可只顯示「3 則收藏等你回顧」這類泛用提示取代實際標題。
    3. 提供「換一組」操作，讓使用者手動更換當日推薦組合。
- **互動**：點擊收藏標題或卡片主體進入「今日拾藏」；「換一組」只更新推薦組合，不應意外開啟收藏詳情。
- **視覺**：暖米白紙面、1dp 低對比邊線、微弱暖調陰影；避免厚重裝飾、過多文字或過高卡片。
- **資料規則**：每日預設挑選 3 則；同一天內維持目前組合，除非使用者主動選擇「換一組」。若收藏數不足 3 則，顯示實際可用數量，不虛構資料。
---

## 5. Reusable Components

Codex 實作時應將下列元件抽取為獨立 Composable：

### 5.1 `JuCangBookmarkCard` (收藏清單卡片)
- **參數**：`title: String`, `sourcePlatform: String`, `date: String`, `imageUrl: String?`, `note: String?`, `url: String`, `tags: List<String>`, `onMoreClick: () -> Unit`, `onCardClick: () -> Unit`
- **外觀特徵**：純白底（#FFFFFF）、圓角 12dp、邊框 1dp（#E6E2D8）。左側 72dp 方形縮圖（圓角 8dp），右側垂直排列來源資訊、標題、標籤與更多按鈕（`⋮`）。

### 5.2 `JuCangTagChip` (標籤膠囊)
- **變體狀態**：
    - `Default`（未選中）：米白底 `#F0EFE8`、深灰字 `#2B2C28`。
    - `Selected`（已選中）：墨綠底 `#5E715D`、米白字 `#FBF9F3`。
    - `CustomSelected`（已選自訂標籤）：淺陶土粉底 `#F7EBE8`、紅棕字 `#5E3027`、附帶刪除垃圾桶圖示。
    - `AddAction`（自訂新標籤）：虛線或細線框、附 `+` 圖示。

### 5.3 `JuCangBottomSheetContainer` (抽屜外殼)
- **外觀**：頂部圓角 20dp（`rounded-t-2xl`）、背景 `#FBF9F3`、頂部置中 32dp×4dp 灰色拖曳手柄（Drag Handle）、最高層級 Scrim 遮罩（覆蓋全螢幕與底欄）。

### 5.4 `JuCangSearchInputBar` (搜尋欄)
- **外觀**：高度 44dp ~ 48dp、圓角 999dp（Pill）或 12dp、前綴放大鏡圖示、提示文字「搜尋這個收藏夾的內容...」、後綴清除 `×` 按鈕。

### 5.5 `JuCangPrimaryButton` / `JuCangSecondaryButton`
- **Primary**：墨綠實心背景 `#5E715D`、文字 `#FBF9F3`、高度 48dp、圓角 8dp ~ 12dp。
- **Secondary**：純白或米白底、1dp 邊框 `#E6E2D8`、文字 `#2B2C28`。

---

## 6. Navigation and Interactions

### 6.1 導覽關係圖 (Navigation Flow)

```
[桌面 Widget (SCREEN_134)]
   ├── 點擊「新增書籤 ＋」 ───────> [新增典藏 (SCREEN_71)]
   └── 點擊「回顧清單」 ─────────> [今日拾藏 (SCREEN_177)]

[首頁 分類頁 (SCREEN_106 / SCREEN_16)]
   ├── 點擊「頭像」 ─────────────> [帳號彈窗 (SCREEN_22)] ──> [登出/登入 (SCREEN_10)]
   ├── 點擊「+ 新增收藏」 ────────> [新增典藏 (SCREEN_71)]
   │                                  ├── 點擊「選擇分類夾」 ──> [分類抽屜 (SCREEN_73)]
   │                                  └── 點擊「自訂新標籤」 ──> [標籤抽屜 (SCREEN_40)]
   ├── 點擊「今日拾藏橫幅」 ──────> [今日拾藏 (SCREEN_177)]
   ├── 點擊「管理」 ─────────────> [分類管理 (SCREEN_51)]
   │                                  ├── 點擊「新增分類」 ────> [新增分類 (SCREEN_46)]
   │                                  └── 點擊「刪除分類」 ────> [刪除確認 (SCREEN_49/42)]
   ├── 點擊「空分類夾 (0篇)」 ────> [空分類提示 (SCREEN_14/12)]
   └── 點擊「特定分類夾」 ────────> [收藏清單 (SCREEN_101)]
                                      ├── 點擊「批量管理」 ───> [批量管理 (SCREEN_103)]
                                      ├── 點擊「篩選展開」 ───> [標籤展開 (SCREEN_85)]
                                      └── 點擊「⋮ 更多」 ────> [更多選單 (SCREEN_36)]
                                                                  ├── 編輯標題 (SCREEN_34)
                                                                  ├── 編輯筆記 (SCREEN_28)
                                                                  ├── 編輯標籤 (SCREEN_24)
                                                                  └── 刪除收藏 (SCREEN_26)
```

### 6.2 互動元件預期行為表
| 觸發元件 | 互動手勢 | 預期反應 | 備註 / 狀態確認 |
|---|---|---|---|
| 卡片右上角 `⋮` | 單擊 (Click) | 於點擊位置彈出 Popover / Floating Menu | 包含 4 項功能 |
| 標籤篩選 Chip | 單擊 (Click) | 切換選取狀態，列表即時過濾文章 | 支援多選 OR 邏輯 |
| 標籤篩選「展開/收合」| 單擊 (Click) | 動態展開多行 FlowRow 或收合為單行 | 動態調整列表上方高度 |
| 批量管理「全選」 | 單擊 (Click) | 將畫面內所有文章 Checkbox 設為選中 | 同步更新計數 |
| 札記文字備註 | 單擊 (Click) | 於卡片內展開完整多行文字（或收合） | 支援摺疊切換 |
| 外部 URL 列 | 單擊 (Click) | 喚起系統預設瀏覽器（Chrome Custom Tabs） | 開啟原始文章連結 |

---

## 7. Android Implementation Guidelines (Jetpack Compose)

### 7.1 Compose 專案結構架構建議
```
com.jucang.bookmark/
├── ui/
│   ├── theme/
│   │   ├── Color.kt             // 3.1 色彩定義
│   │   ├── Type.kt              // 3.2 字型與字級定義 (Noto Serif / Kaiti)
│   │   ├── Shape.kt             // 3.3 圓角尺度
│   │   └── Theme.kt             // Material 3 主題注入
│   ├── components/              // 第 5 章通用元件
│   │   ├── JuCangBookmarkCard.kt
│   │   ├── JuCangTagChip.kt
│   │   ├── JuCangBottomSheetContainer.kt
│   │   └── JuCangSearchInputBar.kt
│   ├── screens/                 // 各獨立 Screen 實作
│   │   ├── home/                // 首頁分類與管理
│   │   ├── detail/              // 收藏清單與批量管理
│   │   ├── editor/              // 新增與編輯表單
│   │   ├── dailypick/           // 今日拾藏
│   │   ├── search/              // 檢索與空狀態
│   │   └── auth/                // 身分驗證流程
│   └── widget/                  // Jetpack Glance 桌面小工具
│       └── JuCangHomeWidget.kt
```

### 7.2 Theme 映射程式碼範例

```kotlin
// ui/theme/Color.kt
package com.jucang.bookmark.ui.theme

import androidx.compose.ui.graphics.Color

val SurfaceWarm = Color(0xFFFBF9F3)
val SurfaceContainerLowWarm = Color(0xFFF5F4ED)
val SurfaceContainerWarm = Color(0xFFF0EFE8)
val SurfaceContainerLowestPure = Color(0xFFFFFFFF)
val TextInkDark = Color(0xFF2B2C28)
val TextMuted = Color(0xFF767871)
val OutlineFine = Color(0xFFE6E2D8)
val SageGreenPrimary = Color(0xFF5E715D)
val TerracottaSecondary = Color(0xFFB88578)
val TerracottaContainer = Color(0xFFF7EBE8)

// ui/theme/Theme.kt
private val LightColorScheme = lightColorScheme(
    primary = SageGreenPrimary,
    onPrimary = SurfaceWarm,
    secondary = TerracottaSecondary,
    onSecondary = Color.White,
    secondaryContainer = TerracottaContainer,
    background = SurfaceWarm,
    surface = SurfaceWarm,
    onSurface = TextInkDark,
    surfaceVariant = SurfaceContainerWarm,
    onSurfaceVariant = TextMuted,
    outline = OutlineFine
)
```

### 7.3 開發遵循規範
1. **嚴禁全畫面放在單一檔案**：每個 Screen 及其專屬 Dialog / BottomSheet 應獨立成檔。
2. **遵守無 Toolbar 設計**：頂部使用 `Modifier.statusBarsPadding()`，返回按鈕整合於大標題左側 Row 內。
3. **圖片載入**：使用 Coil 函式庫，並在非同步載入時提供純色 `#F5F4ED` 作為紙本色 Placeholder。

---

## 8. Responsive Layout and Accessibility

### 8.1 響應式佈局原則 (不同螢幕尺寸)
- **緊湊型手機 (Compact, < 600dp 寬)**：
    - 分類夾首頁預設採雙欄網格（GridCells.Fixed(2)），水平 Padding 為 16dp。
    - 收藏文章卡片一律為單欄垂直列表，固定左側 72dp 縮圖，右側彈性填滿。
- **摺疊螢幕與平板 (Medium / Expanded, ≥ 600dp 寬)**：
    - 分類夾首頁升級為 3 ~ 4 欄網格。
    - 表單與對話框設定最大寬度約束（`Modifier.widthIn(max = 560dp)`），避免過度拉伸失真。

### 8.2 文字超出與折行規範
- **長標題**：收藏卡片標題最多顯示 2 行，超出部分使用 `TextOverflow.Ellipsis` 截斷。
- **長網址**：單行顯示，靠左對齊，超出部分強制截斷（`maxLines = 1, overflow = TextOverflow.Ellipsis`）。
- **個人筆記**：清單中預設收合至 1 行，點擊後展開全部；編輯器中限制 500 字，動態顯示即時字數計數。

### 8.3 無障礙 (Accessibility) 與觸控熱區
- **最小觸控面積**：所有圖標按鈕（如 `<` 返回鍵、`⋮` 更多按鈕、標籤 `×` 按鈕）之視覺大小雖可為 20dp ~ 24dp，但外部必須配置最少 **48dp × 48dp** 的點擊熱區（可透過 `Modifier.minimumInteractiveComponentSize()` 達成）。
- **語意標籤 (Content Description)**：所有向量 Icon（返回鍵、更多、橡皮擦、垃圾桶）均須提供精確中文語意標記，供 TalkBack 報讀。

---

## 9. Design Constraints and Open Questions

### 9.1 設計稿已明確鎖定之規範（不可任意變更）
1. **配色不可任意更動**：全域背景使用 `#FBF9F3`，主文字使用 `#2B2C28`，主操作與選取色使用 `#5E715D`，陶土點綴使用 `#B88578`。所有元件以第 3 章 Design Tokens 為準。
2. **無頂部 Toolbar**：嚴禁加入 Android 傳統生硬之固定 TopAppBar，必須採用畫面內標題整合返回鍵的做法。
3. **無全域底部導覽列**：主要功能頁面（表單、管理、清單、檢索）不設全域 Bottom Tab Bar。
4. **返回圖示外觀**：一律採用簡約 `<`（Chevron Left）外觀。
5. **卡片右上角操作圖示**：一律為直立式三點 `⋮`（More Vert）。
6. **標籤配額**：單則收藏最多僅能選取 **5 個標籤**。
7. **第三方登入**：僅保留 Google 登入選項。

### 9.2 待開發階段進一步確認之規格 (Open Questions)

| 項目 | 目前畫面現況 | 需確認之技術/產品規格 |
|---|---|---|
| **本機離線快取機制** | 畫面有展示封面縮圖與摘要 | 當離線時文章縮圖是否使用 Glance/Room 本機快取，或是顯示通用紙本佔位圖？ |
| **自動萃取服務 API** | 畫面有「已萃取文章封面與摘要」 | URL 解析係由手機端直接解析 HTML OpenGraph，抑或透過後端 API 服務爬取？ |
| **標籤移除連鎖效應** | 畫面提示「含此標籤的收藏若全數刪除，標籤亦會消失」 | 標籤是否完全由收藏動態彙整（Dynamic Derived），不單獨儲存標籤實體表？ |
| **今日拾藏抽取頻率** | 畫面為「今日拾藏 · 3 則」與「換一組」 | 換一組是否有次數限制？每日何時自動更新（如午夜 00:00）？ |
| **分類排序記憶** | 畫面規則為「有最新收藏的在最前」 | 是否需支援使用者自訂手動釘選特定分類（除預設未分類外）？ |
| **Widget 刷新機制** | 桌面小工具提供引言與待回顧提示 | Glance Widget 的 WorkManager 建議定時更新排程（如每 4 小時或每日定時）？ |


---

## 10. 統一規格優先順序

若本文件不同章節或舊版 Stitch 畫面出現差異，請依下列順序判斷：
1. 第 9.1 節的已鎖定產品決策與第 3 章統一 Design Tokens。
2. 各 Screen 的具體功能與互動規格。
3. 通用元件與視覺風格建議。
4. 舊版畫面中的裝飾性細節。

### 統一後的關鍵決策
- **主色**：`#5E715D`；不使用其他綠色作為另一套主色。
- **背景**：`#FBF9F3`；卡片可使用 `#FFFFFF`，次級容器使用第 3 章定義的暖灰米白。
- **陶土點綴色**：`#B88578`；只作次要強調，不與主色搶焦點。
- **導覽**：不使用全域底部導覽列；返回按鈕整合於頁面標題區。
- **圓角**：一般容器 4–12dp；大型卡片可依現有畫面使用 16dp，但不可全面使用膠囊外形。標籤 Chip 可例外使用膠囊形。
- **字體**：標題優先 Noto Serif TC；正文與操作優先 Noto Sans TC。楷書只在專案有可用字型資源時使用。
- **桌面 Widget**：以精簡、較矮的單卡片為主，顯示當日 3 則收藏的實際標題，提供手動「換一組」；不以高大的三列卡片或泛用提示文案作為必要設計。
- **待確認項目**：第 9.2 節的技術問題仍屬待確認事項，實作前需決定，不可當作已定案功能。
