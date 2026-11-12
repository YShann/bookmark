package com.personal.bookmark_app.ui.screen

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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personal.bookmark_app.ui.components.BookmarkCard
import com.personal.bookmark_app.ui.components.SearchInputField
import com.personal.bookmark_app.ui.model.BookmarkSource
import com.personal.bookmark_app.ui.model.BookmarkUiModel
import com.personal.bookmark_app.ui.utils.openBookmarkUrl
import com.personal.bookmark_app.ui.viewmodel.SearchBookmarkEntry
import com.personal.bookmark_app.ui.viewmodel.SearchBookmarkViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBookmarkScreen(
    onBackClick: () -> Unit = {},
    viewModel: SearchBookmarkViewModel = viewModel(),
) {
    val bookmarkEntries by viewModel.bookmarks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val sourceBookmarkCounts by viewModel.sourceCounts.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val listState = rememberLazyListState()
    // 搜尋關鍵字
    var searchQuery by remember {
        mutableStateOf("")
    }

    // 目前選取的平台，null 代表全部平台
    var selectedSource by remember {
        mutableStateOf<BookmarkSource?>(null)
    }

    LaunchedEffect(searchQuery, selectedSource) {
        delay(300)
        viewModel.search(searchQuery, selectedSource)
    }

    LaunchedEffect(listState, bookmarkEntries.size, isLoading) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (bookmarkEntries.isNotEmpty() && lastVisibleIndex != null && lastVisibleIndex >= bookmarkEntries.lastIndex - 3) {
                    viewModel.loadNextPage()
                }
            }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SearchBookmarkTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
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
                sourceBookmarkCounts = sourceBookmarkCounts,
                totalCount = totalCount,
                selectedSource = selectedSource,
                onSourceSelected = {
                    selectedSource = it
                }
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            error?.let { message ->
                Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // 搜尋結果列表
            if (isLoading && bookmarkEntries.isEmpty()) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("載入收藏中…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (bookmarkEntries.isEmpty()) {
                EmptySearchResult(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                SearchResultList(
                    bookmarks = bookmarkEntries,
                    listState = listState,
                    availableTags = bookmarkEntries.flatMap { it.bookmark.tags }.distinct().sorted(),
                    onEditTitle = { entry, value -> viewModel.updateBookmark(entry, title = value) },
                    onEditNote = { entry, value -> viewModel.updateBookmark(entry, note = value) },
                    onEditTags = { entry, value -> viewModel.updateBookmark(entry, tags = value) },
                    onDelete = viewModel::deleteBookmark,
                    modifier = Modifier.weight(1f)
                )
            }
        }
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
    sourceBookmarkCounts: Map<BookmarkSource, Int>,
    totalCount: Long,
    selectedSource: BookmarkSource?,
    onSourceSelected: (BookmarkSource?) -> Unit,
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

        val visibleSources = BookmarkSource.entries.filter { source ->
            source == selectedSource || (sourceBookmarkCounts[source] ?: 0) > 0
        }
        visibleSources.forEach { source ->
            FilterChip(
                selected = selectedSource == source,
                onClick = {
                    onSourceSelected(source)
                },
                label = {
                    Text(
                        text = "${source.displayName}（${sourceBookmarkCounts[source] ?: 0}）",
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
    bookmarks: List<SearchBookmarkEntry>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    availableTags: List<String>,
    onEditTitle: (SearchBookmarkEntry, String) -> Unit,
    onEditNote: (SearchBookmarkEntry, String) -> Unit,
    onEditTags: (SearchBookmarkEntry, List<String>) -> Unit,
    onDelete: (SearchBookmarkEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = PaddingValues(
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = bookmarks,
            key = { bookmark ->
                bookmark.bookmark.id
            }
        ) { bookmark ->
            BookmarkCard(
                item = bookmark.toFolderBookmarkUiModel(),
                onClick = {
                    openBookmarkUrl(context, bookmark.bookmark.url)
                },
                onEditTitleClick = { onEditTitle(bookmark, it) },
                onEditNoteClick = { onEditNote(bookmark, it) },
                availableTags = availableTags,
                onTagsSave = { onEditTags(bookmark, it) },
                onDeleteClick = { onDelete(bookmark) }
            )
        }
    }
}

private fun SearchBookmarkEntry.toFolderBookmarkUiModel() = BookmarkUiModel(
    id = bookmark.id.toString(),
    source = bookmark.source?.toBookmarkSource()?.displayName ?: "Web",
    date = bookmark.createdAt?.take(10) ?: "",
    title = bookmark.title,
    description = bookmark.content.orEmpty().ifBlank { bookmark.url },
    imageUrl = bookmark.imageUrl.orEmpty(),
    tags = bookmark.tags,
    note = bookmark.note
)

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

// --------------------------------------------------
// 收藏關鍵字比對
// --------------------------------------------------

private fun String.toBookmarkSource(): BookmarkSource = when (uppercase()) {
    "THREADS" -> BookmarkSource.THREADS
    "INSTAGRAM" -> BookmarkSource.INSTAGRAM
    "DCARD" -> BookmarkSource.DCARD
    "YOUTUBE" -> BookmarkSource.YOUTUBE
    else -> BookmarkSource.WEB
}


