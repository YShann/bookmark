package com.personal.bookmark_app.ui.screen.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.ui.model.FolderUiModel
import com.personal.bookmark_app.ui.components.AddFolderDialog
import com.personal.bookmark_app.ui.utils.MAX_FOLDER_NAME_LENGTH

private enum class FolderDeleteAction {
    MOVE_TO_UNCLASSIFIED,
    DELETE_ALL
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FolderManagementSheet(
    folders: LazyPagingItems<FolderUiModel>,
    folderCount: Long,
    onDismiss: () -> Unit,
    onRename: (Long, String) -> Unit,
    onDelete: (Long) -> Unit,
    onMoveToUnclassified: (Long) -> Unit,
    onAdd: suspend (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val isKeyboardVisible = WindowInsets.isImeVisible

    BackHandler {
        if (isKeyboardVisible) {
            keyboardController?.hide()
        } else {
            onDismiss()
        }
    }

    var showAddDialog by rememberSaveable {
        mutableStateOf(false)
    }

    // 目前準備刪除的資料夾
    var folderToDelete by remember {
        mutableStateOf<FolderUiModel?>(null)
    }

    // 使用者選擇的刪除方式
    var deleteAction by remember {
        mutableStateOf(FolderDeleteAction.MOVE_TO_UNCLASSIFIED)
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 每列預留 78dp（列高與列間距），清單再保留 20dp 底部間距。
            // 整個內容區最多使用 BottomSheet 可用高度的 80%。
            val maxSheetHeight = maxHeight * 0.8f
            val maxFolderListHeight = (maxSheetHeight - 240.dp).coerceAtLeast(0.dp)
            val folderListHeight =
                (folderCount * 78 + 20).toFloat().dp.coerceAtMost(maxFolderListHeight)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxSheetHeight)
                    .padding(horizontal = 16.dp)
            ) {

                // 標題與關閉按鈕
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(22.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = "管理收藏夾",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(50)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "關閉管理",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 新增收藏夾按鈕
                OutlinedButton(
                    onClick = {
                        showAddDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        width = 0.dp,
                        color = Color.Transparent
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "新建收藏夾",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(Modifier.height(16.dp))

                // 收藏夾清單
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(folderListHeight),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(
                        count = folders.itemCount,
                        key = folders.itemKey { it.id }
                    ) { index ->
                        val folder = folders[index] ?: return@items
                        FolderManagementItem(
                            folder = folder,
                            isManageable = folder.title != "未分類",
                            onRename = { newTitle ->
                                if (folder.title != "未分類") {
                                    onRename(folder.id, newTitle)
                                }
                            },
                            onDelete = {
                                if (folder.title != "未分類") {
                                    folderToDelete = folder

                                    // 每次開啟 Dialog，預設選擇移至未分類
                                    deleteAction = FolderDeleteAction.MOVE_TO_UNCLASSIFIED
                                }
                            }
                        )
                    }
                }

                // 完成管理按鈕
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .padding(bottom = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "完成管理",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // 新增收藏夾 Dialog
    if (showAddDialog) {
        AddFolderDialog(
            onDismiss = {
                showAddDialog = false
            },
            onConfirm = onAdd
        )
    }

    // 刪除資料夾 Dialog
    folderToDelete?.let { folder ->
        DeleteFolderDialog(
            folder = folder,
            selectedAction = deleteAction,
            onActionChange = {
                deleteAction = it
            },
            onDismiss = {
                folderToDelete = null
            },
            onConfirm = {
                when {
                    // 第一種：沒有收藏
                    folder.count == 0 -> {
                        onDelete(folder.id)
                    }

                    // 第二種：移至未分類
                    deleteAction ==
                            FolderDeleteAction.MOVE_TO_UNCLASSIFIED -> {
                        onMoveToUnclassified(folder.id)
                    }

                    // 第二種：連同收藏一起刪除
                    deleteAction == FolderDeleteAction.DELETE_ALL -> {
                        onDelete(folder.id)
                    }
                }

                folderToDelete = null
            }
        )
    }
}

@Composable
private fun FolderManagementItem(
    folder: FolderUiModel,
    isManageable: Boolean,
    onRename: (String) -> Unit,
    onDelete: () -> Unit
) {
    var isEditing by rememberSaveable(folder.id) {
        mutableStateOf(false)
    }

    var editedTitle by rememberSaveable(folder.id) {
        mutableStateOf(folder.title)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 左側資料夾圖示
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.FolderOpen,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            if (isManageable && isEditing) {
                val newTitle = editedTitle.trim()
                val nameTooLong = newTitle.length > MAX_FOLDER_NAME_LENGTH
                val canRename = newTitle.isNotEmpty() && !nameTooLong

                // 編輯模式：輸入框
                OutlinedTextField(
                    value = editedTitle,
                    onValueChange = {
                        editedTitle = it
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = nameTooLong,
                    supportingText = if (nameTooLong) {
                        { Text("收藏夾名稱不能超過 $MAX_FOLDER_NAME_LENGTH 個字") }
                    } else {
                        null
                    },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    shape = RoundedCornerShape(8.dp),
                    placeholder = {
                        Text("輸入收藏夾名稱")
                    }
                )

                Spacer(Modifier.width(8.dp))

                // 確認修改
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(
                            enabled = canRename,
                            onClick = {
                                onRename(newTitle)
                                isEditing = false
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (canRename) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "確認修改",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

            } else {
                // 一般模式：資料夾名稱與數量
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

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "${folder.count} 篇內容",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isManageable) {
                    // 編輯按鈕
                    IconButton(
                        onClick = {
                            editedTitle = folder.title
                            isEditing = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "修改收藏夾名稱",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // 刪除按鈕
                    IconButton(
                        onClick = onDelete
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "刪除收藏夾",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteFolderDialog(
    folder: FolderUiModel,
    selectedAction: FolderDeleteAction,
    onActionChange: (FolderDeleteAction) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val hasBookmarks = folder.count > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "確定要刪除「${folder.title}」？",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // 顯示收藏數量
                Text(
                    text = if (hasBookmarks) {
                        "此收藏夾共有 ${folder.count} 篇收藏，請選擇處理方式。"
                    } else {
                        "此收藏夾目前尚無任何內容。"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 有收藏時才顯示兩種處理方式
                if (hasBookmarks) {

                    // 選項一：移至未分類
                    DeleteFolderOption(
                        title = "移至「未分類」收藏夾",
                        description = "保留所有收藏，只刪除這個收藏夾。",
                        selected = selectedAction ==
                                FolderDeleteAction.MOVE_TO_UNCLASSIFIED,
                        onClick = {
                            onActionChange(
                                FolderDeleteAction.MOVE_TO_UNCLASSIFIED
                            )
                        }
                    )

                    // 選項二：連同收藏一起刪除
                    DeleteFolderOption(
                        title = "連同 ${folder.count} 篇收藏全部刪除",
                        description = "資料夾與裡面的收藏都會永久刪除。",
                        selected = selectedAction ==
                                FolderDeleteAction.DELETE_ALL,
                        isDestructive = true,
                        onClick = {
                            onActionChange(
                                FolderDeleteAction.DELETE_ALL
                            )
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    text = when {
                        !hasBookmarks -> "確認刪除"
                        selectedAction ==
                                FolderDeleteAction.MOVE_TO_UNCLASSIFIED ->
                            "確認並移至未分類"

                        else -> "確認刪除全部"
                    },
                    color = if (
                        hasBookmarks &&
                        selectedAction == FolderDeleteAction.DELETE_ALL
                    ) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "取消",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}


@Composable
private fun DeleteFolderOption(
    title: String,
    description: String,
    selected: Boolean,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val borderColor = when {
        selected && isDestructive ->
            MaterialTheme.colorScheme.error

        selected ->
            MaterialTheme.colorScheme.primary

        else ->
            MaterialTheme.colorScheme.outlineVariant
    }

    val containerColor = when {
        selected && isDestructive ->
            MaterialTheme.colorScheme.errorContainer

        selected ->
            MaterialTheme.colorScheme.primaryContainer

        else ->
            MaterialTheme.colorScheme.surface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),

        shape = RoundedCornerShape(8.dp),

        color = containerColor,

        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            )

            Spacer(Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
