package com.personal.bookmark_app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.data.remote.backendMessage
import com.personal.bookmark_app.ui.utils.MAX_FOLDER_NAME_LENGTH
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


@Composable
fun AddFolderDialog(
    onDismiss: () -> Unit,
    onConfirm: suspend (String) -> Unit
) {
    var folderName by rememberSaveable {
        mutableStateOf("")
    }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val trimmedName = folderName.trim()
    val nameTooLong = trimmedName.length > MAX_FOLDER_NAME_LENGTH
    val fieldError = if (nameTooLong) "收藏夾名稱不能超過 $MAX_FOLDER_NAME_LENGTH 個字" else errorMessage

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = {
            Text(
                text = "新增收藏夾",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = folderName,
                onValueChange = {
                    folderName = it
                    errorMessage = null
                },
                enabled = !isSubmitting,
                isError = fieldError != null,
                supportingText = fieldError?.let { message ->
                    { Text(message, style = MaterialTheme.typography.bodySmall) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text("請輸入收藏夾名稱")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.FolderOpen,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(8.dp)
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isSubmitting) return@TextButton
                    isSubmitting = true
                    errorMessage = null
                    scope.launch {
                        try {
                            onConfirm(trimmedName)
                            onDismiss()
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (error: Exception) {
                            errorMessage = when {
                                error is HttpException && error.code() == 409 ->
                                    "收藏夾名稱已存在，請換個名稱"

                                error is HttpException && error.code() == 400 ->
                                    error.backendMessage() ?: "請確認收藏夾名稱"

                                error is IOException ->
                                    "連線至伺服器失敗，請稍後再試"

                                else -> "新增收藏夾失敗，請稍後再試"
                            }
                        } finally {
                            isSubmitting = false
                        }
                    }
                },
                enabled = trimmedName.isNotEmpty() && !nameTooLong && !isSubmitting
            ) {
                Text(if (isSubmitting) "新增中…" else "新增")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSubmitting
            ) {
                Text("取消")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}
