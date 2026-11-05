package com.personal.bookmark_app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.ui.model.BookmarkUiModel
import com.personal.bookmark_app.ui.theme.BookmarkTextSecondary
import com.personal.bookmark_app.ui.utils.openBookmarkUrl
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage

@Composable
fun BookmarkCard(
    bookmark: BookmarkUiModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                openBookmarkUrl(
                    context = context,
                    url = bookmark.originalUrl
                )
            },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BookmarkMetadata(
                source = bookmark.source.displayName,
                tags = bookmark.tags,
                date = bookmark.date
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = bookmark.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = bookmark.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BookmarkTextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                bookmark.imageUrl?.let { imageUrl ->
                    CoilImage(
                        imageModel = { imageUrl },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop
                        ),
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline
            )
            bookmark.note
                ?.takeIf { it.isNotBlank() }
                ?.let { BookmarkNote(it) }
                ?: BookmarkUrl(bookmark.originalUrl)
        }
    }
}

@Composable
private fun BookmarkMetadata(
    source: String,
    tags: List<String>,
    date: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.primary.copy(
                alpha = 0.10f
            )
        ) {
            Text(
                text = source,
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 5.dp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Text(
            text = tags.joinToString(" ") { "#$it" },
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = "•",
            color = BookmarkTextSecondary
        )

        Text(
            text = date,
            color = BookmarkTextSecondary,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
    }
}

@Composable
private fun BookmarkNote(note: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
            )
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
private fun BookmarkUrl(url: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
            )
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