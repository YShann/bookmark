package com.personal.bookmark_app.ui.components.bookmark

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.ui.components.BottomSheetDragHandle
import com.personal.bookmark_app.ui.components.TextFieldView
import com.personal.bookmark_app.ui.model.BookmarkUiModel
import com.personal.bookmark_app.ui.utils.MAX_BOOKMARK_TITLE_LENGTH

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditTitleBottomSheet(
    item: BookmarkUiModel,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    // 編輯中的標題
    var editedTitle by rememberSaveable(item.id) {
        mutableStateOf(item.title)
    }

    val isTitleChanged = editedTitle != item.title
    val isSaveEnabled = editedTitle.isNotBlank() &&
            editedTitle.trim().length <= MAX_BOOKMARK_TITLE_LENGTH &&
            isTitleChanged

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val keyboardController = LocalSoftwareKeyboardController.current
    val isKeyboardVisible = WindowInsets.isImeVisible

    BackHandler {
        if (isKeyboardVisible) {
            keyboardController?.hide()
        } else {
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false
        ),
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp
        ),
        dragHandle = {
            BottomSheetDragHandle()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 24.dp
                )
                .verticalScroll(rememberScrollState())
        ) {

            // 標題列
            EditTitleHeader(
                onDismiss = onDismiss
            )

            Spacer(Modifier.height(16.dp))

//            // 收藏預覽卡片
//            EditTitlePreviewCard(
//                item = item,
//                modifier = Modifier.padding(bottom = 20.dp)
//            )

            // 標題輸入區域
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 2.dp,
                            vertical = 6.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "收藏標題",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "${editedTitle.length} / $MAX_BOOKMARK_TITLE_LENGTH",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 標題輸入框
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    TextFieldView(
                        value = editedTitle,
                        onValueChange = { newTitle ->
                            if (newTitle.length <= MAX_BOOKMARK_TITLE_LENGTH) {
                                editedTitle = newTitle
                            }
                        },
                        placeholder = "請輸入收藏標題",
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 4.dp,
                                vertical = 4.dp
                            )
                    )
                }

                // 提示文字
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 4.dp,
                            vertical = 8.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = "建議保留清晰關鍵字，便於日後檢索與回顧",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // 儲存按鈕
            Button(
                onClick = {
                    onSave(editedTitle.trim())
                },
                enabled = isSaveEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Text(
                    text = "儲存標題變更",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // 取消返回
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = "取消返回",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EditTitleHeader(
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = "編輯收藏標題",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "關閉",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

//@Composable
//private fun EditTitlePreviewCard(
//    item: BookmarkUiModel,
//    modifier: Modifier = Modifier
//) {
//    Surface(
//        modifier = modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(8.dp),
//        color = MaterialTheme.colorScheme.surface,
//        shadowElevation = 2.dp
//    ) {
//        Row(
//            modifier = Modifier.padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//
//            // 收藏縮圖
//            CoilImage(
//                imageModel = { item.imageUrl },
//                imageOptions = ImageOptions(
//                    contentScale = ContentScale.Crop
//                ),
//                modifier = Modifier
//                    .size(48.dp)
//                    .clip(RoundedCornerShape(4.dp))
//            )
//
//            Spacer(Modifier.width(12.dp))
//
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .size(6.dp)
//                            .background(
//                                color = MaterialTheme.colorScheme.tertiary,
//                                shape = CircleShape
//                            )
//                    )
//
//                    Spacer(Modifier.width(6.dp))
//
//                    Text(
//                        text = item.source,
//                        style = MaterialTheme.typography.labelSmall,
//                        color = MaterialTheme.colorScheme.tertiary
//                    )
//
//                    Spacer(Modifier.width(6.dp))
//
//                    Text(
//                        text = "·",
//                        color = MaterialTheme.colorScheme.outline
//                    )
//
//                    Spacer(Modifier.width(6.dp))
//
//                    Text(
//                        text = item.date,
//                        style = MaterialTheme.typography.labelSmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//
//                Spacer(Modifier.height(4.dp))
//
//                Text(
//                    text = item.title,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurface,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//            }
//        }
//    }
//}
