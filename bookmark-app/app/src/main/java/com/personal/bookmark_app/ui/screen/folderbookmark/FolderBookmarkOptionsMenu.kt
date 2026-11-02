package com.personal.bookmark_app.ui.screen.folderbookmark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FolderBookmarkOptionsMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onEditTitleClick: () -> Unit,
    onEditNoteClick: () -> Unit,
    onEditTagsClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .width(220.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {

        // 編輯標題
        DropdownMenuItem(
            text = {
                Text(
                    text = "編輯標題",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            onClick = onEditTitleClick,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
            }
        )

        // 編輯筆記
        DropdownMenuItem(
            text = {
                Text(
                    text = "編輯筆記",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            onClick = onEditNoteClick,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = null
                )
            }
        )

        // 編輯標籤
        DropdownMenuItem(
            text = {
                Text(
                    text = "編輯標籤",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            onClick = onEditTagsClick,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocalOffer,
                    contentDescription = null
                )
            }
        )

        // 分隔線
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // 刪除收藏
        DropdownMenuItem(
            text = {
                Text(
                    text = "刪除這則收藏",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            },
            onClick = onDeleteClick,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        )
    }
}