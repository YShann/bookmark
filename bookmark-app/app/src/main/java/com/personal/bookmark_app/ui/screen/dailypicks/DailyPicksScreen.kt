package com.personal.bookmark_app.ui.screen.dailypicks

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.data.remote.dto.response.bookmark.BookmarkSourceResponse
import com.personal.bookmark_app.data.remote.dto.response.bookmark.displayNameFor
import com.personal.bookmark_app.ui.theme.BookmarkTextSecondary
import com.personal.bookmark_app.ui.viewmodel.DailyPicksViewModel
import java.util.Locale

@Composable
fun DailyPicksScreen(
    viewModel: DailyPicksViewModel,
    onBackClick: () -> Unit = {},
    onAddBookmarkClick: () -> Unit = {},
    bookmarkSources: List<BookmarkSourceResponse> = emptyList()
) {
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.retry()
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle, viewModel) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.watchDailyRefresh()
        }
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dailyPicks = uiState.picks
    val totalBookmarkCount = uiState.totalBookmarkCount
    val isLoading = uiState.isLoading
    val error = uiState.error
    val hasLoaded = uiState.selectionDate != null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DailyPicksTopBar(
                onBackClick = onBackClick,
                onRefreshClick = viewModel::refreshPicks,
                showRefreshAction = totalBookmarkCount > 3,
                refreshEnabled = uiState.canRefresh
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            if (hasLoaded || error == null) {
                DailyPicksHeader(uiState.selectionDate)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (error != null && hasLoaded) {
                    Text(
                        text = error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    TextButton(
                        onClick = viewModel::retry,
                        enabled = !isLoading
                    ) { Text("重新載入") }
                }

                when {
                    !hasLoaded && (isLoading || error == null) -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    !hasLoaded && error != null -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = error.orEmpty(),
                                color = MaterialTheme.colorScheme.error
                            )
                            TextButton(onClick = viewModel::retry) {
                                Text("重新載入")
                            }
                        }
                    }

                    // 狀態一：完全沒有收藏
                    totalBookmarkCount == 0L -> {
                        DailyPicksEmptyState(
                            onAddBookmarkClick = onAddBookmarkClick
                        )
                    }

                    // 狀態二：收藏不足 3 則
                    !uiState.isSufficient -> {
                        DailyPicksInsufficientState(
                            bookmarkCount = totalBookmarkCount.toInt(),
                            onAddBookmarkClick = onAddBookmarkClick
                        )
                    }

                    // 狀態三：至少 3 則收藏
                    else -> {
                        dailyPicks.forEach { bookmark ->
                            DailyBookmarkCard(
                                bookmark = bookmark,
                                sourceName = bookmarkSources.displayNameFor(bookmark.source)
                            )
                        }

                        if (totalBookmarkCount > 3) DailyPicksFooter(
                            onRefreshClick = viewModel::refreshPicks,
                            enabled = !isLoading
                        )
                    }
                }
            }
        }
    }
}

// ==================================================
// Top Bar
// ==================================================


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailyPicksTopBar(
    onBackClick: () -> Unit,
    onRefreshClick: () -> Unit,
    showRefreshAction: Boolean,
    refreshEnabled: Boolean
) {
    TopAppBar(
        title = {
            Text(
                text = "今日拾藏",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
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
            if (showRefreshAction) {
                TextButton(
                    onClick = onRefreshClick,
                    enabled = refreshEnabled
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "換一組",
                        tint = LocalContentColor.current,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "換一組",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}


// ==================================================
// Header
// ==================================================

@Composable
private fun DailyPicksHeader(selectionDate: String?) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {

        val today = selectionDate?.takeIf { it.isNotBlank() }?.let {
            runCatching {
                LocalDate.parse(it).format(
                    DateTimeFormatter.ofPattern("yyyy 年 M 月 d 日・E", Locale.TAIWAN)
                )
            }.getOrDefault(it)
        } ?: "載入日期中…"

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = "日期",
                tint = BookmarkTextSecondary,
                modifier = Modifier.size(12.dp)
            )

            Text(
                text = today,
                style = MaterialTheme.typography.bodySmall,
                color = BookmarkTextSecondary
            )
        }
    }
}

// ==================================================
// Footer
// ==================================================

@Composable
private fun DailyPicksFooter(
    enabled: Boolean,
    onRefreshClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = onRefreshClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 12.dp
                ),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null
            )
            Text(
                text = "換一組推薦",
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }

        Text(
            text = "每日清晨 06:00 固定刷新，亦可手動更換。",
            color = BookmarkTextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
