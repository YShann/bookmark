package com.personal.bookmark_app.ui.screen.home

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personal.bookmark_app.ui.model.FolderDisplayMode
import com.personal.bookmark_app.ui.model.FolderUiModel
import com.personal.bookmark_app.ui.viewmodel.HomeViewModel
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearchClick: () -> Unit = {},
    onAddBookmarkClick: () -> Unit = {},
    onDailyPicksClick: () -> Unit = {},
    onEditFoldersClick: () -> Unit = {},
    onFolderClick: (FolderUiModel) -> Unit = {},
    onAvatarSelected: (Uri) -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var showFolderManagement by rememberSaveable {
        mutableStateOf(false)
    }

    // 記錄目前選取的空資料夾
    var selectedEmptyFolder by remember {
        mutableStateOf<FolderUiModel?>(null)
    }

    val homeViewModel: HomeViewModel = viewModel()
    androidx.lifecycle.compose.LifecycleEventEffect(androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
        homeViewModel.refreshFolders()
    }


    // 觀察本地端儲存的顯示方式
    val displayMode by homeViewModel.folderDisplayMode.collectAsState()
    val folders by homeViewModel.folders.collectAsState()
    val isRefreshingFolders by homeViewModel.isRefreshingFolders.collectAsState()
    val profile by homeViewModel.profile.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HomeTopAppBar(
                onSearchClick = onSearchClick,
                onAddBookmarkClick = onAddBookmarkClick,
                email = profile.email,
                avatarImageUrl = profile.avatarImageUrl,
                onAvatarSelected = { uri ->
                    homeViewModel.updateAvatar(uri)
                    onAvatarSelected(uri)
                },
                onLogoutClick = onLogoutClick
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

            PullToRefreshBox(
                isRefreshing = isRefreshingFolders,
                onRefresh = homeViewModel::refreshFolders,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                FolderSection(
                    folders = folders,
                    modifier = Modifier.fillMaxSize(),
                    displayMode = displayMode,
                    onDisplayModeChange = homeViewModel::updateFolderDisplayMode,
                    onEditClick = {
                        showFolderManagement = true
                    },
                    onFolderClick = { folder ->
                        // 判斷資料夾是否有收藏
                        if (folder.count == 0) {
                            selectedEmptyFolder = folder
                        } else {
                            // 有收藏則維持原本的導頁行為
                            onFolderClick(folder)
                        }
                    }
                )
            }
        }
    }

    // 收藏夾管理 Bottom Sheet
    if (showFolderManagement) {
        FolderManagementSheet(
            folders = folders,
            onDismiss = {
                showFolderManagement = false
            },
            // 修改資料夾名稱
            onRename = { folderId, newTitle ->
                homeViewModel.renameFolder(folderId, newTitle)
            },
            // 直接刪除資料夾及其收藏
            onDelete = { folderId ->
                homeViewModel.deleteFolder(folderId)
            },
            // 將收藏移至「未分類」後刪除原資料夾
            onMoveToUnclassified = { folderId ->
                homeViewModel.moveFolderBookmarksToUnclassified(folderId)
            },
            // 新增資料夾
            onAdd = { folderName ->
                homeViewModel.createFolder(folderName)
            }
        )
    }

    // 空資料夾 Bottom Sheet
    selectedEmptyFolder?.let { folder ->
        EmptyFolderBottomSheet(
            folder = folder,
            onDismiss = {
                selectedEmptyFolder = null
            },
            onAddBookmarkClick = {
                selectedEmptyFolder = null

                // 導向新增收藏頁面
                onAddBookmarkClick()
            }
        )
    }

}

