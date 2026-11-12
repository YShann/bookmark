package com.personal.bookmark_app.ui.screen.addbookmark

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.ui.components.MultilineTextFieldView
import com.personal.bookmark_app.ui.components.TagInfoPopup
import com.personal.bookmark_app.ui.components.TextFieldView
import com.personal.bookmark_app.ui.model.BookmarkPreviewUiModel
import com.personal.bookmark_app.ui.model.BookmarkSource
import com.personal.bookmark_app.ui.model.FolderUiModel
import com.personal.bookmark_app.ui.model.TagOptionUiModel

@Composable
fun AddBookmarkScreen(
    uiState: AddBookmarkUiState,
    onSourceUrlChange: (String) -> Unit,
    onCreateTag: (String) -> Unit,
    onRetry: () -> Unit,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSourceChange: (BookmarkSource) -> Unit,

    onFolderSelected: (Long) -> Unit,
    onCreateFolderClick: (String) -> Unit,

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
                border = androidx.compose.foundation.BorderStroke(
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

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("來源網址 URL", style = MaterialTheme.typography.bodySmall)
                            ReparseAction(
                                isParsing = uiState.isParsing,
                                enabled = !uiState.isParsing && !uiState.isSaving && uiState.sourceUrl.isNotBlank(),
                                onClick = onReparseClick
                            )
                        }
                        TextFieldView(
                            value = uiState.sourceUrl,
                            onValueChange = onSourceUrlChange,
                            placeholder = "https://",
                            enabled = !uiState.isSaving
                        )
                        if (uiState.isParsing) {
                            androidx.compose.material3.LinearProgressIndicator(Modifier.fillMaxWidth())
                        }
                        if (uiState.content.isNotBlank()) Text(
                            uiState.content,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    BookmarkFolderSelector(
                        selectedFolder = folders.find { it.id == uiState.selectedFolderId },
                        enabled = !uiState.isLoading && !uiState.isSaving,
                        onClick = { showFolderSheet = true }
                    )
                    uiState.parseError?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                    if (uiState.isLoading) Text("載入中…")
                    if (uiState.isSaving) Text("儲存中…")
                    uiState.errorMessage?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                    if (folders.isEmpty() && !uiState.isLoading) {
                        androidx.compose.material3.TextButton(onClick = onRetry) { Text("重新載入") }
                        androidx.compose.material3.TextButton(onClick = {
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
//                    SourcePlatformSection(
//                        selectedSource = uiState.selectedSource,
//                        onSourceChange = onSourceChange
//                    )
//
//                    Spacer(modifier = Modifier.height(2.dp))

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
            onConfirm = { folderName ->

                onCreateFolderClick(folderName)

                showAddFolderDialog = false
            }
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
 * 1. 文章預覽、來源網址、收藏資料夾
 * ------------------------------------------------------- */

@Composable
private fun ReparseAction(
    isParsing: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (enabled) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = if (isParsing) "解析中…" else "重新解析",
            style = MaterialTheme.typography.bodySmall,
            color = contentColor
        )
    }
}

@Composable
private fun BookmarkPreviewCard(
    preview: BookmarkPreviewUiModel,
    folders: List<FolderUiModel>,
    selectedFolderId: Long,
    isParsing: Boolean,
    onFolderClick: () -> Unit,
    onReparseClick: () -> Unit
) {

    val selectedFolder = folders.find {
        it.id == selectedFolderId
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 來源網址標題
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "來源網址 URL",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ReparseAction(
                isParsing = isParsing,
                enabled = !isParsing,
                onClick = onReparseClick
            )
        }

        // 來源網址內容
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp)
                .padding(start = 0.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = preview.sourceUrl,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        BookmarkFolderSelector(
            selectedFolder = selectedFolder,
            enabled = true,
            onClick = onFolderClick
        )
    }
}

@Composable
private fun BookmarkFolderSelector(
    selectedFolder: FolderUiModel?,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "選擇收藏夾",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Folder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = selectedFolder?.title ?: "選擇收藏夾",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (selectedFolder != null) {
                    Text(
                        text = "${selectedFolder.count} 篇收藏",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


/* -------------------------------------------------------
 * 2. 來源平台
 * ------------------------------------------------------- */
//@Composable
//private fun SourcePlatformSection(
//    selectedSource: BookmarkSource,
//    onSourceChange: (BookmarkSource) -> Unit
//) {
//    Column(
//        verticalArrangement = Arrangement.spacedBy(2.dp)
//    ) {
//        Text(
//            text = "來源平台識別",
//            style = MaterialTheme.typography.bodySmall,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .horizontalScroll(rememberScrollState()),
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            BookmarkSource.entries.forEach { source ->
//                FilterChip(
//                    selected = selectedSource == source,
//                    onClick = {
//                        onSourceChange(source)
//                    },
//                    label = {
//                        Text(
//                            text = source.displayName,
//                            style = MaterialTheme.typography.labelLarge
//                        )
//                    }
//                )
//            }
//        }
//    }
//}

/* -------------------------------------------------------
 * 3. 文章標題
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
 * 4. 個人札記
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
 * 5. 索引標籤
 * ------------------------------------------------------- */

@Composable
private fun BookmarkTagSection(
    tags: List<TagOptionUiModel>,
    selectedTagIds: Set<String>,
    onTagClick: (String) -> Unit,
    onAddCustomTagClick: () -> Unit
) {
    var showTagInfo by remember { mutableStateOf(false) }
    var isTagsExpanded by remember { mutableStateOf(false) }
    val canAddTag = selectedTagIds.size < 5

    // 標籤標題及選取數量
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "標籤分類",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(5.dp))

            Box {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "標籤篩選說明",
                    modifier = Modifier
                        .size(15.dp)
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
                        message = "顯示此資料夾內既有標籤，亦可自訂新增；最多可選取 5 個標籤。"
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "已選 ${selectedTagIds.size}/5 個",
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 5.dp
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (tags.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isTagsExpanded = !isTagsExpanded
                        }
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = if (isTagsExpanded) "收合" else "展開",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Icon(
                        imageVector = if (isTagsExpanded) {
                            Icons.Default.ExpandLess
                        } else {
                            Icons.Default.ExpandMore
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // 標籤選項
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxLines = if (isTagsExpanded) Int.MAX_VALUE else 2
    ) {
        tags.forEach { tag ->
            val isSelected = tag.id in selectedTagIds

            Surface(
                modifier = Modifier
                    .height(36.dp)
                    .clickable {
                        if (isSelected || selectedTagIds.size < 5) {
                            onTagClick(tag.id)
                        }
                    },
                shape = RoundedCornerShape(6.dp),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surface
                },
                border = if (isSelected) {
                    null
                } else {
                    BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSelected) {
                            "# ${tag.name} ×"
                        } else {
                            "+ ${tag.name}"
                        },
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

    // 自訂新標籤
    Surface(
        modifier = Modifier
            .height(36.dp)
            .clickable(
                enabled = canAddTag,
                onClick = onAddCustomTagClick
            ),
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "自訂新標籤",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


/* -------------------------------------------------------
 * 6. 今日拾藏
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
