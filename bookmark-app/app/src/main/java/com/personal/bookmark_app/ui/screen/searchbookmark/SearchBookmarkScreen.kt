package com.personal.bookmark_app.ui.screen.searchbookmark

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.personal.bookmark_app.ui.components.bookmark.BookmarkCard
import com.personal.bookmark_app.ui.components.bookmark.MoveBookmarkSheet
import com.personal.bookmark_app.ui.components.SearchInputField
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkSourceResponse
import com.personal.bookmark_app.data.remote.dto.response.bookmark.displayNameFor
import com.personal.bookmark_app.ui.model.toUiModel
import com.personal.bookmark_app.ui.utils.openBookmarkUrl
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkResponse
import com.personal.bookmark_app.ui.viewmodel.SearchBookmarkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBookmarkScreen(
    onBackClick: () -> Unit = {},
    viewModel: SearchBookmarkViewModel = hiltViewModel(),
    bookmarkSources: List<BookmarkSourceResponse> = emptyList()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bookmarks = viewModel.bookmarks.collectAsLazyPagingItems()
    val isLoading = bookmarks.loadState.refresh is LoadState.Loading
    val mutationError = uiState.mutationError
    val error = mutationError ?: when {
        bookmarks.loadState.refresh is LoadState.Error -> "搜尋收藏載入失敗，請稍後再試。"
        bookmarks.loadState.append is LoadState.Error -> "載入更多收藏失敗，請稍後再試。"
        else -> null
    }
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SearchBookmarkTopBar(
                searchQuery = uiState.query,
                onSearchQueryChange = viewModel::onQueryChange,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // 平台篩選
            SourceFilterSection(
                bookmarkSources = bookmarkSources,
                sourceBookmarkCounts = uiState.sourceCounts,
                totalCount = uiState.totalCount,
                selectedSource = uiState.selectedSource,
                onSourceSelected = viewModel::onSourceSelected
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            error?.let { message ->
                Text(
                    message,
                    modifier = if (mutationError == null) Modifier.clickable { bookmarks.retry() } else Modifier,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // 搜尋結果列表
            if (isLoading && bookmarks.itemCount == 0) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("載入收藏中…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (bookmarks.itemCount == 0) {
                EmptySearchResult(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                SearchResultList(
                    bookmarks = bookmarks,
                    bookmarkSources = bookmarkSources,
                    listState = listState,
                    availableTags = bookmarks.itemSnapshotList.items.flatMap { it.tags }.distinct()
                        .sorted(),
                    onEditTitle = { entry, value ->
                        viewModel.updateBookmark(
                            entry,
                            title = value
                        )
                    },
                    onEditNote = { entry, value -> viewModel.updateBookmark(entry, note = value) },
                    onEditTags = { entry, value -> viewModel.updateBookmark(entry, tags = value) },
                    onDelete = viewModel::deleteBookmark,
                    onMove = viewModel::openMoveSheet,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    uiState.moveSelection?.let { selection ->
        MoveBookmarkSheet(
            selectedCount = 1,
            folders = selection.folders,
            onDismiss = viewModel::closeMoveSheet,
            onMove = viewModel::moveBookmark,
            onCreateFolder = viewModel::createMoveFolder
        )
    }
}

// --------------------------------------------------
// Toolbar
// --------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBookmarkTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = "返回",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        title = {
            SearchInputField(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                placeholder = "搜尋標題、網址或筆記..."
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

// --------------------------------------------------
// 平台篩選
// --------------------------------------------------

@Composable
private fun SourceFilterSection(
    bookmarkSources: List<BookmarkSourceResponse>,
    sourceBookmarkCounts: Map<String, Long>,
    totalCount: Long,
    selectedSource: String?,
    onSourceSelected: (String?) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 全部平台
        FilterChip(
            selected = selectedSource == null,
            onClick = {
                onSourceSelected(null)
            },
            label = {
                Text(
                    text = "全部平台（$totalCount）",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.surface
            )
        )

        val visibleSources =
            (bookmarkSources.map { it.code } + sourceBookmarkCounts.keys + listOfNotNull(
                selectedSource
            ))
                .distinctBy { it.uppercase() }
                .map { code ->
                    val count = sourceBookmarkCounts.entries
                        .filter { it.key.equals(code, ignoreCase = true) }.sumOf { it.value }
                    code to count
                }
                .filter { (code, count) ->
                    count > 0L || code.equals(
                        selectedSource,
                        ignoreCase = true
                    )
                }
                .sortedByDescending { (_, count) -> count }
        visibleSources.forEach { (code, count) ->
            val selected = code.equals(selectedSource, ignoreCase = true)
            FilterChip(
                selected = selected,
                onClick = {
                    onSourceSelected(code)
                },
                label = {
                    Text(
                        text = "${bookmarkSources.displayNameFor(code)}（$count）",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

// --------------------------------------------------
// 搜尋結果列表
// --------------------------------------------------

@Composable
private fun SearchResultList(
    bookmarks: LazyPagingItems<BookmarkResponse>,
    bookmarkSources: List<BookmarkSourceResponse>,
    listState: LazyListState,
    availableTags: List<String>,
    onEditTitle: (BookmarkResponse, String) -> Unit,
    onEditNote: (BookmarkResponse, String) -> Unit,
    onEditTags: (BookmarkResponse, List<String>) -> Unit,
    onDelete: (BookmarkResponse) -> Unit,
    onMove: (BookmarkResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = PaddingValues(
            bottom = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            count = bookmarks.itemCount,
            key = bookmarks.itemKey { it.id }
        ) { index ->
            val bookmark = bookmarks[index] ?: return@items
            BookmarkCard(
                item = bookmark.toUiModel(bookmarkSources),
                onClick = {
                    openBookmarkUrl(context, bookmark.url)
                },
                onEditTitleClick = { onEditTitle(bookmark, it) },
                onEditNoteClick = { onEditNote(bookmark, it) },
                availableTags = availableTags,
                onTagsSave = { onEditTags(bookmark, it) },
                onDeleteClick = { onDelete(bookmark) },
                onMoveClick = { onMove(bookmark) }
            )
        }
    }
}

// --------------------------------------------------
// 搜尋結果為空
// --------------------------------------------------

@Composable
private fun EmptySearchResult(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "找不到相關收藏",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "試試其他關鍵字或切換平台分類",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
