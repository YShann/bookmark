package com.personal.bookmark_app.ui.components.bookmark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun BookmarkOptionsMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onEditTitleClick: () -> Unit,
    onEditNoteClick: () -> Unit,
    onEditTagsClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMoveClick: (() -> Unit)? = null
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        modifier = Modifier.size(18.dp),
                        contentDescription = null
                    )

                    Text(
                        text = "編輯標題",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            },
            onClick = onEditTitleClick
        )

        // 編輯筆記
        DropdownMenuItem(
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        modifier = Modifier.size(18.dp),
                        contentDescription = null
                    )

                    Text(
                        text = "編輯筆記",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            },
            onClick = onEditNoteClick
        )

        // 編輯標籤
        DropdownMenuItem(
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        modifier = Modifier.size(18.dp),
                        contentDescription = null
                    )

                    Text(
                        text = "編輯標籤",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            },
            onClick = onEditTagsClick
        )

        // 分隔線
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant
        )

        if (onMoveClick != null) {
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            modifier = Modifier.size(18.dp),
                            contentDescription = null
                        )
                        Text(
                            text = "移動至其他收藏夾",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                },
                onClick = onMoveClick
            )
        }

        // 刪除收藏
        DropdownMenuItem(
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        modifier = Modifier.size(18.dp),
                        contentDescription = null
                    )

                    Text(
                        text = "刪除這則收藏",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            onClick = onDeleteClick
        )
    }
}
