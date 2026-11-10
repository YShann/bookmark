package com.personal.bookmark_app.ui.screen.dailypicks

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personal.bookmark_app.ui.theme.BookmarkTextSecondary
import com.personal.bookmark_app.ui.viewmodel.DailyPicksViewModel
import java.util.Locale

@Composable
fun DailyPicksScreen(
    onBackClick: () -> Unit = {},
    onAddBookmarkClick: () -> Unit = {},
    viewModel: DailyPicksViewModel = viewModel()
) {
    androidx.lifecycle.compose.LifecycleEventEffect(androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
        viewModel.retry()
    }
    val dailyPicks by viewModel.picks.collectAsState()
    val totalBookmarkCount by viewModel.totalBookmarkCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectionDate by viewModel.selectionDate.collectAsState()
    val isSufficient by viewModel.isSufficient.collectAsState()
    val canRefresh = totalBookmarkCount > 3 && !isLoading

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DailyPicksTopBar(
                onBackClick = onBackClick,
                onRefreshClick = viewModel::refreshPicks,
                showRefreshAction = canRefresh
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            DailyPicksHeader(selectionDate)

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (error != null && dailyPicks.isNotEmpty()) {
                    Text(
                        text = error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    TextButton(onClick = viewModel::retry, enabled = !isLoading) { Text("重新載入") }
                }

                when {
                    isLoading && dailyPicks.isEmpty() -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    error != null && dailyPicks.isEmpty() -> {
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
                    !isSufficient -> {
                        DailyPicksInsufficientState(
                            bookmarkCount = totalBookmarkCount.toInt(),
                            onAddBookmarkClick = onAddBookmarkClick
                        )
                    }

                    // 狀態三：至少 3 則收藏
                    else -> {
                        dailyPicks.forEach { bookmark ->
                            DailyBookmarkCard(
                                bookmark = bookmark
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
    showRefreshAction: Boolean
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
                    onClick = onRefreshClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "換一組",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "換一組",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
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
private fun DailyPicksHeader(selectionDate: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {

        val today = selectionDate.takeIf { it.isNotBlank() }?.let {
            runCatching { java.time.LocalDate.parse(it).format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy 年 M 月 d 日・E", Locale.TAIWAN)) }.getOrDefault(it)
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
            text = "每日依台北時間換日，下次開啟時更新；亦可手動更換。",
            color = BookmarkTextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
