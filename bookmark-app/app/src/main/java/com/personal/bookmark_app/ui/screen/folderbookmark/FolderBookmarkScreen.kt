package com.personal.bookmark_app.ui.screen.folderbookmark

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.ui.components.TextFieldView
import com.personal.bookmark_app.ui.mockdata.FolderDetailMockData
import com.personal.bookmark_app.ui.mockdata.FolderDetailMockData.availableTags

//@Composable
//fun FolderDetailScreen(
//    folderName: String = "未分類",
////    folderId: Long,
//    onBackClick: () -> Unit = {},
//    onBatchManageClick: () -> Unit = {},
//    onBookmarkClick: (String) -> Unit = {}
//) {
//    var uiState by remember {
//        mutableStateOf(FolderBookmarkUiState())
//    }
//
//    val bookmarks = FolderDetailMockData.bookmarks
//
//    // 搜尋與標籤篩選
//    val filteredBookmarks = remember(
//        bookmarks,
//        uiState.searchQuery,
//        uiState.selectedTags,
//        uiState.isNewestFirst
//    ) {
//        bookmarks
//            .filter { bookmark ->
//                val matchesSearch =
//                    uiState.searchQuery.isBlank() ||
//                            bookmark.title.contains(uiState.searchQuery, ignoreCase = true) ||
//                            bookmark.description.contains(uiState.searchQuery, ignoreCase = true) ||
//                            bookmark.source.contains(uiState.searchQuery, ignoreCase = true)
//
//                val matchesTags =
//                    uiState.selectedTags.isEmpty() ||
//                            uiState.selectedTags.all { tag ->
//                                tag in bookmark.tags
//                            }
//
//                matchesSearch && matchesTags
//            }
//            .let { result ->
//                if (uiState.isNewestFirst) {
//                    result.sortedByDescending { it.date }
//                } else {
//                    result.sortedBy { it.date }
//                }
//            }
//    }
//
//    Scaffold(
//        containerColor = MaterialTheme.colorScheme.background,
//        topBar = {
//            FolderDetailTopBar(
//                folderName = folderName,
//                onBackClick = onBackClick,
//                onBatchManageClick = {
//                    uiState = uiState.copy(
//                        isBatchManaging = !uiState.isBatchManaging
//                    )
//                    onBatchManageClick()
//                }
//            )
//        }
//    ) { innerPadding ->
//
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding),
//            contentPadding = PaddingValues(
//                start = 24.dp,
//                end = 24.dp,
//                top = 16.dp,
//                bottom = 28.dp
//            ),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//
//            // 搜尋欄位
//            item {
//                FolderSearchBar(
//                    value = uiState.searchQuery,
//                    onValueChange = {
//                        uiState = uiState.copy(searchQuery = it)
//                    }
//                )
//            }
//
//            // 標籤篩選
//            item {
//                FolderTagFilter(
//                    availableTags = FolderDetailMockData.availableTags,
//                    selectedTags = uiState.selectedTags,
//                    isExpanded = uiState.isTagFilterExpanded,
//                    onTagClick = { tag ->
//                        val newTags = uiState.selectedTags.toMutableSet()
//
//                        if (tag in newTags) {
//                            newTags.remove(tag)
//                        } else {
//                            newTags.add(tag)
//                        }
//
//                        uiState = uiState.copy(
//                            selectedTags = newTags
//                        )
//                    },
//                    onClearClick = {
//                        uiState = uiState.copy(
//                            selectedTags = emptySet()
//                        )
//                    },
//                    onExpandClick = {
//                        uiState = uiState.copy(
//                            isTagFilterExpanded =
//                                !uiState.isTagFilterExpanded
//                        )
//                    }
//                )
//            }
//
//            // 收藏數量與排序
//            item {
//                FolderBookmarkHeader(
//                    count = filteredBookmarks.size,
//                    isNewestFirst = uiState.isNewestFirst,
//                    onSortClick = {
//                        uiState = uiState.copy(
//                            isNewestFirst = !uiState.isNewestFirst
//                        )
//                    }
//                )
//            }
//
//            // 收藏清單
//            if (filteredBookmarks.isEmpty()) {
//                item {
//                    EmptyBookmarkView()
//                }
//            } else {
//                items(
//                    items = filteredBookmarks,
//                    key = { it.id }
//                ) { bookmark ->
//                    FolderBookmarkCard(
//                        item = bookmark,
//                        onClick = {
//                            onBookmarkClick(bookmark.id)
//                        }
//                    )
//                }
//            }
//        }
//    }
//}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderBookmarkScreen(
    folderName: String = "未分類",
    onBackClick: () -> Unit = {},
    onBatchManageClick: () -> Unit = {},
    onBookmarkClick: (String) -> Unit = {},
    onDeleteBookmarks: (List<String>) -> Unit = {},
    onMoveBookmarks: (List<String>, String) -> Unit = { _, _ -> }
) {
    // ------------------------------------
    // UI State
    // ------------------------------------

    var uiState by remember {
        mutableStateOf(FolderBookmarkUiState())
    }

    // 畫面收藏假資料
    var bookmarks by remember {
        mutableStateOf(FolderDetailMockData.bookmarks.toList())
    }

    // 已選取的收藏 ID
    var selectedBookmarkIds by remember {
        mutableStateOf<Set<String>>(emptySet())
    }

    // Dialog / BottomSheet
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var showMoveSheet by remember {
        mutableStateOf(false)
    }

    val moveSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // 目的資料夾假資料
    val availableFolders = remember(folderName) {
        listOf(
            "生活靈感", "稍後閱讀", "旅行清單", "美食收藏"
        ).filterNot { it == folderName }
    }

    // ------------------------------------
    // 收藏搜尋、篩選、排序
    // ------------------------------------

    val filteredBookmarks = remember(
        bookmarks,
        uiState.searchQuery,
        uiState.selectedTags,
        uiState.isNewestFirst
    ) {
        bookmarks
            .filter { bookmark ->

                val matchesSearch =
                    uiState.searchQuery.isBlank() ||
                            bookmark.title.contains(
                                uiState.searchQuery, ignoreCase = true
                            ) ||
                            bookmark.description.contains(
                                uiState.searchQuery, ignoreCase = true
                            ) ||
                            bookmark.source.contains(
                                uiState.searchQuery, ignoreCase = true
                            )

                val matchesTags =
                    uiState.selectedTags.isEmpty() ||
                            uiState.selectedTags.all { it in bookmark.tags }

                matchesSearch && matchesTags
            }
            .let { result ->
                if (uiState.isNewestFirst) {
                    result.sortedByDescending { it.date }
                } else {
                    result.sortedBy { it.date }
                }
            }
    }

    // ------------------------------------
    // 批量管理狀態
    // ------------------------------------

    // 目前顯示的收藏是否全部選取
    val isAllSelected =
        filteredBookmarks.isNotEmpty() &&
                filteredBookmarks.all { it.id in selectedBookmarkIds }

    // 已選取數量
    val selectedCount = selectedBookmarkIds.size

    // 切換單筆選取
    fun toggleBookmarkSelection(id: String) {
        selectedBookmarkIds =
            if (id in selectedBookmarkIds) {
                selectedBookmarkIds - id
            } else {
                selectedBookmarkIds + id
            }
    }

    // 全選 / 取消全選
    // toggle 切換
    fun toggleSelectAll() {
        selectedBookmarkIds =
            if (isAllSelected) {
                // 已經全選 → 清空
                emptySet()
            } else {
                // 尚未全選 → 全部勾選
                filteredBookmarks.map { it.id }.toSet()
            }
    }

    // ------------------------------------
    // 結束批量管理
    // ------------------------------------

    fun finishBatchManaging() {
        uiState = uiState.copy(isBatchManaging = false)
        selectedBookmarkIds = emptySet()
        showDeleteDialog = false
        showMoveSheet = false
    }

    // ------------------------------------
    // 刪除收藏
    // ------------------------------------

    fun deleteSelectedBookmarks() {
        val idsToDelete = selectedBookmarkIds.toList()
        if (idsToDelete.isEmpty()) return
        // 從目前畫面的資料移除
        bookmarks = bookmarks.filterNot { bookmark ->
            bookmark.id in selectedBookmarkIds
        }
        // 通知上層處理資料刪除
        onDeleteBookmarks(idsToDelete)
        finishBatchManaging()
    }

    // ------------------------------------
    // 移動收藏
    // ------------------------------------

    fun moveSelectedBookmarks(destination: String) {
        val idsToMove = selectedBookmarkIds.toList()
        if (idsToMove.isEmpty()) return
        // 從目前資料夾清單移除
        bookmarks = bookmarks.filterNot { bookmark ->
            bookmark.id in selectedBookmarkIds
        }
        // 通知上層處理資料夾歸屬
        onMoveBookmarks(idsToMove, destination)
        finishBatchManaging()
    }

    // ------------------------------------
    // Scaffold
    // ------------------------------------

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FolderDetailTopBar(
                folderName = folderName,
                isBatchManaging = uiState.isBatchManaging,
                onBackClick = {
                    if (uiState.isBatchManaging) {
                        finishBatchManaging()
                    } else {
                        onBackClick()
                    }
                },
                onBatchManageClick = {
                    if (uiState.isBatchManaging) {
                        finishBatchManaging()
                    } else {
                        uiState = uiState.copy(isBatchManaging = true)
                        selectedBookmarkIds = emptySet()
                        onBatchManageClick()
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.isBatchManaging) {
                BatchManageBottomBar(
                    isAllSelected = isAllSelected,
                    hasBookmarks = filteredBookmarks.isNotEmpty(),
                    hasSelection = selectedCount > 0,
                    onSelectAllClick = { toggleSelectAll() },
                    onDeleteClick = { showDeleteDialog = true },
                    onMoveClick = { showMoveSheet = true }
                )
            }
        }

    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 24.dp,
                end = 24.dp,
                top = 16.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 搜尋欄位
            item {
                FolderSearchBar(
                    value = uiState.searchQuery,
                    onValueChange = { query ->
                        uiState = uiState.copy(
                            searchQuery = query
                        )
                        // 搜尋條件變更，清除原本選取
                        selectedBookmarkIds = emptySet()
                    }
                )
            }

            // 標籤篩選
            item {
                FolderTagFilter(
                    availableTags = FolderDetailMockData.availableTags,
                    selectedTags = uiState.selectedTags,
                    isExpanded = uiState.isTagFilterExpanded,
                    onTagClick = { tag ->
                        val newTags =
                            uiState.selectedTags.toMutableSet()

                        if (tag in newTags) {
                            newTags.remove(tag)
                        } else {
                            newTags.add(tag)
                        }
                        uiState = uiState.copy(
                            selectedTags = newTags
                        )
                        selectedBookmarkIds = emptySet()
                    },
                    onClearClick = {
                        uiState = uiState.copy(
                            selectedTags = emptySet()
                        )
                        selectedBookmarkIds = emptySet()
                    },
                    onExpandClick = {
                        uiState = uiState.copy(
                            isTagFilterExpanded =
                                !uiState.isTagFilterExpanded
                        )
                    }
                )
            }

            // 收藏數量與排序
            item {
                FolderBookmarkHeader(
                    count = filteredBookmarks.size,
                    isNewestFirst = uiState.isNewestFirst,
                    onSortClick = {
                        uiState = uiState.copy(
                            isNewestFirst = !uiState.isNewestFirst
                        )
                    }
                )
            }

            // 收藏清單
            if (filteredBookmarks.isEmpty()) {

                item {
                    EmptyBookmarkView()
                }

            } else {
                items(
                    items = filteredBookmarks,
                    key = { it.id }
                ) { bookmark ->

                    FolderBookmarkCard(
                        item = bookmark,
                        isBatchManaging = uiState.isBatchManaging,
                        isSelected = bookmark.id in selectedBookmarkIds,

                        onSelectionClick = {
                            toggleBookmarkSelection(bookmark.id)
                        },

                        onClick = {
                            if (uiState.isBatchManaging) {
                                toggleBookmarkSelection(bookmark.id)
                            } else {
                                onBookmarkClick(bookmark.id)
                            }
                        },

                        // 編輯標題
                        onEditTitleClick = { newTitle ->
                            bookmarks = bookmarks.map { item ->
                                if (item.id == bookmark.id) {
                                    item.copy(title = newTitle)
                                } else {
                                    item
                                }
                            }
                        },

                        // 編輯筆記
                        onEditNoteClick = { newNote ->
                            bookmarks = bookmarks.map { item ->
                                if (item.id == bookmark.id) {
                                    item.copy(note = newNote)
                                } else {
                                    item
                                }
                            }
                        },

                        // 編輯標籤
                        availableTags = availableTags,
                        onTagsSave = { newTags ->
                            bookmarks = bookmarks.map { item ->
                                if (item.id == bookmark.id) {
                                    item.copy(tags = newTags)
                                } else {
                                    item
                                }
                            }
                        },

                        // 刪除收藏
                        onDeleteClick = {
                            bookmarks = bookmarks.filterNot { item ->
                                item.id == bookmark.id
                            }
                        }
                    )
                }
            }
        }
    }

    // ------------------------------------
    // 刪除確認 Dialog
    // ------------------------------------

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text(
                    text = "刪除收藏",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = "確定要刪除選取的 $selectedCount 筆收藏嗎？\n刪除後將無法復原。",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteSelectedBookmarks()
                    }
                ) {
                    Text(
                        text = "刪除",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("取消")
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // ------------------------------------
    // 移動收藏 BottomSheet
    // ------------------------------------

    if (showMoveSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showMoveSheet = false
            },
            sheetState = moveSheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            MoveBookmarkSheet(
                selectedCount = selectedCount,
                folders = availableFolders,
                onFolderClick = { destination ->
                    moveSelectedBookmarks(destination)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FolderDetailTopBar(
    folderName: String,
    isBatchManaging: Boolean,
    onBackClick: () -> Unit,
    onBatchManageClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = folderName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },

        navigationIcon = {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回"
                )
            }
        },

        actions = {
            Surface(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBatchManageClick),

                color = if (isBatchManaging) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },

                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 8.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (!isBatchManaging) {
                        Icon(
                            imageVector = Icons.Default.CheckBox,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = if (isBatchManaging) "完成" else "批量管理",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isBatchManaging) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun FolderDetailTopBar(
//    folderName: String,
//    onBackClick: () -> Unit,
//    onBatchManageClick: () -> Unit
//) {
//    TopAppBar(
//        title = {
//            Text(
//                text = folderName,
//                style = MaterialTheme.typography.titleLarge,
//                fontWeight = FontWeight.Bold
//            )
//        },
//        navigationIcon = {
//            IconButton(
//                onClick = onBackClick
//            ) {
//                Icon(
//                    imageVector = Icons.Default.ArrowBack,
//                    contentDescription = "返回"
//                )
//            }
//        },
//        actions = {
//            Surface(
//                modifier = Modifier
//                    .padding(end = 16.dp)
//                    .clip(CircleShape)
//                    .clickable(
//                        onClick = onBatchManageClick
//                    ),
//                color = MaterialTheme.colorScheme.surfaceVariant,
//                shape = CircleShape
//            ) {
//                Row(
//                    modifier = Modifier.padding(
//                        horizontal = 12.dp,
//                        vertical = 8.dp
//                    ),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(6.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.CheckBox,
//                        contentDescription = null,
//                        modifier = Modifier.size(18.dp)
//                    )
//
//                    Text(
//                        text = "批量管理",
//                        style = MaterialTheme.typography.labelLarge
//                    )
//                }
//            }
//        },
//        colors = TopAppBarDefaults.topAppBarColors(
//            containerColor = MaterialTheme.colorScheme.background
//        )
//    )
//}

@Composable
private fun FolderSearchBar(
    value: String,
    onValueChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 12.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "搜尋",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TextFieldView(
                value = value,
                onValueChange = onValueChange,
                placeholder = "搜尋這個收藏夾的內容...",
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FolderTagFilter(
    availableTags: List<String>,
    selectedTags: Set<String>,
    isExpanded: Boolean,
    onTagClick: (String) -> Unit,
    onClearClick: () -> Unit,
    onExpandClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "標籤篩選",
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "標籤篩選說明",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.weight(1f))

                if (selectedTags.isNotEmpty()) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "已選 ${selectedTags.size} 個標籤",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "清除",
                    modifier = Modifier.clickable(
                        enabled = selectedTags.isNotEmpty(),
                        onClick = onClearClick
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selectedTags.isNotEmpty()) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )

                Spacer(Modifier.width(12.dp))

                Row(
                    modifier = Modifier.clickable(
                        onClick = onExpandClick
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isExpanded) "收合" else "展開",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Icon(
                        imageVector = if (isExpanded) {
                            Icons.Default.ExpandLess
                        } else {
                            Icons.Default.ExpandMore
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (isExpanded) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableTags.forEach { tag ->
                        val isSelected = tag in selectedTags

                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                onTagClick(tag)
                            },
                            label = {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                null
                            },
                            shape = RoundedCornerShape(6.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor =
                                    MaterialTheme.colorScheme.primary,
                                selectedLabelColor =
                                    MaterialTheme.colorScheme.onPrimary,
                                selectedLeadingIconColor =
                                    MaterialTheme.colorScheme.onPrimary,
                                containerColor =
                                    MaterialTheme.colorScheme.surface,
                                labelColor =
                                    MaterialTheme.colorScheme.onSurface
                            ),
                            border = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FolderBookmarkHeader(
    count: Int,
    isNewestFirst: Boolean,
    onSortClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 4.dp,
                vertical = 4.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "共 $count 篇收藏",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.clickable(
                onClick = onSortClick
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = "切換排序",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.width(4.dp))

            Text(
                text = if (isNewestFirst) "時間排序" else "時間排序",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun EmptyBookmarkView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "找不到符合條件的收藏",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


//
//import androidx.compose.runtime.*
//import androidx.compose.foundation.*
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.*
//import androidx.compose.ui.draw.*
//import androidx.compose.ui.graphics.*
//import androidx.compose.ui.text.font.*
//import androidx.compose.ui.unit.*
//import androidx.compose.ui.text.*
//import androidx.compose.ui.layout.*
//import com.personal.bookmark_app.ui.components.TextFieldView
//import com.skydoves.landscapist.*
//import com.skydoves.landscapist.coil3.*
//
//@Composable
//fun FolderDetailScreen() {
//    val textField1 = remember { mutableStateOf("") }
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .fillMaxHeight()
//            .background(
//                color = Color(0xFFFFFFFF),
//            )
//    ){
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f)
//                .verticalScroll(rememberScrollState())
//        ){
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(
//                        color = Color(0xFFF7F3EA),
//                    )
//                    .padding(top = 20.dp,start = 24.dp,end = 24.dp,)
//            ){
//                Row(
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ){
//                    CoilImage(
//                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/8bd56qu5_expires_30_days.png"},
//                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                        modifier = Modifier
//                            .padding(start = 13.dp,)
//                            .width(13.dp)
//                            .height(22.dp)
//                    )
//                    Text("未分類",
//                        color = Color(0xFF2F2A24),
//                        fontSize = 26.sp,
//                        fontWeight = FontWeight.Bold,
//                    )
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .padding(end = 2.dp,)
//                            .clip(shape = RoundedCornerShape(9999.dp))
//                            .background(
//                                color = Color(0xFFEDE8DE),
//                                shape = RoundedCornerShape(9999.dp)
//                            )
//                            .padding(vertical = 10.dp,horizontal = 16.dp,)
//                    ){
//                        CoilImage(
//                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/0mwl5btt_expires_30_days.png"},
//                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                            modifier = Modifier
//                                .clip(shape = RoundedCornerShape(9999.dp))
//                                .width(15.dp)
//                                .height(18.dp)
//                        )
//                        Column(
//                            modifier = Modifier
//                                .width(8.dp)
//                                .height(22.dp)
//                        ){
//                        }
//                        Text("批量管理",
//                            color = Color(0xFF5A5A52),
//                            fontSize = 15.sp,
//                        )
//                    }
//                }
//                Column(
//                    modifier = Modifier
//                        .height(24.dp)
//                        .fillMaxWidth()
//                ){
//                }
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .clip(shape = RoundedCornerShape(16.dp))
//                        .fillMaxWidth()
//                        .background(
//                            color = Color(0xFFF3EFE6),
//                            shape = RoundedCornerShape(16.dp)
//                        )
//                ){
//                    CoilImage(
//                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/rwvvirt0_expires_30_days.png"},
//                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                        modifier = Modifier
//                            .padding(start = 20.dp,)
//                            .clip(shape = RoundedCornerShape(16.dp))
//                            .width(20.dp)
//                            .height(20.dp)
//                    )
//                    Column(
//                        modifier = Modifier
//                            .width(12.dp)
//                            .height(25.dp)
//                    ){
//                    }
//                    TextFieldView(
//                        placeholder = "搜尋這個收藏夾的內容...",
//                        value = textField1.value,
//                        onValueChange = { newText -> textField1.value = newText },
//                        textStyle = TextStyle(
//                            color = Color(0xFF8E8A80),
//                            fontSize = 16.sp,
//                        ),
//                        modifier = Modifier
//                            .padding(end = 4.dp,)
//                            .weight(1f)
//                            .padding(vertical = 16.dp,)
//                    )
//                }
//                Column(
//                    modifier = Modifier
//                        .height(17.dp)
//                        .fillMaxWidth()
//                ){
//                }
//                Column(
//                    modifier = Modifier
//                        .clip(shape = RoundedCornerShape(16.dp))
//                        .fillMaxWidth()
//                        .background(
//                            color = Color(0xFFF3EFE6),
//                            shape = RoundedCornerShape(16.dp)
//                        )
//                        .padding(16.dp)
//                ){
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                    ){
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                        ){
//                            CoilImage(
//                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/l9qf5z6d_expires_30_days.png"},
//                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                modifier = Modifier
//                                    .width(13.dp)
//                                    .height(18.dp)
//                            )
//                            Column(
//                                modifier = Modifier
//                                    .width(12.dp)
//                                    .height(31.dp)
//                            ){
//                            }
//                            Text("標籤篩選",
//                                color = Color(0xFF2F2A24),
//                                fontSize = 16.sp,
//                            )
//                            Column(
//                                modifier = Modifier
//                                    .width(12.dp)
//                                    .height(31.dp)
//                            ){
//                            }
//                            Column(
//                                horizontalAlignment = Alignment.CenterHorizontally,
//                            ){
//                                Column(
//                                    modifier = Modifier
//                                        .padding(bottom = 7.dp,)
//                                ){
//                                    Column(
//                                        modifier = Modifier
//                                            .padding(start = 15.dp,)
//                                            .width(12.dp)
//                                            .height(31.dp)
//                                    ){
//                                    }
//                                    CoilImage(
//                                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/cjtve16x_expires_30_days.png"},
//                                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                        modifier = Modifier
//                                            .width(16.dp)
//                                            .height(16.dp)
//                                    )
//                                }
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .clip(shape = RoundedCornerShape(9999.dp))
//                                    .background(
//                                        color = Color(0xFF4E624B),
//                                        shape = RoundedCornerShape(9999.dp)
//                                    )
//                                    .clickable { println("Pressed!") }
//                                    .padding(vertical = 5.dp,horizontal = 16.dp,)
//                            ){
//                                Text("已選 2 個標籤",
//                                    color = Color(0xFFFFFFFF),
//                                    fontSize = 13.sp,
//                                    fontWeight = FontWeight.Bold,
//                                )
//                            }
//                        }
//                        Column(
//                            modifier = Modifier
//                                .padding(end = 1.dp,)
//                                .width(8.dp)
//                                .height(31.dp)
//                        ){
//                        }
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                        ){
//                            Text("清除",
//                                color = Color(0xFF8E8A80),
//                                fontSize = 16.sp,
//                                modifier = Modifier
//                                    .padding(end = 2.dp,)
//                            )
//                            Column(
//                                modifier = Modifier
//                                    .width(16.dp)
//                                    .height(24.dp)
//                            ){
//                            }
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                            ){
//                                Text("展開",
//                                    color = Color(0xFF4E624B),
//                                    fontSize = 16.sp,
//                                    modifier = Modifier
//                                        .padding(end = 2.dp,)
//                                )
//                                Column(
//                                    modifier = Modifier
//                                        .width(4.dp)
//                                        .height(24.dp)
//                                ){
//                                }
//                                CoilImage(
//                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/y4pj66j4_expires_30_days.png"},
//                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                    modifier = Modifier
//                                        .width(12.dp)
//                                        .height(12.dp)
//                                )
//                            }
//                        }
//                    }
//                    Column(
//                        modifier = Modifier
//                            .height(16.dp)
//                            .fillMaxWidth()
//                    ){
//                    }
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                    ){
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                        ){
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                                modifier = Modifier
//                                    .clip(shape = RoundedCornerShape(4.dp))
//                                    .background(
//                                        color = Color(0xFF4E624B),
//                                        shape = RoundedCornerShape(4.dp)
//                                    )
//                                    .padding(vertical = 11.dp,horizontal = 20.dp,)
//                            ){
//                                CoilImage(
//                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/3lc61rjb_expires_30_days.png"},
//                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                    modifier = Modifier
//                                        .clip(shape = RoundedCornerShape(4.dp))
//                                        .width(12.dp)
//                                        .height(13.dp)
//                                )
//                                Column(
//                                    modifier = Modifier
//                                        .width(8.dp)
//                                        .height(24.dp)
//                                ){
//                                }
//                                Text("生活",
//                                    color = Color(0xFFFFFFFF),
//                                    fontSize = 16.sp,
//                                    fontWeight = FontWeight.Bold,
//                                )
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .width(12.dp)
//                                    .height(46.dp)
//                            ){
//                            }
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                                modifier = Modifier
//                                    .clip(shape = RoundedCornerShape(4.dp))
//                                    .background(
//                                        color = Color(0xFF4E624B),
//                                        shape = RoundedCornerShape(4.dp)
//                                    )
//                                    .padding(vertical = 11.dp,horizontal = 20.dp,)
//                            ){
//                                CoilImage(
//                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/pf812isq_expires_30_days.png"},
//                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                    modifier = Modifier
//                                        .clip(shape = RoundedCornerShape(4.dp))
//                                        .width(12.dp)
//                                        .height(13.dp)
//                                )
//                                Column(
//                                    modifier = Modifier
//                                        .width(8.dp)
//                                        .height(24.dp)
//                                ){
//                                }
//                                Text("閱讀",
//                                    color = Color(0xFFFFFFFF),
//                                    fontSize = 16.sp,
//                                    fontWeight = FontWeight.Bold,
//                                )
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .width(12.dp)
//                                    .height(46.dp)
//                            ){
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .clip(shape = RoundedCornerShape(4.dp))
//                                    .background(
//                                        color = Color(0xFFE8E4DB),
//                                        shape = RoundedCornerShape(4.dp)
//                                    )
//                                    .clickable { println("Pressed!") }
//                                    .padding(vertical = 11.dp,horizontal = 20.dp,)
//                            ){
//                                Text("美食",
//                                    color = Color(0xFF5A5A52),
//                                    fontSize = 16.sp,
//                                    fontWeight = FontWeight.Bold,
//                                )
//                            }
//                        }
//                        Column(
//                            modifier = Modifier
//                                .height(12.dp)
//                                .fillMaxWidth()
//                        ){
//                        }
//                        Column(
//                            modifier = Modifier
//                                .clip(shape = RoundedCornerShape(4.dp))
//                                .background(
//                                    color = Color(0xFFE8E4DB),
//                                    shape = RoundedCornerShape(4.dp)
//                                )
//                                .clickable { println("Pressed!") }
//                                .padding(vertical = 11.dp,horizontal = 20.dp,)
//                        ){
//                            Text("旅遊",
//                                color = Color(0xFF5A5A52),
//                                fontSize = 16.sp,
//                                fontWeight = FontWeight.Bold,
//                            )
//                        }
//                    }
//                }
//                Column(
//                    modifier = Modifier
//                        .height(24.dp)
//                        .fillMaxWidth()
//                ){
//                }
//                Row(
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 4.dp,)
//                ){
//                    Text("共 18 篇收藏",
//                        color = Color(0xFF8A8478),
//                        fontSize = 15.sp,
//                        fontWeight = FontWeight.Bold,
//                    )
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .padding(horizontal = 1.dp,)
//                    ){
//                        CoilImage(
//                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/xytjxhbt_expires_30_days.png"},
//                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                            modifier = Modifier
//                                .width(16.dp)
//                                .height(14.dp)
//                        )
//                        Column(
//                            modifier = Modifier
//                                .width(9.dp)
//                                .height(24.dp)
//                        ){
//                        }
//                        Text("時間排序",
//                            color = Color(0xFF6F6A60),
//                            fontSize = 16.sp,
//                        )
//                    }
//                }
//                Column(
//                    modifier = Modifier
//                        .height(16.dp)
//                        .fillMaxWidth()
//                ){
//                }
//                FolderBookmarkCards()
//            }
//        }
//    }
//}
//
//
//
//private data class FolderBookmarkItem(val source:String,val date:String,val title:String,val description:String,val imageUrl:String,val tags:List<String>,val note:String?=null)
//
//@Composable private fun FolderBookmarkCards(){
// val photos=listOf("https://images.unsplash.com/photo-1445116572660-236099ec97a0?w=240","https://images.unsplash.com/photo-1497250681960-ef046c08a56e?w=240","https://images.unsplash.com/photo-1516483638261-f4dbaf036963?w=240","https://images.unsplash.com/photo-1476224203421-9ac39bcb3327?w=240","https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=240")
// val items=listOf(
// FolderBookmarkItem("Threads","2024/06/12","台北巷弄裡的質感咖啡廳","在這座城市裡，總有一些不經意走進去...",photos[0],listOf("美食","生活"),"老闆推薦手沖那加雪菲，二樓窗邊位置採光最好，平日不限時，適合帶筆電或閱讀一整個下午。"),
// FolderBookmarkItem("Dcard","2024/06/10","讓生活變美好的 5 個習慣","從日常小事開始，慢慢變成更喜歡的自己...",photos[1],listOf("生活","閱讀")),
// FolderBookmarkItem("Instagram","2024/06/08","京都小旅行｜必去的 5 個景點","這次的京都行，記錄了幾個很喜歡的地...",photos[2],listOf("旅遊","生活")),
// FolderBookmarkItem("YouTube","2024/06/05","簡單好吃的家常料理｜番茄燉飯","食材簡單、步驟不難，在家也能輕鬆做...",photos[3],listOf("美食","料理")),
// FolderBookmarkItem("網頁","2024/06/02","如何建立自己的數位資料庫","整理收藏、管理資訊，讓靈感不再從指...",photos[4],listOf("設計","工具")))
// Column(Modifier.fillMaxWidth().padding(bottom=28.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){items.forEach{FolderBookmarkCard(it)}}
//}
//
//@Composable private fun FolderBookmarkCard(item:FolderBookmarkItem){
// Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).padding(14.dp),verticalAlignment=Alignment.CenterVertically){
// CoilImage(imageModel={item.imageUrl},imageOptions=ImageOptions(contentScale=ContentScale.Crop),modifier=Modifier.size(76.dp).clip(RoundedCornerShape(7.dp)))
// Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)){
// Row(verticalAlignment=Alignment.CenterVertically){Text("${item.source} · ${item.date}",color=Color(0xFF858178),fontSize=12.sp,maxLines=1,modifier=Modifier.weight(1f));Text("⋮",color=Color(0xFF858178),fontSize=20.sp)}
// Spacer(Modifier.height(3.dp));Text(item.title,color=Color(0xFF292720),fontSize=16.sp,fontWeight=FontWeight.SemiBold,maxLines=2,lineHeight=21.sp)
// Spacer(Modifier.height(2.dp));Text(item.description,color=Color(0xFF6C675E),fontSize=12.sp,maxLines=1)
// item.note?.let{note->Spacer(Modifier.height(8.dp));Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(5.dp)).background(Color(0xFFF4F2EC)).padding(9.dp)){Text("☷  筆記                                      ⌃",color=Color(0xFF50624E),fontSize=12.sp,fontWeight=FontWeight.SemiBold);Spacer(Modifier.height(5.dp));HorizontalDivider(color=Color(0xFFE2DED5));Spacer(Modifier.height(5.dp));Text(note,color=Color(0xFF49453E),fontSize=12.sp,lineHeight=18.sp,maxLines=4)}}
// Spacer(Modifier.height(7.dp));Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){item.tags.forEach{tag->Text(tag,color=Color(0xFF57564F),fontSize=11.sp,fontWeight=FontWeight.SemiBold,modifier=Modifier.clip(RoundedCornerShape(50)).background(Color(0xFFE9E7E1)).padding(horizontal=9.dp,vertical=3.dp))}}
// }}
//}
