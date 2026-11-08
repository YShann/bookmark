package com.personal.bookmark_app.ui.screen.dailypicks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.ui.model.DaliyBookmarkUiModel
import com.personal.bookmark_app.ui.theme.BookmarkTextSecondary
import com.personal.bookmark_app.ui.utils.openBookmarkUrl
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImageState
import com.skydoves.landscapist.coil3.CoilImage

@Composable
fun DailyBookmarkCard(
    bookmark: DaliyBookmarkUiModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasThumbnail by remember(bookmark.id, bookmark.imageUrl) {
        mutableStateOf(!bookmark.imageUrl.isNullOrBlank())
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                openBookmarkUrl(context, bookmark.originalUrl)
            },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${bookmark.source.displayName} · ${bookmark.date}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = bookmark.title,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

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
                    Text(
                        text = bookmark.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (bookmark.tags.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(count = bookmark.tags.size) { index ->
                                DailyBookmarkTag(bookmark.tags[index])
                            }
                        }
                    }
                }

                if (hasThumbnail) {
                    Spacer(Modifier.width(12.dp))

                    CoilImage(
                        imageModel = { bookmark.imageUrl },
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

            // 筆記／網址區塊保留原 BookmarkCard 的呈現方式。
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            bookmark.note
                ?.takeIf { it.isNotBlank() }
                ?.let { DailyBookmarkNote(it) }
                ?: DailyBookmarkUrl(bookmark.originalUrl)
        }
    }
}

@Composable
private fun DailyBookmarkTag(tag: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = "# $tag",
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun DailyBookmarkNote(note: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.06f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Outlined.EditNote,
            contentDescription = "個人筆記",
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = note,
            modifier = Modifier.weight(1f),
            color = BookmarkTextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun DailyBookmarkUrl(url: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.06f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Link,
            contentDescription = "收藏網址",
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = url,
            modifier = Modifier.weight(1f),
            color = BookmarkTextSecondary,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
