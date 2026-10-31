package com.personal.bookmark_app.ui.screen.addbookmark

import com.personal.bookmark_app.ui.mock_data.AddBookmarkMockData
import com.personal.bookmark_app.ui.model.BookmarkSource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.ui.components.TextFieldView
import com.personal.bookmark_app.ui.model.FolderUiModel
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookmarkScreen(
    uiState: AddBookmarkUiState,

    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSourceChange: (BookmarkSource) -> Unit,

    onFolderClick: () -> Unit,
    onTagClick: (String) -> Unit,
    onAddCustomTagClick: () -> Unit,

    onDailyPicksChange: (Boolean) -> Unit,

    onReparseClick: () -> Unit,
    onClearTitleClick: () -> Unit,

    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "新增典藏",
                        style = MaterialTheme.typography.titleLarge
                    )
                },

                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },

                actions = {
                    Button(
                        onClick = onSaveClick,
                        enabled = uiState.isFormValid,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "儲存",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            )
        }

    ) { innerPadding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. 文章預覽、來源網址、收藏資料夾
            BookmarkPreviewCard(
                preview = AddBookmarkMockData.preview,
                folders = AddBookmarkMockData.folders,
                selectedFolderId = uiState.selectedFolderId,
                isParsing = uiState.isParsing,
                onFolderClick = onFolderClick,
                onReparseClick = onReparseClick
            )

            // 2. 來源平台
            SourcePlatformSection(
                selectedSource = uiState.selectedSource,
                onSourceChange = onSourceChange
            )

            // 3. 文章標題
            BookmarkTitleSection(
                title = uiState.title,
                onTitleChange = onTitleChange,
                onClearClick = onClearTitleClick
            )

            // 4. 個人札記
            BookmarkNoteSection(
                note = uiState.note,
                onNoteChange = onNoteChange
            )

            // 5. 索引標籤
            BookmarkTagSection(
                tags = AddBookmarkMockData.tags,
                selectedTagIds = uiState.selectedTagIds,
                onTagClick = onTagClick,
                onAddCustomTagClick = onAddCustomTagClick
            )

            // 6. 是否加入今日拾藏
            DailyPicksOption(
                checked = uiState.isAddedToDailyPicks,
                onCheckedChange = onDailyPicksChange
            )
        }
    }
}


/* -------------------------------------------------------
 * 1. 文章預覽、來源網址、收藏資料夾
 * ------------------------------------------------------- */

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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // 文章封面
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {

                CoilImage(
                    imageModel = { preview.imageUrl },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop
                    ),
                    modifier = Modifier.fillMaxSize()
                )

                // 解析狀態
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {

                    Row(
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 5.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = if (preview.isParsed) {
                                "已萃取文章封面與摘要"
                            } else {
                                "尚未完成解析"
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            // 來源網址標題
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "來源網址 URL",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                TextButton(
                    onClick = onReparseClick,
                    enabled = !isParsing
                ) {

                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = if (isParsing) "解析中" else "重新解析"
                    )
                }
            }

            // 來源網址內容
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
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

            // 收藏資料夾標題
            Text(
                text = "收藏分類夾",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 資料夾選擇
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = onFolderClick)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // 資料夾封面
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = selectedFolder?.title ?: "未選擇資料夾",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "${selectedFolder?.count ?: 0} 篇收藏",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "選擇資料夾",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


/* -------------------------------------------------------
 * 2. 來源平台
 * ------------------------------------------------------- */

@Composable
private fun SourcePlatformSection(
    selectedSource: BookmarkSource,
    onSourceChange: (BookmarkSource) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = "來源平台識別",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            BookmarkSource.entries.forEach { source ->

                FilterChip(
                    selected = selectedSource == source,
                    onClick = {
                        onSourceChange(source)
                    },
                    label = {
                        Text(
                            text = source.displayName,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
            }
        }
    }
}


/* -------------------------------------------------------
 * 3. 文章標題
 * ------------------------------------------------------- */

@Composable
private fun BookmarkTitleSection(
    title: String,
    onTitleChange: (String) -> Unit,
    onClearClick: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "文章或影音標題",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TextButton(
                onClick = onClearClick,
                enabled = title.isNotEmpty()
            ) {

                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text("清空")
            }
        }

        TextFieldView(
            value = title,
            onValueChange = onTitleChange,
            placeholder = "請輸入文章或影音標題",
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}


/* -------------------------------------------------------
 * 4. 個人札記
 * ------------------------------------------------------- */

@Composable
private fun BookmarkNoteSection(
    note: String,
    onNoteChange: (String) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "個人拾藏札記",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(15.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "備註",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        TextFieldView(
            value = note,
            onValueChange = onNoteChange,
            placeholder = "寫下收藏這篇文章的原因、想法或未來想做的事...",
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        // 字數統計
        Text(
            text = "${note.length} 字",
            modifier = Modifier.align(Alignment.End),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
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

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

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
                    text = "索引標籤分類",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(5.dp))

                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "標籤說明",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp)
                )
            }

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
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // 標籤選項
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            tags.forEach { tag ->

                val isSelected = tag.id in selectedTagIds

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        // 已選取的標籤可以取消
                        // 未選取的標籤必須尚未達到上限
                        if (isSelected || selectedTagIds.size < 5) {
                            onTagClick(tag.id)
                        }
                    },
                    label = {
                        Text(
                            text = if (isSelected) {
                                "#${tag.name} ×"
                            } else {
                                "+ ${tag.name}"
                            }
                        )
                    }
                )
            }

            // 自訂新標籤
            FilterChip(
                selected = false,
                onClick = onAddCustomTagClick,
                enabled = selectedTagIds.size < 5,
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text("自訂新標籤")
                    }
                }
            )
        }

        // 提示文字
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "顯示此資料夾內既有標籤，亦可自訂新增；最多可選取 5 個標籤。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            .padding(12.dp),
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
            text = "加入今日拾藏備選庫",
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

