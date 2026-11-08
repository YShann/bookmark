package com.personal.bookmark_app.ui.screen.folderbookmark

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.ui.components.BookmarkCard
import com.personal.bookmark_app.ui.components.SearchInputField
import com.personal.bookmark_app.ui.components.TagInfoPopup
import com.personal.bookmark_app.ui.mockdata.FolderDetailMockData
import com.personal.bookmark_app.ui.mockdata.FolderDetailMockData.availableTags
import com.personal.bookmark_app.ui.screen.addbookmark.AddFolderDialog
import com.personal.bookmark_app.ui.screen.folderbookmark.batchmanage.BatchManageBottomBar
import com.personal.bookmark_app.ui.screen.folderbookmark.batchmanage.DeleteBookmarkDialog
import com.personal.bookmark_app.ui.screen.folderbookmark.batchmanage.MoveBookmarkSheet

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

    var showAddFolderDialog by remember {
        mutableStateOf(false)
    }

    // 目的資料夾假資料
    var availableFolders by remember(folderName) {
        mutableStateOf(
            listOf(
                "未分類",
                "生活靈感",
                "稍後閱讀",
                "旅行清單",
                "美食收藏"
            ).filterNot {
                it == folderName && it != "未分類"
            }
        )
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
        val visibleIds = filteredBookmarks.map { it.id }.toSet()

        selectedBookmarkIds =
            if (isAllSelected) {
                // 取消目前畫面顯示項目的選取，保留其他篩選條件外的選取
                selectedBookmarkIds - visibleIds
            } else {
                // 選取目前畫面顯示的項目，保留之前已選取的項目
                selectedBookmarkIds + visibleIds
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
            FolderBookmarkTopBar(
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
                start = 20.dp,
                end = 20.dp,
                top = 12.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 搜尋欄位
            item {
                SearchInputField(
                    query = uiState.searchQuery,
                    onQueryChange = { query ->
                        uiState = uiState.copy(
                            searchQuery = query
                        )
                    },
                    placeholder = "搜尋這個收藏夾的內容..."
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
                    },
                    onClearClick = {
                        uiState = uiState.copy(
                            selectedTags = emptySet()
                        )
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
                    selectedCount = selectedCount,
                    isBatchManaging = uiState.isBatchManaging,
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

                    BookmarkCard(
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
        DeleteBookmarkDialog(
            selectedCount = selectedCount,
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                deleteSelectedBookmarks()
            }
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
                },
                onCreateFolderClick = {
                    showAddFolderDialog = true
                }
            )
        }
    }

    if (showAddFolderDialog) {
        AddFolderDialog(
            onDismiss = {
                showAddFolderDialog = false
            },
            onConfirm = { newFolderName ->
                val name = newFolderName.trim()

                if (
                    name.isNotEmpty() &&
                    name !in availableFolders
                ) {
                    // 新資料夾放在未分類後面
                    availableFolders = listOf("未分類") +
                            availableFolders.filterNot {
                                it == "未分類"
                            }.let {
                                listOf(name) + it
                            }
                }

                showAddFolderDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FolderBookmarkTopBar(
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
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },

        navigationIcon = {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = "返回"
                )
            }
        },

        actions = {
            Surface(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onBatchManageClick),
                color = if (isBatchManaging) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (isBatchManaging) {
                            Icons.Default.Check
                        } else {
                            Icons.Default.Checklist
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (isBatchManaging) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )

                    Text(
                        text = if (isBatchManaging) "完成" else "批量管理",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
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
    var showTagInfo by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onExpandClick
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = "標籤篩選",
                        style = MaterialTheme.typography.titleSmall
                    )

                    Spacer(Modifier.width(6.dp))

                    Box {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "標籤篩選說明",
                            modifier = Modifier
                                .size(18.dp)
                                .clickable {
                                    showTagInfo = !showTagInfo
                                },
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (showTagInfo) {
                            TagInfoPopup(
                                onDismiss = {
                                    showTagInfo = false
                                },
                                message = "標籤由收藏自動彙整，若該標籤的文章全數刪除或移出，標籤亦會同步移除。"
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    // 已選標籤數量與清除按鈕
                    if (selectedTags.isNotEmpty()) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    start = 10.dp,
                                    end = 4.dp,
                                    top = 2.dp,
                                    bottom = 2.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "已選 ${selectedTags.size} 個",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                IconButton(
                                    onClick = onClearClick,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "清除已選標籤",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.width(6.dp))

                    Icon(
                        imageVector = if (isExpanded) {
                            Icons.Default.ExpandLess
                        } else {
                            Icons.Default.ExpandMore
                        },
                        contentDescription = if (isExpanded) "收合標籤" else "展開標籤",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (isExpanded) {
                Spacer(Modifier.width(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    availableTags.forEach { tag ->
                        val isSelected = tag in selectedTags

                        Surface(
                            modifier = Modifier
                                .height(32.dp)
                                .clickable {
                                    onTagClick(tag)
                                },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "# $tag",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FolderBookmarkHeader(
    count: Int,
    selectedCount: Int,
    isBatchManaging: Boolean,
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "共 $count 篇收藏",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isBatchManaging && selectedCount > 0) {
                Text(
                    text = "已選取 $selectedCount 篇",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Row(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSortClick
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isNewestFirst) "從新到舊" else "從舊到新",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.width(4.dp))

            Icon(
                imageVector = if (isNewestFirst) {
                    Icons.Default.ArrowDownward
                } else {
                    Icons.Default.ArrowUpward
                },
                contentDescription = if (isNewestFirst) "從新到舊" else "從舊到新",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
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
