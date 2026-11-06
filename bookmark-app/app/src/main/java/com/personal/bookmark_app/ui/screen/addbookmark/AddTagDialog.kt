package com.personal.bookmark_app.ui.screen.addbookmark

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.ui.model.TagOptionUiModel

@Composable
fun AddTagDialog(
    existingTags: List<TagOptionUiModel>,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var tagName by rememberSaveable {
        mutableStateOf("")
    }

    var errorMessage by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "新增自訂標籤",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = tagName,
                    onValueChange = {
                        tagName = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = "標籤",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    placeholder = {
                        Text("請輸入標籤名稱")
                    },
                    shape = RoundedCornerShape(8.dp),
                    isError = errorMessage != null
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    val name = tagName.trim()

                    when {
                        name.isBlank() -> {
                            errorMessage = "請輸入標籤名稱"
                        }

                        existingTags.any {
                            it.name.equals(name, ignoreCase = true)
                        } -> {
                            errorMessage = "此標籤已經存在"
                        }

                        else -> {
                            onConfirm(name)
                        }
                    }
                },
                enabled = tagName.isNotBlank()
            ) {
                Text("新增")
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("取消")
            }
        },

        containerColor = MaterialTheme.colorScheme.surface
    )
}