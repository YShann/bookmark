package com.personal.bookmark_app.ui.screen.dailypicks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.ui.theme.BookmarkTextSecondary

@Composable
fun DailyPicksInsufficientState(
    bookmarkCount: Int,
    onAddBookmarkClick: () -> Unit
) {
    val remainingCount = 3 - bookmarkCount

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // 提示標題
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = if (remainingCount > 0) {
                    "再收藏一些內容吧！"
                } else {
                    "已經集滿 3 則拾藏！"
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 提示說明
        Text(
            text = if (remainingCount > 0) {
                "目前有 $bookmarkCount 則收藏，再收藏 $remainingCount 則，就能湊滿每日 3 則拾藏。"
            } else {
                "目前已經有 3 則收藏，繼續收藏更多內容，就能讓每日推薦有更多變化。"
            },
            style = MaterialTheme.typography.bodySmall,
            color = BookmarkTextSecondary
        )

        // 繼續收藏
        TextButton(
            onClick = onAddBookmarkClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = "繼續收藏",
                modifier = Modifier.padding(start = 4.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
