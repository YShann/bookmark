package com.personal.bookmark_app.ui.components.bookmark

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.ui.model.BookmarkUiModel
import com.personal.bookmark_app.ui.theme.BookmarkTextSecondary
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import com.skydoves.landscapist.coil3.CoilImageState


@Composable
fun BookmarkCard(
    item: BookmarkUiModel,
    showActions: Boolean = true,
    isBatchManaging: Boolean = false,
    isSelected: Boolean = false,
    onSelectionClick: () -> Unit = {},
    onClick: () -> Unit,
    onEditTitleClick: (String) -> Unit = {},
    onEditNoteClick: (String) -> Unit = {},
    availableTags: List<String> = emptyList(),
    onTagsSave: (List<String>) -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onMoveClick: (() -> Unit)? = null
) {
    var hasThumbnail by remember(item.id, item.imageUrl) {
        mutableStateOf(item.imageUrl.isNotBlank())
    }

    // 控制更多操作選單
    var expanded by remember {
        mutableStateOf(false)
    }

    var showEditTitleSheet by remember {
        mutableStateOf(false)
    }

    var showEditNoteSheet by remember {
        mutableStateOf(false)
    }

    var showEditTagsSheet by remember {
        mutableStateOf(false)
    }

    var showDeleteSheet by remember {
        mutableStateOf(false)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header：來源、日期、更多操作
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 批量管理 Checkbox
                if (isBatchManaging) {
                    Surface(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = onSelectionClick),
                        shape = CircleShape,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        border = if (isSelected) {
                            null
                        } else {
                            BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    ) {
                        if (isSelected) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "已選取",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    Spacer(Modifier.width(8.dp))
                }

                Text(
                    text = "${item.source} · ${item.date}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (showActions) {
                    Box {
                        IconButton(
                            onClick = { expanded = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "更多操作",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        BookmarkOptionsMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                            },
                            onEditTitleClick = {
                                expanded = false
                                showEditTitleSheet = true
                            },
                            onEditNoteClick = {
                                expanded = false
                                showEditNoteSheet = true
                            },
                            onEditTagsClick = {
                                expanded = false
                                showEditTagsSheet = true
                            },
                            onMoveClick = onMoveClick?.let { move ->
                                {
                                    expanded = false
                                    move()
                                }
                            },
                            onDeleteClick = {
                                expanded = false
                                showDeleteSheet = true
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // 標題：獨立一整列
            Text(
                text = item.title,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            // 收藏主要內容：左側縮圖，右側描述與標籤
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = if (hasThumbnail) {
                        Modifier.weight(1f)
                    } else {
                        Modifier.fillMaxWidth()
                    },
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 文章描述
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // 標籤：單行橫向滑動
                    if (item.tags.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(count = item.tags.size) { index ->
                                BookmarkTag(tag = item.tags[index])
                            }
                        }
                    }
                }

                if (hasThumbnail) {
                    Spacer(Modifier.width(12.dp))

                    // 收藏縮圖；失敗時移除欄位，讓描述與標籤使用完整寬度。
                    CoilImage(
                        imageModel = { item.imageUrl },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop
                        ),
                        onImageStateChanged = { state ->
                            if (state is CoilImageState.Failure) {
                                hasThumbnail = false
                            }
                        },
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(7.dp))
                    )
                }
            }

            // 筆記
            item.note?.takeIf { it.isNotBlank() }?.let { note ->
                Spacer(Modifier.height(8.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Spacer(Modifier.height(8.dp))

                BookmarkNote(note = note)
            }
        }
    }
    if (showEditTitleSheet) {
        EditTitleBottomSheet(
            item = item,
            onDismiss = {
                showEditTitleSheet = false
            },
            onSave = { newTitle ->
                onEditTitleClick(newTitle)
                showEditTitleSheet = false
            }
        )
    }

    if (showEditNoteSheet) {
        EditNoteBottomSheet(
            currentNote = item.note.orEmpty(),
            onDismiss = {
                showEditNoteSheet = false
            },
            onSave = { newNote ->
                onEditNoteClick(newNote)
                showEditNoteSheet = false
            }
        )
    }

    if (showEditTagsSheet) {
        EditTagsBottomSheet(
            currentTags = item.tags,
            availableTags = availableTags,
            onDismiss = {
                showEditTagsSheet = false
            },
            onSave = { newTags ->
                onTagsSave(newTags)
                showEditTagsSheet = false
            }
        )
    }

    if (showDeleteSheet) {
        DeleteBookmarkBottomSheet(
            item = item,
            onDismiss = {
                showDeleteSheet = false
            },
            onConfirmDelete = {
                onDeleteClick()
                showDeleteSheet = false
            }
        )
    }
}


@Composable
private fun BookmarkNote(note: String) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var hasMultipleLines by remember(note) {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
            )
            .then(
                if (hasMultipleLines) {
                    Modifier.clickable { isExpanded = !isExpanded }
                } else {
                    Modifier
                }
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Outlined.EditNote,
            contentDescription = "個人筆記",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = note,
            modifier = Modifier.weight(1f),
            color = BookmarkTextSecondary,
            style = MaterialTheme.typography.bodySmall,
            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
            onTextLayout = { result ->
                // 展開後文字不再溢出，但仍需保留「可收合」狀態與圖示。
                if (result.hasVisualOverflow) hasMultipleLines = true
            }
        )

        if (hasMultipleLines) {
            Icon(
                imageVector = if (isExpanded) {
                    Icons.Default.ExpandLess
                } else {
                    Icons.Default.ExpandMore
                },
                contentDescription = if (isExpanded) "收合筆記" else "展開筆記",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun BookmarkTag(
    tag: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = "# $tag",
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 3.dp
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal
        )
    }
}
