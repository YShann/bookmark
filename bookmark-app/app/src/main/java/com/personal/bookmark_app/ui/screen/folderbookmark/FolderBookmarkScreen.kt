package com.personal.bookmark_app.ui.screen.folderbookmark

import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import com.personal.bookmark_app.ui.utils.openBookmarkUrl
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.personal.bookmark_app.ui.components.bookmark.BookmarkCard
import com.personal.bookmark_app.ui.components.bookmark.MoveBookmarkSheet
import com.personal.bookmark_app.ui.components.SearchInputField
import com.personal.bookmark_app.ui.components.TagInfoPopup
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkSourceResponse
import com.personal.bookmark_app.ui.model.toUiModel
import com.personal.bookmark_app.ui.screen.folderbookmark.batchmanage.BatchManageBottomBar
import com.personal.bookmark_app.ui.screen.folderbookmark.batchmanage.DeleteBookmarkDialog
import com.personal.bookmark_app.ui.viewmodel.FolderBookmarkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderBookmarkScreen(
    folderId: Long,
    onBackClick: () -> Unit = {},
    bookmarkSources: List<BookmarkSourceResponse> = emptyList()
) {
    // ------------------------------------
    // UI State
    // ------------------------------------

    var isTagFilterExpanded by remember { mutableStateOf(false) }
    var isBatchManaging by remember { mutableStateOf(false) }

    val viewModel: FolderBookmarkViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bookmarks = viewModel.bookmarks.collectAsLazyPagingItems()
    val isRefreshingBookmarks = bookmarks.loadState.refresh is LoadState.Loading
    val loading = uiState.isLoading || isRefreshingBookmarks
    val loadFailed =
        bookmarks.loadState.refresh is LoadState.Error || bookmarks.loadState.append is LoadState.Error
    val context = LocalContext.current
    val folderName = uiState.folderName
    val availableTags = uiState.tags
    val busy = loading || uiState.isMutating

    // 已選取的收藏 ID
    var selectedBookmarkIds by remember {
        mutableStateOf<Set<String>>(emptySet())
    }

    // Dialog / BottomSheet
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var bookmarkIdsToMove by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    val availableFolders = uiState.folders.filterNot { it.id == folderId }
    BackHandler(enabled = isBatchManaging || uiState.isMutating) {
        if (!uiState.isMutating) {
            isBatchManaging = false
            selectedBookmarkIds = emptySet()
        }
    }

    // ------------------------------------
    // 批量管理狀態
    // ------------------------------------

    // 目前顯示的收藏是否全部選取
    var matchingIds by remember(uiState.searchQuery, uiState.selectedTags, uiState.totalCount) {
        mutableStateOf<Set<String>?>(null)
    }
    val loadedBookmarks = bookmarks.itemSnapshotList.items
    val loadedIds = remember(loadedBookmarks) { loadedBookmarks.map { it.id.toString() }.toSet() }
    val allIds = matchingIds ?: loadedIds.takeIf { it.size.toLong() == uiState.totalCount }
    val isAllSelected = !allIds.isNullOrEmpty() && allIds.all { it in selectedBookmarkIds }

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
        if (busy) return
        fun toggleIds(ids: Set<String>) {
            selectedBookmarkIds = if (ids.all { it in selectedBookmarkIds }) {
                selectedBookmarkIds - ids
            } else {
                selectedBookmarkIds + ids
            }
        }

        if (!allIds.isNullOrEmpty() && viewModel.isBookmarkFilterApplied()) {
            toggleIds(allIds)
            return
        }

        viewModel.getMatchingIds { ids ->
            if (!isBatchManaging) return@getMatchingIds
            matchingIds = ids
            toggleIds(ids)
        }
    }

    // ------------------------------------
    // 結束批量管理
    // ------------------------------------

    fun finishBatchManaging() {
        isBatchManaging = false
        selectedBookmarkIds = emptySet()
        showDeleteDialog = false
        bookmarkIdsToMove = emptyList()
    }

    // ------------------------------------
    // 刪除收藏
    // ------------------------------------

    fun deleteSelectedBookmarks() {
        val idsToDelete = selectedBookmarkIds.toList()
        if (idsToDelete.isEmpty()) return
        if (busy) return
        showDeleteDialog = false
        viewModel.delete(
            idsToDelete,
            onRemoved = { removed ->
                if (removed.isNotEmpty()) matchingIds = null
                selectedBookmarkIds -= removed
            }) { finishBatchManaging() }
    }

    fun openMoveSheet(ids: List<String>) {
        viewModel.loadMoveFolders { bookmarkIdsToMove = ids }
    }

    fun moveBookmarks(destination: Long) {
        val ids = bookmarkIdsToMove
        if (ids.isEmpty() || busy) return
        bookmarkIdsToMove = emptyList()
        viewModel.move(ids, destination, onRemoved = { removed ->
            if (removed.isNotEmpty()) matchingIds = null
            selectedBookmarkIds -= removed
        }) {
            if (isBatchManaging) finishBatchManaging()
        }
    }

    // ------------------------------------
    // Scaffold
    // ------------------------------------

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FolderBookmarkTopBar(
                folderName = folderName,
                isBatchManaging = isBatchManaging,
                onBackClick = {
                    if (uiState.isMutating) return@FolderBookmarkTopBar
                    if (isBatchManaging) {
                        finishBatchManaging()
                    } else {
                        onBackClick()
                    }
                },
                onBatchManageClick = {
                    if (busy) return@FolderBookmarkTopBar
                    if (isBatchManaging) {
                        finishBatchManaging()
                    } else {
                        isBatchManaging = true
                        selectedBookmarkIds = emptySet()
                    }
                }
            )
        },
        bottomBar = {
            if (isBatchManaging) {
                BatchManageBottomBar(
                    isAllSelected = isAllSelected,
                    hasBookmarks = uiState.totalCount > 0 && !busy,
                    hasSelection = selectedCount > 0 && !busy,
                    onSelectAllClick = { if (!busy) toggleSelectAll() },
                    onDeleteClick = { showDeleteDialog = true },
                    onMoveClick = { openMoveSheet(selectedBookmarkIds.toList()) }
                )
            }
        }

    ) { innerPadding ->

        PullToRefreshBox(
            isRefreshing = isRefreshingBookmarks || uiState.isMutating,
            onRefresh = {
                if (!busy) {
                    matchingIds = null
                    viewModel.refresh()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 4.dp,
                    bottom = 20.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                if (uiState.error != null || loadFailed) {
                    item {
                        uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        if (loadFailed && uiState.error == null) {
                            Text(
                                "載入收藏夾失敗，請重試",
                                modifier = Modifier.clickable { bookmarks.retry() },
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                // 搜尋欄位
                item {
                    SearchInputField(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::onQueryChange,
                        placeholder = "搜尋這個收藏夾的內容..."
                    )
                }

                // 標籤篩選
                item {
                    FolderTagFilter(
                        availableTags = availableTags,
                        selectedTags = uiState.selectedTags,
                        isExpanded = isTagFilterExpanded,
                        onTagClick = viewModel::toggleTag,
                        onClearClick = viewModel::clearTags,
                        onExpandClick = { isTagFilterExpanded = !isTagFilterExpanded }
                    )
                }

                // 收藏數量與排序
                item {
                    FolderBookmarkHeader(
                        count = uiState.totalCount,
                        selectedCount = selectedCount,
                        isBatchManaging = isBatchManaging,
                        isNewestFirst = uiState.isNewestFirst,
                        onSortClick = viewModel::toggleSortOrder
                    )
                }

                // 收藏清單
                if (bookmarks.itemCount == 0 && !loading && !loadFailed && uiState.error == null) {

                    item {
                        EmptyBookmarkView()
                    }

                } else {
                    items(
                        count = bookmarks.itemCount,
                        key = bookmarks.itemKey { it.id }
                    ) { index ->
                        val response = bookmarks[index] ?: return@items
                        val bookmark = response.toUiModel(bookmarkSources)

                        BookmarkCard(
                            item = bookmark,
                            showActions = !busy && !isBatchManaging,
                            isBatchManaging = isBatchManaging,
                            isSelected = bookmark.id in selectedBookmarkIds,

                            onSelectionClick = {
                                if (busy) return@BookmarkCard
                                toggleBookmarkSelection(bookmark.id)
                            },

                            onClick = {
                                if (busy) return@BookmarkCard
                                if (isBatchManaging) {
                                    toggleBookmarkSelection(bookmark.id)
                                } else {
                                    openBookmarkUrl(context, response.url)
                                }
                            },

                            onEditTitleClick = { viewModel.update(response, title = it) },
                            onEditNoteClick = { viewModel.update(response, note = it) },
                            availableTags = availableTags,
                            onTagsSave = { viewModel.update(response, tags = it) },
                            onDeleteClick = {
                                viewModel.delete(
                                    listOf(bookmark.id),
                                    onRemoved = { removed ->
                                        if (removed.isNotEmpty()) matchingIds = null
                                        selectedBookmarkIds -= removed
                                    })
                            },
                            onMoveClick = { openMoveSheet(listOf(bookmark.id)) }
                        )
                    }
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

    if (bookmarkIdsToMove.isNotEmpty()) {
        MoveBookmarkSheet(
            selectedCount = bookmarkIdsToMove.size,
            folders = availableFolders,
            onDismiss = { bookmarkIdsToMove = emptyList() },
            onMove = ::moveBookmarks,
            onCreateFolder = viewModel::createFolder
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
        border = BorderStroke(
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
    count: Long,
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
