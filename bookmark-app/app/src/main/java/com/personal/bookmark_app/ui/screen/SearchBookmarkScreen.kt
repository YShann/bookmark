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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.ui.components.BookmarkCard
import com.personal.bookmark_app.ui.mockdata.bookmarkMockData
import com.personal.bookmark_app.ui.model.BookmarkSource
import com.personal.bookmark_app.ui.model.BookmarkUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBookmarkScreen(
    onBackClick: () -> Unit = {},
) {
    // 搜尋關鍵字
    var searchQuery by remember {
        mutableStateOf("")
    }

    // 目前選取的平台，null 代表全部平台
    var selectedSource by remember {
        mutableStateOf<BookmarkSource?>(null)
    }

    // 先根據關鍵字篩選收藏
    val searchFilteredBookmarks = remember(searchQuery) {
        bookmarkMockData.filter { bookmark ->
            bookmark.matchesSearchQuery(searchQuery)
        }
    }

    // 計算各平台符合目前關鍵字的收藏數量
    val sourceBookmarkCounts = remember(searchFilteredBookmarks) {
        BookmarkSource.entries.associateWith { source ->
            searchFilteredBookmarks.count { bookmark ->
                bookmark.source == source
            }
        }
    }

    // 再根據選取的平台篩選搜尋結果
    val filteredBookmarks = remember(
        searchFilteredBookmarks,
        selectedSource
    ) {
        searchFilteredBookmarks.filter { bookmark ->
            selectedSource == null || bookmark.source == selectedSource
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
            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // 平台篩選
            SourceFilterSection(
                sourceBookmarkCounts = sourceBookmarkCounts,
                selectedSource = selectedSource,
                onSourceSelected = {
                    selectedSource = it
                }
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // 搜尋結果列表
            if (filteredBookmarks.isEmpty()) {
                EmptySearchResult(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                SearchResultList(
                    bookmarks = filteredBookmarks,
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
                onQueryChange = onSearchQueryChange
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

// --------------------------------------------------
// 搜尋欄
// --------------------------------------------------

@Composable
private fun SearchInputField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        placeholder = {
            Text(
                text = "搜尋標題、網址或筆記...",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "搜尋"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "清除搜尋"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        )
    )
}

// --------------------------------------------------
// 平台篩選
// --------------------------------------------------

@Composable
private fun SourceFilterSection(
    sourceBookmarkCounts: Map<BookmarkSource, Int>,
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
                    text = "全部平台（${sourceBookmarkCounts.values.sum()}）",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = Color.White
            )
        )

        // 各個收藏平台
        BookmarkSource.entries.forEach { source ->
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
                    selectedLabelColor = Color.White
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
    bookmarks: List<BookmarkUiModel>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            bottom = 36.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = bookmarks,
            key = { bookmark ->
                bookmark.id
            }
        ) { bookmark ->
            BookmarkCard(
                bookmark = bookmark
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

// --------------------------------------------------
// 收藏關鍵字比對
// --------------------------------------------------

private fun BookmarkUiModel.matchesSearchQuery(
    query: String
): Boolean {
    val keyword = query.trim()

    if (keyword.isEmpty()) {
        return true
    }

    return title.contains(keyword, ignoreCase = true) ||
            description.contains(keyword, ignoreCase = true) ||
            originalUrl.contains(keyword, ignoreCase = true) ||
            (note?.contains(keyword, ignoreCase = true) == true) ||
            source.displayName.contains(keyword, ignoreCase = true) ||
            tags.any { tag ->
                tag.contains(keyword, ignoreCase = true)
            }
}