// --------------------------------------------------
// Home Top App Bar
// --------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    onSearchClick: () -> Unit,
    onAddBookmarkClick: () -> Unit,
    email: String,
    avatarImageUrl: String?,
    onAvatarSelected: (Uri) -> Unit,
    onLogoutClick: () -> Unit
) {
    var showProfileDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showLogoutConfirmation by rememberSaveable {
        mutableStateOf(false)
    }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            showProfileDialog = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val avatarModel = avatarImageUrl?.takeIf { it.isNotBlank() }

                    if (avatarModel != null) {
                        CoilImage(
                            imageModel = { avatarModel },
                            imageOptions = ImageOptions(
                                contentScale = ContentScale.Crop
                            ),
                            failure = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "個人頭像載入失敗",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "個人頭像",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

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

            IconButton(
                onClick = onAddBookmarkClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "新增書籤"
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

    // 個人檔案 Dialog
    if (showProfileDialog) {
        PersonalProfileDialog(
            email = email,
            avatarImageUrl = avatarImageUrl,
            onDismiss = {
                showProfileDialog = false
            },
            onComplete = { uri ->
                uri?.let(onAvatarSelected)
                showProfileDialog = false
            },
            onLogoutClick = {
                showProfileDialog = false
                showLogoutConfirmation = true
            }
        )
    }

    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = { Text("確認登出") },
            text = { Text("確定要登出目前帳號嗎？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirmation = false
                        onLogoutClick()
                    }
                ) {
                    Text("登出")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
                    Text("取消")
                }
            }
        )
    }
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
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左側主色裝飾線
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "今日拾藏",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "從遺忘的角落拾起3則收藏。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
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
}

// --------------------------------------------------
// Folder Section
// --------------------------------------------------

@Composable
private fun FolderSection(
    folders: List<FolderUiModel>,
    modifier: Modifier = Modifier,
    displayMode: FolderDisplayMode,
    onDisplayModeChange: (FolderDisplayMode) -> Unit,
    onEditClick: () -> Unit,
    onFolderClick: (FolderUiModel) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 20.dp)
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
                    style = MaterialTheme.typography.titleMedium,
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // GRID、LIST 切換，編輯按鈕
                // 區塊顯示
                IconButton(
                    onClick = {
                        onDisplayModeChange(FolderDisplayMode.GRID)
                    },
                    modifier = Modifier.size(36.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (displayMode == FolderDisplayMode.GRID) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        } else {
                            Color.Transparent
                        }
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.GridView,
                        contentDescription = "區塊顯示",
                        tint = if (displayMode == FolderDisplayMode.GRID) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 列表顯示
                IconButton(
                    onClick = {
                        onDisplayModeChange(FolderDisplayMode.LIST)
                    },
                    modifier = Modifier.size(36.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (displayMode == FolderDisplayMode.LIST) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        } else {
                            Color.Transparent
                        }
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ViewList,
                        contentDescription = "列表顯示",
                        tint = if (displayMode == FolderDisplayMode.LIST) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onEditClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "編輯",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
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
            if (folder.imageUrl.isNotBlank()) {
                CoilImage(
                    imageModel = { folder.imageUrl },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop
                    ),
                    failure = {
                        FolderImageFallback(
                            modifier = Modifier.fillMaxSize(),
                            iconSize = 40.dp,
                            contentDescription = "預設資料夾封面"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.52f)
                )
            } else {
                FolderImageFallback(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.52f),
                    iconSize = 40.dp,
                    contentDescription = "預設資料夾封面"
                )
            }

            Row(
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 10.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = folder.title,
                    modifier = Modifier.weight(1f, fill = false),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "(${folder.count})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun FolderImageFallback(
    modifier: Modifier,
    iconSize: androidx.compose.ui.unit.Dp,
    contentDescription: String
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerLow),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.FolderOpen,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(iconSize)
        )
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
            if (folder.imageUrl.isNotBlank()) {
                CoilImage(
                    imageModel = { folder.imageUrl },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop
                    ),
                    failure = {
                        FolderImageFallback(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(6.dp)),
                            iconSize = 32.dp,
                            contentDescription = "資料夾封面"
                        )
                    },
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            } else {
                FolderImageFallback(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    iconSize = 32.dp,
                    contentDescription = "資料夾封面"
                )
            }

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
