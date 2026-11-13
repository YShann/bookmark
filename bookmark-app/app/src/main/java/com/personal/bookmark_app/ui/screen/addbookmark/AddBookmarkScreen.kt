package com.personal.bookmark_app.ui.screen.addbookmark

import androidx.compose.material3.TextButton
import com.personal.bookmark_app.ui.components.AddFolderDialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.ui.components.MultilineTextFieldView
import com.personal.bookmark_app.ui.components.TextFieldView

@Composable
fun AddBookmarkScreen(
    uiState: AddBookmarkUiState,
    onSourceUrlChange: (String) -> Unit,
    onCreateTag: (String) -> Unit,
    onRetry: () -> Unit,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,

    onFolderSelected: (Long) -> Unit,
    onCreateFolderClick: suspend (String) -> Unit,

    onTagClick: (String) -> Unit,

    onDailyPicksChange: (Boolean) -> Unit,
    onReparseClick: () -> Unit,
    onClearTitleClick: () -> Unit,

    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var showFolderSheet by rememberSaveable {
        mutableStateOf(false)
    }

    var showAddFolderDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val folders = uiState.folders

    // 控制新增自訂標籤 Dialog
    var showAddTagDialog by rememberSaveable {
        mutableStateOf(false)
    }

    // 暫存標籤列表
    val tags = uiState.tags

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AddBookmarkTopBar(
                isSaveEnabled = uiState.isFormValid,
                onBackClick = onBackClick,
                onSaveClick = onSaveClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 4.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 封面、網址與資料夾
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    BookmarkSourceField(
                        sourceUrl = uiState.sourceUrl,
                        content = uiState.content,
                        isParsing = uiState.isParsing,
                        isSaving = uiState.isSaving,
                        onSourceUrlChange = onSourceUrlChange,
                        onReparseClick = onReparseClick
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    BookmarkFolderSelector(
                        selectedFolder = folders.find { it.id == uiState.selectedFolderId },
                        enabled = !uiState.isLoading && !uiState.isSaving,
                        onClick = { showFolderSheet = true }
                    )
                    uiState.parseError?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    if (uiState.isLoading) Text("載入中…")
                    if (uiState.isSaving) Text("儲存中…")
                    uiState.errorMessage?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    if (folders.isEmpty() && !uiState.isLoading) {
                        TextButton(onClick = onRetry) { Text("重新載入") }
                        TextButton(onClick = {
                            showAddFolderDialog = true
                        }) { Text("新增資料夾") }
                    }

                }
            }

            // 收藏內容與分類
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BookmarkTitleSection(
                        title = uiState.title,
                        onTitleChange = onTitleChange,
                        onClearClick = onClearTitleClick
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BookmarkNoteSection(
                        note = uiState.note,
                        onNoteChange = onNoteChange
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BookmarkTagSection(
                        tags = tags,
                        selectedTagIds = uiState.selectedTagIds,
                        onTagClick = onTagClick,
                        onAddCustomTagClick = {
                            showAddTagDialog = true
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))


                    DailyPicksOption(
                        checked = uiState.isAddedToDailyPicks,
                        onCheckedChange = { if (!uiState.isSaving) onDailyPicksChange(it) })
                    Text(
                        "收藏總數達三筆後，儲存時會加入今日拾藏；已有三筆拾藏時隨機替換一筆。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }
            }
        }
    }
    if (showFolderSheet) {
        FolderSelectionSheet(
            folders = folders,
            selectedFolderId = uiState.selectedFolderId,
            onDismiss = {
                showFolderSheet = false
            },
            onFolderSelected = { folderId ->
                onFolderSelected(folderId)
                showFolderSheet = false
            },
            onCreateFolderClick = {
                showFolderSheet = false
                showAddFolderDialog = true
            }
        )
    }

    // 新增資料夾 Dialog
    if (showAddFolderDialog) {
        AddFolderDialog(
            onDismiss = {
                showAddFolderDialog = false
            },
            onConfirm = onCreateFolderClick
        )
    }

    // 新增自訂標籤 Dialog
    if (showAddTagDialog) {
        AddTagDialog(
            existingTags = tags,
            onDismiss = {
                showAddTagDialog = false
            },
            onConfirm = { tagName ->

                onCreateTag(tagName)

                showAddTagDialog = false
            }
        )
    }
}

// ==================================================
// Top Bar
// ==================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBookmarkTopBar(
    isSaveEnabled: Boolean,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "新增收藏",
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
            Button(
                onClick = onSaveClick,
                enabled = isSaveEnabled,
                modifier = Modifier.padding(end = 12.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(
                    horizontal = 18.dp,
                    vertical = 8.dp
                )
            ) {
                Text(
                    text = "儲存",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}


/* -------------------------------------------------------
 * 文章標題
 * ------------------------------------------------------- */

@Composable
private fun BookmarkTitleSection(
    title: String,
    onTitleChange: (String) -> Unit,
    onClearClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "文章或影音標題",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )


        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable(
                    enabled = title.isNotEmpty(),
                    onClick = onClearClick
                )
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "清空",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    TextFieldView(
        value = title,
        onValueChange = onTitleChange,
        placeholder = "請輸入文章或影音標題",
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}


/* -------------------------------------------------------
 * 個人札記
 * ------------------------------------------------------- */

@Composable
private fun BookmarkNoteSection(
    note: String,
    onNoteChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "個人拾藏札記",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "備註",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    MultilineTextFieldView(
        value = note,
        onValueChange = onNoteChange,
        placeholder = "寫下收藏這篇文章的原因、想法或未來想做的事...",
        textStyle = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.fillMaxWidth(),
        height = 120.dp,
        charCount = "${note.length} 字"
    )
}


/* -------------------------------------------------------
 * 今日拾藏
 * ------------------------------------------------------- */

@Composable
private fun DailyPicksOption(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable {
                onCheckedChange(!checked)
            }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(21.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "加入今日拾藏",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
