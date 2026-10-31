package com.personal.bookmark_app.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.ui.mock_data.FolderMockData
import com.personal.bookmark_app.ui.mock_data.DisplayMode
import com.personal.bookmark_app.ui.model.FolderDisplayMode
import com.personal.bookmark_app.ui.model.FolderUiModel
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearchClick: () -> Unit = {},
    onAddBookmarkClick: () -> Unit = {},
    onDailyPicksClick: () -> Unit = {},
    onEditFoldersClick: () -> Unit = {},
    onFolderClick: (FolderUiModel) -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HomeTopAppBar(
                onSearchClick = onSearchClick,
                onAddBookmarkClick = onAddBookmarkClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            DailyPicksCard(
                onClick = onDailyPicksClick
            )

            FolderSection(
                folders = FolderMockData,
                modifier = Modifier.weight(1f),
                onEditClick = onEditFoldersClick,
                onFolderClick = onFolderClick
            )
        }
    }
}

// --------------------------------------------------
// Home Top App Bar
// --------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    onSearchClick: () -> Unit,
    onAddBookmarkClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CoilImage(
                    imageModel = {
                        "https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/fg1q4rmy_expires_30_days.png"
                    },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop
                    ),
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Text(
                    text = "聚藏",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            IconButton(
                onClick = onSearchClick
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "搜尋收藏",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Button(
                onClick = onAddBookmarkClick,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 8.dp
                )
            ) {
                Text(
                    text = "＋ 新增書籤",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

// --------------------------------------------------
// Daily Picks Card
// --------------------------------------------------

@Composable
private fun DailyPicksCard(
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "今日拾藏 • 3 則收藏已抽出",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "沉澱數位碎片，翻開為你挑選的安靜篇章。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoStories,
                    contentDescription = "今日拾藏",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// --------------------------------------------------
// Folder Section
// --------------------------------------------------

@Composable
private fun FolderSection(
    folders: List<FolderUiModel>,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
    onFolderClick: (FolderUiModel) -> Unit
) {
    // 暫時使用假資料，未來改由 ViewModel 接收後端回傳的顯示方式
    var displayMode by rememberSaveable {
        mutableStateOf(DisplayMode)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 28.dp
            )
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // SpaceBetween左側：標題與數量
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "我的收藏",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${folders.size} 個分類",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // SpaceBetween右側：顯示方式與編輯按鈕
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // GRID、LIST 切換，編輯按鈕
                Row(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(
                                alpha = 0.5f
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 區塊顯示
                    IconButton(
                        onClick = {
                            displayMode = FolderDisplayMode.GRID
                        },
                        modifier = Modifier.size(34.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (displayMode == FolderDisplayMode.GRID) {
                                MaterialTheme.colorScheme.surface
                            } else {
                                Color.Transparent
                            }
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.GridView,
                            contentDescription = "區塊顯示",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // 列表顯示
                    IconButton(
                        onClick = {
                            displayMode = FolderDisplayMode.LIST
                        },
                        modifier = Modifier.size(34.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (displayMode == FolderDisplayMode.LIST) {
                                MaterialTheme.colorScheme.surface
                            } else {
                                Color.Transparent
                            }
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ViewList,
                            contentDescription = "列表顯示",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = onEditClick,
                    contentPadding = PaddingValues(
                        horizontal = 4.dp,
                        vertical = 0.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Text(
                        text = "編輯",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // 根據顯示方式切換內容
        when (displayMode) {
            FolderDisplayMode.GRID -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(
                        bottom = 24.dp
                    ),
                    userScrollEnabled = true
                ) {
                    items(
                        items = folders,
                        key = { it.id }
                    ) { folder ->
                        FolderCard(
                            folder = folder,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onFolderClick(folder)
                            }
                        )
                    }
                }
            }

            FolderDisplayMode.LIST -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(
                        bottom = 24.dp
                    ),
                    userScrollEnabled = true
                ) {
                    items(
                        items = folders,
                        key = { it.id }
                    ) { folder ->
                        FolderListItem(
                            folder = folder,
                            onClick = {
                                onFolderClick(folder)
                            }
                        )
                    }
                }
            }
        }
    }
}

// --------------------------------------------------
// Folder Card
// --------------------------------------------------

@Composable
private fun FolderCard(
    folder: FolderUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column {
            CoilImage(
                imageModel = { folder.imageUrl },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.52f)
            )

            Text(
                text = "${folder.title} (${folder.count})",
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 12.dp
                ),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// --------------------------------------------------
// Folder List Item
// --------------------------------------------------

@Composable
private fun FolderListItem(
    folder: FolderUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(
                alpha = 0.5f
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 收藏夾縮圖
            CoilImage(
                imageModel = { folder.imageUrl },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop
                ),
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(6.dp))
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            // 收藏夾名稱與收藏數量
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = folder.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "${folder.count} 則收藏",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // > 查看收藏夾
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = "查看收藏夾",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}