//
//import androidx.compose.runtime.*
//import androidx.compose.foundation.*
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.*
//import androidx.compose.ui.draw.*
//import androidx.compose.ui.graphics.*
//import androidx.compose.ui.text.font.*
//import androidx.compose.ui.unit.*
//import androidx.compose.ui.text.*
//import androidx.compose.ui.layout.*
//import androidx.compose.ui.text.style.TextOverflow
//import com.personal.bookmark_app.ui.components.TextFieldView
//import com.skydoves.landscapist.*
//import com.skydoves.landscapist.coil3.*
//
//@Composable
//fun AddBookmarkScreen() {
//    val textField1 = remember { mutableStateOf("") }
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                color = Color(0xFFFBF9F3),
//            )
//    ){
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f)
//                .verticalScroll(rememberScrollState())
//        ){
//            Box{
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ){
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .background(color = Color(0xFFFBF9F3))
//                            .padding(top = 12.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
//                    ){
//                        Row(
//                            horizontalArrangement = Arrangement.SpaceBetween,
//                            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                        ){
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                            ){
//                                CoilImage(
//                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/l4k9l7d1_expires_30_days.png"},
//                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                    modifier = Modifier
//                                        .padding(end = 1.dp,)
//                                        .width(13.dp)
//                                        .height(22.dp)
//                                )
//                                Column(
//                                    horizontalAlignment = Alignment.CenterHorizontally,
//                                ){
//                                    Column(
//                                    ){
//                                        Column(
//                                            modifier = Modifier
//                                                .width(12.dp)
//                                                .height(28.dp)
//                                        ){
//                                        }
//                                        Text("新增典藏",
//                                            color = Color(0xFF1F1A17),
//                                            fontSize = 28.sp,
//                                            fontWeight = FontWeight.Bold,
//                                            modifier = Modifier
//                                                .padding(start = 11.dp,)
//                                        )
//                                    }
//                                }
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .clip(shape = RoundedCornerShape(8.dp))
//                                    .background(
//                                        color = Color(0xFF5A6A55),
//                                        shape = RoundedCornerShape(8.dp)
//                                    )
//                                    .shadow(
//                                        elevation = 4.dp,
//                                        spotColor = Color(0x2E000000),
//                                    )
//                                    .clickable { println("Pressed!") }
//                                    .padding(vertical = 9.dp,horizontal = 20.dp,)
//                            ){
//                                Text("儲存",
//                                    color = Color(0xFFFFFFFF),
//                                    fontSize = 16.sp,
//                                    fontWeight = FontWeight.Bold,
//                                )
//                            }
//                        }
//                        Column(
//                            modifier = Modifier
//                                .height(12.dp)
//                                .fillMaxWidth()
//                        ){
//                        }
//                        Column(
//                            modifier = Modifier
//                                .clip(shape = RoundedCornerShape(14.dp))
//                                .fillMaxWidth()
//                                .background(
//                                    color = Color(0xFFFFFFFF),
//                                    shape = RoundedCornerShape(14.dp)
//                                )
//                                .padding(16.dp)
//                        ){
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(144.dp)
//                                    .clip(RoundedCornerShape(8.dp))
//                            ){
//                                CoilImage(
//                                    imageModel = { "https://lh3.googleusercontent.com/aida-public/AB6AXuAc0UhWKR7yzzBJLrjr_NvAPuEaEmluA83gfl5sYhpgyXWeacRwbSmdvYvSsPZ5StI74u7lmSGgnvT04Acml3KSHGeQkWe_-yvc5t5gGKdIzKN7zJMNSnhBb9M0DgchNfC0MxVw-DEqtNdcbucCDO3DzTmIFfuWzXppwR4Zjj5crEGF_npWDAouTEpIP28sm12tzNAQwfgh2-VPfdVHQgGi1fs5Q0OuMFzJUokTDR8crlXlseMSJTA0zw" },
//                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                    modifier = Modifier.fillMaxSize(),
//                                )
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier
//                                        .align(Alignment.BottomStart)
//                                        .padding(8.dp)
//                                        .clip(shape = RoundedCornerShape(6.dp))
//                                        .background(
//                                            color = Color(0xFFF3EFE4),
//                                            shape = RoundedCornerShape(6.dp)
//                                        )
//                                        .shadow(
//                                            elevation = 2.dp,
//                                            spotColor = Color(0x0D000000),
//                                        )
//                                        .padding(vertical = 5.dp,horizontal = 10.dp,)
//                                ){
//                                    Text("✦",
//                                        color = Color(0xFFD08A5A),
//                                        fontSize = 12.sp,
//                                        fontWeight = FontWeight.Bold,
//                                        modifier = Modifier
//                                            .padding(end = 3.dp,)
//                                    )
//                                    Column(
//                                        modifier = Modifier
//                                            .width(8.dp)
//                                            .height(21.dp)
//                                    ){
//                                    }
//                                    Text("已萃取文章封面與摘要",
//                                        color = Color(0xFF2A241F),
//                                        fontSize = 14.sp,
//                                        fontWeight = FontWeight.Bold,
//                                    )
//                                }
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .height(16.dp)
//                                    .fillMaxWidth()
//                            ){
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                            ){
//                                Row(
//                                    horizontalArrangement = Arrangement.SpaceBetween,
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                ){
//                                    Text("來源網址 URL",
//                                        color = Color(0xFF7A746B),
//                                        fontSize = 15.sp,
//                                        fontWeight = FontWeight.Bold,
//                                    )
//                                    Row(
//                                        verticalAlignment = Alignment.CenterVertically,
//                                    ){
//                                        CoilImage(
//                                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/9ud9vkij_expires_30_days.png"},
//                                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                            modifier = Modifier
//                                                .width(14.dp)
//                                                .height(14.dp)
//                                        )
//                                        Column(
//                                            modifier = Modifier
//                                                .width(4.dp)
//                                                .height(22.dp)
//                                        ){
//                                        }
//                                        Text("重新解析",
//                                            color = Color(0xFFA35F3F),
//                                            fontSize = 15.sp,
//                                            fontWeight = FontWeight.Bold,
//                                        )
//                                    }
//                                }
//                                Column(
//                                    modifier = Modifier
//                                        .height(9.dp)
//                                        .fillMaxWidth()
//                                ){
//                                }
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier
//                                        .clip(shape = RoundedCornerShape(6.dp))
//                                        .fillMaxWidth()
//                                        .background(
//                                            color = Color(0xFFF4F1E8),
//                                            shape = RoundedCornerShape(6.dp)
//                                        )
//                                        .padding(vertical = 11.dp,)
//                                ){
//                                    CoilImage(
//                                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/f1df3ryg_expires_30_days.png"},
//                                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                        modifier = Modifier
//                                            .padding(start = 18.dp,)
//                                            .clip(shape = RoundedCornerShape(6.dp))
//                                            .width(16.dp)
//                                            .height(12.dp)
//                                    )
//                                    Column(
//                                        modifier = Modifier
//                                            .width(14.dp)
//                                            .height(24.dp)
//                                    ){
//                                    }
//                                    Text("https://www.threads.net/@tokyo_editorial...",
//                                        color = Color(0xFF8A847A),
//                                        fontSize = 13.sp,
//                                        maxLines = 1,
//                                        overflow = TextOverflow.Ellipsis,
//                                        modifier = Modifier
//                                            .weight(1f)
//                                            .padding(end = 12.dp)
//                                    )
//                                }
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .height(17.dp)
//                                    .fillMaxWidth()
//                            ){
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(bottom = 1.dp,)
//                            ){
//                                Text("收藏分類夾",
//                                    color = Color(0xFF7A746B),
//                                    fontSize = 15.sp,
//                                    fontWeight = FontWeight.Bold,
//                                )
//                                Column(
//                                    modifier = Modifier
//                                        .height(7.dp)
//                                        .fillMaxWidth()
//                                ){
//                                }
//                                Row(
//                                    horizontalArrangement = Arrangement.SpaceBetween,
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier
//                                        .clip(shape = RoundedCornerShape(6.dp))
//                                        .fillMaxWidth()
//                                        .background(
//                                            color = Color(0xFFF4F1E8),
//                                            shape = RoundedCornerShape(6.dp)
//                                        )
//                                        .padding(12.dp)
//                                ){
//                                    Row(
//                                        verticalAlignment = Alignment.CenterVertically,
//                                    ){
//                                        CoilImage(
//                                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/e1m495y1_expires_30_days.png"},
//                                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                            modifier = Modifier
//                                                .clip(shape = RoundedCornerShape(6.dp))
//                                                .width(42.dp)
//                                                .height(40.dp)
//                                        )
//                                        Column(
//                                            modifier = Modifier
//                                                .width(12.dp)
//                                                .height(40.dp)
//                                        ){
//                                        }
//                                        Column(
//                                            horizontalAlignment = Alignment.CenterHorizontally,
//                                        ){
//                                            Column(
//                                                modifier = Modifier
//                                                    .padding(end = 14.dp,)
//                                            ){
//                                                Text("未分類",
//                                                    color = Color(0xFF2A241F),
//                                                    fontSize = 16.sp,
//                                                    fontWeight = FontWeight.Bold,
//                                                )
//                                            }
//                                            Column(
//                                                modifier = Modifier
//                                                    .width(61.dp)
//                                                    .height(3.dp)
//                                            ){
//                                            }
//                                            Text("18 篇收藏",
//                                                color = Color(0xFF7F786F),
//                                                fontSize = 13.sp,
//                                            )
//                                        }
//                                    }
//                                    CoilImage(
//                                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/pks2xafm_expires_30_days.png"},
//                                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                        modifier = Modifier
//                                            .clip(shape = RoundedCornerShape(6.dp))
//                                            .width(11.dp)
//                                            .height(17.dp)
//                                    )
//                                }
//                            }
//                        }
//                        Column(
//                            modifier = Modifier
//                                .height(12.dp)
//                                .fillMaxWidth()
//                        ){
//                        }
//                        Column(
//                            modifier = Modifier
//                                .padding(bottom = 33.dp,)
//                                .clip(shape = RoundedCornerShape(14.dp))
//                                .fillMaxWidth()
//                                .background(
//                                    color = Color(0xFFFFFFFF),
//                                    shape = RoundedCornerShape(14.dp)
//                                )
//                                .padding(vertical = 15.dp,)
//                        ){
//                            Column(
//                                modifier = Modifier
//                                    .padding(start = 16.dp,end = 2.dp,)
//                            ){
//                                Text("來源平台識別",
//                                    color = Color(0xFF7A746B),
//                                    fontSize = 15.sp,
//                                    fontWeight = FontWeight.Bold,
//                                )
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .padding(end = 16.dp,)
//                                    .height(11.dp)
//                                    .fillMaxWidth()
//                            ){
//                            }
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                                modifier = Modifier
//                                    .padding(end = 16.dp,)
//                                    .fillMaxWidth()
//                            ){
//                                Row(
//                                    horizontalArrangement = Arrangement.Center,
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier
//                                        .padding(start = 16.dp,)
//                                        .clip(shape = RoundedCornerShape(6.dp))
//                                        .weight(1f)
//                                        .background(
//                                            color = Color(0xFF6B7D63),
//                                            shape = RoundedCornerShape(6.dp)
//                                        )
//                                        .padding(vertical = 7.dp,)
//                                ){
//                                    CoilImage(
//                                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/5daq4ddc_expires_30_days.png"},
//                                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                        modifier = Modifier
//                                            .clip(shape = RoundedCornerShape(6.dp))
//                                            .width(13.dp)
//                                            .height(14.dp)
//                                    )
//                                    Column(
//                                        modifier = Modifier
//                                            .width(8.dp)
//                                            .height(22.dp)
//                                    ){
//                                    }
//                                    Text("@ Threads",
//                                        color = Color(0xFFFFFFFF),
//                                        fontSize = 14.sp,
//                                        fontWeight = FontWeight.Bold,
//                                    )
//                                }
//                                Column(
//                                    modifier = Modifier
//                                        .width(8.dp)
//                                        .height(38.dp)
//                                ){
//                                }
//                                Row(
//                                    horizontalArrangement = Arrangement.Center,
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier
//                                        .border(
//                                            width = 1.dp,
//                                            color = Color(0xFFE8E1D6),
//                                            shape = RoundedCornerShape(6.dp)
//                                        )
//                                        .clip(shape = RoundedCornerShape(6.dp))
//                                        .weight(1f)
//                                        .background(
//                                            color = Color(0xFFF1EEE6),
//                                            shape = RoundedCornerShape(6.dp)
//                                        )
//                                        .padding(vertical = 8.dp,)
//                                ){
//                                    CoilImage(
//                                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/2r8qdf00_expires_30_days.png"},
//                                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                        modifier = Modifier
//                                            .clip(shape = RoundedCornerShape(6.dp))
//                                            .width(13.dp)
//                                            .height(14.dp)
//                                    )
//                                    Column(
//                                        modifier = Modifier
//                                            .width(8.dp)
//                                            .height(22.dp)
//                                    ){
//                                    }
//                                    Text("Instagram",
//                                        color = Color(0xFF5F5A52),
//                                        fontSize = 14.sp,
//                                    )
//                                }
//                                Column(
//                                    modifier = Modifier
//                                        .width(8.dp)
//                                        .height(38.dp)
//                                ){
//                                }
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier
//                                        .border(
//                                            width = 1.dp,
//                                            color = Color(0xFFE8E1D6),
//                                            shape = RoundedCornerShape(6.dp)
//                                        )
//                                        .clip(shape = RoundedCornerShape(6.dp))
//                                        .background(
//                                            color = Color(0xFFF1EEE6),
//                                            shape = RoundedCornerShape(6.dp)
//                                        )
//                                        .padding(vertical = 8.dp,)
//                                ){
//                                    CoilImage(
//                                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/xs72p7c0_expires_30_days.png"},
//                                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                        modifier = Modifier
//                                            .padding(start = 14.dp,)
//                                            .clip(shape = RoundedCornerShape(6.dp))
//                                            .width(15.dp)
//                                            .height(15.dp)
//                                    )
//                                    Column(
//                                        modifier = Modifier
//                                            .width(8.dp)
//                                            .height(22.dp)
//                                    ){
//                                    }
//                                    Text("Dcard",
//                                        color = Color(0xFF5F5A52),
//                                        fontSize = 14.sp,
//                                        modifier = Modifier
//                                            .padding(end = 1.dp,)
//                                    )
//                                }
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .padding(end = 16.dp,)
//                                    .height(21.dp)
//                                    .fillMaxWidth()
//                            ){
//                            }
//                            Row(
//                                horizontalArrangement = Arrangement.SpaceBetween,
//                                verticalAlignment = Alignment.CenterVertically,
//                                modifier = Modifier
//                                    .padding(end = 16.dp,)
//                                    .fillMaxWidth()
//                                    .padding(start = 16.dp,)
//                            ){
//                                Text("文章或影音標題",
//                                    color = Color(0xFF7A746B),
//                                    fontSize = 15.sp,
//                                    fontWeight = FontWeight.Bold,
//                                )
//                                Text("清空",
//                                    color = Color(0xFF8A847A),
//                                    fontSize = 15.sp,
//                                )
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .padding(end = 16.dp,)
//                                    .height(8.dp)
//                                    .fillMaxWidth()
//                            ){
//                            }
//                            TextFieldView(
//                                placeholder = "東京神保町古書店的選書哲學與紙張香氣",
//                                value = textField1.value,
//                                onValueChange = { newText -> textField1.value = newText },
//                                textStyle = TextStyle(
//                                    color = Color(0xFF1F1A17),
//                                    fontSize = 20.sp,
//                                    fontWeight = FontWeight.Bold,
//                                ),
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp,)
//                                    .clip(shape = RoundedCornerShape(6.dp))
//                                    .fillMaxWidth()
//                                    .background(
//                                        color = Color(0xFFF4F1E8),
//                                        shape = RoundedCornerShape(6.dp)
//                                    )
//                                    .padding(vertical = 13.dp,horizontal = 16.dp,)
//                            )
//                            Column(
//                                modifier = Modifier
//                                    .padding(end = 16.dp,)
//                                    .height(21.dp)
//                                    .fillMaxWidth()
//                            ){
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .padding(bottom = 17.dp,end = 16.dp,)
//                                    .fillMaxWidth()
//                                    .padding(start = 16.dp,)
//                            ){
//                                Row(
//                                    horizontalArrangement = Arrangement.SpaceBetween,
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                ){
//                                    Text("個人拾藏札記",
//                                        color = Color(0xFF7A746B),
//                                        fontSize = 15.sp,
//                                        fontWeight = FontWeight.Bold,
//                                    )
//                                    Row(
//                                        verticalAlignment = Alignment.CenterVertically,
//                                    ){
//                                        CoilImage(
//                                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/gvh34owo_expires_30_days.png"},
//                                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                            modifier = Modifier
//                                                .width(14.dp)
//                                                .height(14.dp)
//                                        )
//                                        Column(
//                                            modifier = Modifier
//                                                .width(4.dp)
//                                                .height(22.dp)
//                                        ){
//                                        }
//                                        Text("備註",
//                                            color = Color(0xFFA35F3F),
//                                            fontSize = 15.sp,
//                                        )
//                                    }
//                                }
//                                Column(
//                                    modifier = Modifier
//                                        .height(9.dp)
//                                        .fillMaxWidth()
//                                ){
//                                }
//                                Column(
//                                    modifier = Modifier
//                                        .clip(shape = RoundedCornerShape(6.dp))
//                                        .fillMaxWidth()
//                                        .background(
//                                            color = Color(0xFFF4F1E8),
//                                            shape = RoundedCornerShape(6.dp)
//                                        )
//                                        .padding(16.dp)
//                                ){
//                                    Text("這家店專門收集 70 年代的建築雜誌，下次去東京一定要抽一個下午過去好好翻閱。",
//                                        color = Color(0xFF2A241F),
//                                        fontSize = 16.sp,
//                                    )
//                                }
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp,)
//                                    .fillMaxWidth()
//                            ){
//                                Column(
//                                    horizontalAlignment = Alignment.End,
//                                    modifier = Modifier
//                                        .padding(bottom = 24.dp,)
//                                        .fillMaxWidth()
//                                ){
//                                    Text("48 字",
//                                        color = Color(0xFF8A847A),
//                                        fontSize = 14.sp,
//                                        modifier = Modifier
//                                            .padding(end = 2.dp,)
//                                    )
//                                }
//                                Box{
//                                    Column(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                    ){
//                                        Box{
//                                            Column(
//                                                modifier = Modifier
//                                                    .fillMaxWidth()
//                                            ){
//                                                Column(
//                                                    modifier = Modifier
//                                                        .fillMaxWidth()
//                                                ){
//                                                    Row(
//                                                        horizontalArrangement = Arrangement.SpaceBetween,
//                                                        verticalAlignment = Alignment.CenterVertically,
//                                                        modifier = Modifier
//                                                            .padding(bottom = 12.dp,)
//                                                            .fillMaxWidth()
//                                                    ){
//                                                        Column(
//                                                            modifier = Modifier
//                                                                .padding(end = 23.dp,)
//                                                        ){
//                                                            Text("索引標籤分類",
//                                                                color = Color(0xFF7A746B),
//                                                                fontSize = 15.sp,
//                                                                fontWeight = FontWeight.Bold,
//                                                            )
//                                                        }
//                                                        Column(
//                                                            modifier = Modifier
//                                                                .clip(shape = RoundedCornerShape(9999.dp))
//                                                                .background(
//                                                                    color = Color(0xFFE8E4D8),
//                                                                    shape = RoundedCornerShape(9999.dp)
//                                                                )
//                                                                .clickable { println("Pressed!") }
//                                                                .padding(vertical = 4.dp,horizontal = 12.dp,)
//                                                        ){
//                                                            Text("已選 2/5 個",
//                                                                color = Color(0xFF5F5A52),
//                                                                fontSize = 13.sp,
//                                                                fontWeight = FontWeight.Bold,
//                                                            )
//                                                        }
//                                                    }
//                                                    Column(
//                                                        modifier = Modifier
//                                                            .fillMaxWidth()
//                                                    ){
//                                                        Row(
//                                                            verticalAlignment = Alignment.CenterVertically,
//                                                        ){
//                                                            Row(
//                                                                verticalAlignment = Alignment.CenterVertically,
//                                                                modifier = Modifier
//                                                                    .padding(end = 1.dp,)
//                                                                    .clip(shape = RoundedCornerShape(4.dp))
//                                                                    .background(
//                                                                        color = Color(0xFFF6C9BF),
//                                                                        shape = RoundedCornerShape(4.dp)
//                                                                    )
//                                                                    .padding(vertical = 6.dp,horizontal = 12.dp,)
//                                                            ){
//                                                                Text("#生活提案",
//                                                                    color = Color(0xFF7A3F2F),
//                                                                    fontSize = 15.sp,
//                                                                    fontWeight = FontWeight.Bold,
//                                                                    modifier = Modifier
//                                                                        .padding(end = 1.dp,)
//                                                                )
//                                                                Column(
//                                                                    modifier = Modifier
//                                                                        .width(8.dp)
//                                                                        .height(22.dp)
//                                                                ){
//                                                                }
//                                                                CoilImage(
//                                                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/xvpe0z0y_expires_30_days.png"},
//                                                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                                                    modifier = Modifier
//                                                                        .clip(shape = RoundedCornerShape(4.dp))
//                                                                        .width(10.dp)
//                                                                        .height(14.dp)
//                                                                )
//                                                            }
//                                                            Column(
//                                                                modifier = Modifier
//                                                                    .width(8.dp)
//                                                                    .height(34.dp)
//                                                            ){
//                                                            }
//                                                            Row(
//                                                                verticalAlignment = Alignment.CenterVertically,
//                                                                modifier = Modifier
//                                                                    .clip(shape = RoundedCornerShape(4.dp))
//                                                                    .background(
//                                                                        color = Color(0xFFF6C9BF),
//                                                                        shape = RoundedCornerShape(4.dp)
//                                                                    )
//                                                                    .padding(vertical = 6.dp,horizontal = 11.dp,)
//                                                            ){
//                                                                Text("#旅行靈感",
//                                                                    color = Color(0xFF7A3F2F),
//                                                                    fontSize = 15.sp,
//                                                                    fontWeight = FontWeight.Bold,
//                                                                    modifier = Modifier
//                                                                        .padding(end = 1.dp,)
//                                                                )
//                                                                Column(
//                                                                    horizontalAlignment = Alignment.CenterHorizontally,
//                                                                ){
//                                                                    Column(
//                                                                        horizontalAlignment = Alignment.CenterHorizontally,
//                                                                        modifier = Modifier
//                                                                            .padding(bottom = 4.dp,)
//                                                                    ){
//                                                                        Column(
//                                                                            modifier = Modifier
//                                                                                .width(8.dp)
//                                                                                .height(22.dp)
//                                                                        ){
//                                                                        }
//                                                                        CoilImage(
//                                                                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/4xho4vsr_expires_30_days.png"},
//                                                                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                                                            modifier = Modifier
//                                                                                .clip(shape = RoundedCornerShape(4.dp))
//                                                                                .width(10.dp)
//                                                                                .height(14.dp)
//                                                                        )
//                                                                    }
//                                                                }
//                                                            }
//                                                        }
//                                                        Column(
//                                                            modifier = Modifier
//                                                                .height(9.dp)
//                                                                .fillMaxWidth()
//                                                        ){
//                                                        }
//                                                        Row(
//                                                            verticalAlignment = Alignment.CenterVertically,
//                                                        ){
//                                                            Column(
//                                                                modifier = Modifier
//                                                                    .clip(shape = RoundedCornerShape(4.dp))
//                                                                    .background(
//                                                                        color = Color(0xFFECE7DC),
//                                                                        shape = RoundedCornerShape(4.dp)
//                                                                    )
//                                                                    .clickable { println("Pressed!") }
//                                                                    .padding(vertical = 6.dp,horizontal = 12.dp,)
//                                                            ){
//                                                                Text("＋ 閱讀清單",
//                                                                    color = Color(0xFF5F5A52),
//                                                                    fontSize = 15.sp,
//                                                                    fontWeight = FontWeight.Bold,
//                                                                )
//                                                            }
//                                                            Column(
//                                                                modifier = Modifier
//                                                                    .width(8.dp)
//                                                                    .height(34.dp)
//                                                            ){
//                                                            }
//                                                            Column(
//                                                                modifier = Modifier
//                                                                    .clip(shape = RoundedCornerShape(4.dp))
//                                                                    .background(
//                                                                        color = Color(0xFFECE7DC),
//                                                                        shape = RoundedCornerShape(4.dp)
//                                                                    )
//                                                                    .clickable { println("Pressed!") }
//                                                                    .padding(vertical = 6.dp,horizontal = 12.dp,)
//                                                            ){
//                                                                Text("＋ 設計靈感",
//                                                                    color = Color(0xFF5F5A52),
//                                                                    fontSize = 15.sp,
//                                                                    fontWeight = FontWeight.Bold,
//                                                                )
//                                                            }
//                                                        }
//                                                        Column(
//                                                            modifier = Modifier
//                                                                .height(8.dp)
//                                                                .fillMaxWidth()
//                                                        ){
//                                                        }
//                                                        Row(
//                                                            verticalAlignment = Alignment.CenterVertically,
//                                                        ){
//                                                            Column(
//                                                                modifier = Modifier
//                                                                    .clip(shape = RoundedCornerShape(4.dp))
//                                                                    .background(
//                                                                        color = Color(0xFFECE7DC),
//                                                                        shape = RoundedCornerShape(4.dp)
//                                                                    )
//                                                                    .clickable { println("Pressed!") }
//                                                                    .padding(vertical = 5.dp,horizontal = 12.dp,)
//                                                            ){
//                                                                Text("＋ 值得研究",
//                                                                    color = Color(0xFF5F5A52),
//                                                                    fontSize = 15.sp,
//                                                                    fontWeight = FontWeight.Bold,
//                                                                )
//                                                            }
//                                                            Column(
//                                                                modifier = Modifier
//                                                                    .width(8.dp)
//                                                                    .height(32.dp)
//                                                            ){
//                                                            }
//                                                            Column(
//                                                                modifier = Modifier
//                                                                    .clip(shape = RoundedCornerShape(4.dp))
//                                                                    .background(
//                                                                        color = Color(0xFFECE7DC),
//                                                                        shape = RoundedCornerShape(4.dp)
//                                                                    )
//                                                                    .clickable { println("Pressed!") }
//                                                                    .padding(vertical = 5.dp,horizontal = 12.dp,)
//                                                            ){
//                                                                Text("＋ 自訂新標籤",
//                                                                    color = Color(0xFF5F5A52),
//                                                                    fontSize = 15.sp,
//                                                                    fontWeight = FontWeight.Bold,
//                                                                )
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                            Column(
//                                                modifier = Modifier
//                                                    .offset(x = 33.dp, y = 20.dp)
//                                                    .align(Alignment.TopStart)
//                                                    .padding(start = 33.dp, bottom = 20.dp)
//                                                    .width(22.dp)
//                                                    .height(22.dp)
//                                                    .background(
//                                                        color = Color(0xFFFFFFFF),
//                                                    )
//                                            ){
//                                            }
//                                        }
//                                    }
//                                    Column(
//                                        horizontalAlignment = Alignment.CenterHorizontally,
//                                        modifier = Modifier
//                                            .offset(x = 29.dp, y = -41.dp)
//                                            .align(Alignment.TopStart)
//                                            .padding(start = 29.dp)
//                                    ){
//                                        Row(
//                                        ){
//                                            CoilImage(
//                                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/egiv4lkg_expires_30_days.png"},
//                                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                                modifier = Modifier
//                                                    .padding(end = 8.dp,)
//                                                    .width(12.dp)
//                                                    .height(16.dp)
//                                            )
//                                            Text("顯示此資料夾內既有標籤，亦可自訂新增；最多可選取 5 個標籤。",
//                                                color = Color(0xFF6F6A61),
//                                                fontSize = 14.sp,
//                                                modifier = Modifier
//                                                    .width(182.dp)
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .padding(end = 16.dp,)
//                                    .height(25.dp)
//                                    .fillMaxWidth()
//                            ){
//                            }
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                                modifier = Modifier
//                                    .padding(horizontal = 16.dp,)
//                                    .clip(shape = RoundedCornerShape(6.dp))
//                                    .fillMaxWidth()
//                                    .background(
//                                        color = Color(0xFFF4F1E8),
//                                        shape = RoundedCornerShape(6.dp)
//                                    )
//                                    .padding(12.dp)
//                            ){
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier
//                                        .padding(end = 1.dp,)
//                                        .weight(1f)
//                                ){
//                                    CoilImage(
//                                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/qp2afgxm_expires_30_days.png"},
//                                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                        modifier = Modifier
//                                            .padding(end = 1.dp,)
//                                            .clip(shape = RoundedCornerShape(6.dp))
//                                            .width(41.dp)
//                                            .height(42.dp)
//                                    )
//                                    Column(
//                                        modifier = Modifier
//                                            .weight(1f)
//                                            .padding(bottom = 9.dp,)
//                                    ){
//                                        Column(
//                                            modifier = Modifier
//                                                .width(12.dp)
//                                                .height(42.dp)
//                                        ){
//                                        }
//                                        Text("加入今日拾藏備選庫",
//                                            color = Color(0xFF2A241F),
//                                            fontSize = 16.sp,
//                                            fontWeight = FontWeight.Bold,
//                                            modifier = Modifier
//                                                .padding(start = 11.dp,)
//                                        )
//                                    }
//                                }
//                                Column(
//                                    modifier = Modifier
//                                        .width(35.dp)
//                                        .height(42.dp)
//                                ){
//                                }
//                                Column(
//                                    modifier = Modifier
//                                        .clip(shape = RoundedCornerShape(9999.dp))
//                                        .background(
//                                            color = Color(0xFF5A6A55),
//                                            shape = RoundedCornerShape(9999.dp)
//                                        )
//                                        .padding(top = 4.dp,bottom = 4.dp,start = 28.dp,end = 4.dp,)
//                                ){
//                                    Column(
//                                        modifier = Modifier
//                                            .clip(shape = RoundedCornerShape(9999.dp))
//                                            .width(24.dp)
//                                            .height(24.dp)
//                                            .background(
//                                                color = Color(0xFFFFFFFF),
//                                                shape = RoundedCornerShape(9999.dp)
//                                            )
//                                            .shadow(
//                                                elevation = 3.dp,
//                                                spotColor = Color(0x1A000000),
//                                            )
//                                    ){
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                Column(
//                    modifier = Modifier
//                        .offset(x = 46.dp, y = 560.dp)
//                        .align(Alignment.TopEnd)
//                        .padding(start = 46.dp, bottom = 560.dp)
//                        .border(
//                            width = 1.dp,
//                            color = Color(0xFFE8E1D6),
//                            shape = RoundedCornerShape(6.dp)
//                        )
//                        .clip(shape = RoundedCornerShape(6.dp))
//                        .width(66.dp)
//                        .height(38.dp)
//                        .background(
//                            color = Color(0xFFF1EEE6),
//                            shape = RoundedCornerShape(6.dp)
//                        )
//                ){
//                }
//            }
//        }
//    }
//}
