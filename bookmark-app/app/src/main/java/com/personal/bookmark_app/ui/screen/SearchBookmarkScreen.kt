package com.personal.bookmark_app.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.ui.components.BookmarkCard
import com.personal.bookmark_app.ui.mock_data.bookmarkMockData
import com.personal.bookmark_app.ui.model.BookmarkSource
import com.personal.bookmark_app.ui.model.BookmarkUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBookmarkScreen(
    onBackClick: () -> Unit = {},
) {
    // 搜尋關鍵字
    var searchQuery by remember {
        mutableStateOf("")
    }

    // 目前選取的平台，null 代表全部平台
    var selectedSource by remember {
        mutableStateOf<BookmarkSource?>(null)
    }

    // 先根據關鍵字篩選收藏
    val searchFilteredBookmarks = remember(searchQuery) {
        bookmarkMockData.filter { bookmark ->
            bookmark.matchesSearchQuery(searchQuery)
        }
    }

    // 計算各平台符合目前關鍵字的收藏數量
    val sourceBookmarkCounts = remember(searchFilteredBookmarks) {
        BookmarkSource.entries.associateWith { source ->
            searchFilteredBookmarks.count { bookmark ->
                bookmark.source == source
            }
        }
    }

    // 再根據選取的平台篩選搜尋結果
    val filteredBookmarks = remember(
        searchFilteredBookmarks,
        selectedSource
    ) {
        searchFilteredBookmarks.filter { bookmark ->
            selectedSource == null || bookmark.source == selectedSource
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SearchBookmarkTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {

            // 搜尋欄
            SearchInputField(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                }
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // 平台篩選
            SourceFilterSection(
                sourceBookmarkCounts = sourceBookmarkCounts,
                selectedSource = selectedSource,
                onSourceSelected = {
                    selectedSource = it
                }
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // 搜尋結果列表
            if (filteredBookmarks.isEmpty()) {
                EmptySearchResult(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                SearchResultList(
                    bookmarks = filteredBookmarks,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// --------------------------------------------------
// Toolbar
// --------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBookmarkTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        title = {
            SearchInputField(
                query = searchQuery,
                onQueryChange = onSearchQueryChange
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

// --------------------------------------------------
// 搜尋欄
// --------------------------------------------------

@Composable
private fun SearchInputField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth(),
//            .height(52.dp),
        placeholder = {
            Text(
                text = "搜尋標題、網址或個人筆記備註...",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "搜尋"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "清除搜尋"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        )
    )
}

// --------------------------------------------------
// 平台篩選
// --------------------------------------------------

@Composable
private fun SourceFilterSection(
    sourceBookmarkCounts: Map<BookmarkSource, Int>,
    selectedSource: BookmarkSource?,
    onSourceSelected: (BookmarkSource?) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 全部平台
        FilterChip(
            selected = selectedSource == null,
            onClick = {
                onSourceSelected(null)
            },
            label = {
                Text(
                    text = "全部平台（${sourceBookmarkCounts.values.sum()}）"
                )
            }
        )

        // 各個收藏平台
        BookmarkSource.entries.forEach { source ->
            FilterChip(
                selected = selectedSource == source,
                onClick = {
                    onSourceSelected(source)
                },
                label = {
                    Text(
                        text = "${source.displayName}（${sourceBookmarkCounts[source] ?: 0}）"
                    )
                }
            )
        }
    }
}

// --------------------------------------------------
// 搜尋結果列表
// --------------------------------------------------

@Composable
private fun SearchResultList(
    bookmarks: List<BookmarkUiModel>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            bottom = 36.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = bookmarks,
            key = { bookmark ->
                bookmark.id
            }
        ) { bookmark ->
            BookmarkCard(
                bookmark = bookmark
            )
        }
    }
}

// --------------------------------------------------
// 搜尋結果為空
// --------------------------------------------------

@Composable
private fun EmptySearchResult(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "找不到相關收藏",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "試試其他關鍵字或切換平台分類",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --------------------------------------------------
// 收藏關鍵字比對
// --------------------------------------------------

private fun BookmarkUiModel.matchesSearchQuery(
    query: String
): Boolean {
    val keyword = query.trim()

    if (keyword.isEmpty()) {
        return true
    }

    return title.contains(keyword, ignoreCase = true) ||
            description.contains(keyword, ignoreCase = true) ||
            originalUrl.contains(keyword, ignoreCase = true) ||
            (note?.contains(keyword, ignoreCase = true) == true) ||
            source.displayName.contains(keyword, ignoreCase = true) ||
            tags.any { tag ->
                tag.contains(keyword, ignoreCase = true)
            }
}


//
//package com.personal.bookmark_app.ui.screen
//
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
//import androidx.compose.ui.layout.*
//import com.skydoves.landscapist.*
//import com.skydoves.landscapist.coil3.*
//
//@Composable
//fun SearchBookmarkScreen() {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .fillMaxHeight()
//            .background(
//                color = Color(0xFFFFFFFF),
//            )
//    ){
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f)
//                .verticalScroll(rememberScrollState())
//        ){
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(
//                        color = Color(0xFFF7F3EA),
//                    )
//                    .padding(top = 20.dp,start = 24.dp,end = 24.dp,)
//            ){
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 5.dp,)
//                ){
//                    CoilImage(
//                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/3njkep1p_expires_30_days.png"},
//                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                        modifier = Modifier
//                            .padding(end = 1.dp,)
//                            .width(13.dp)
//                            .height(22.dp)
//                    )
//                    Column(
//                        modifier = Modifier
//                            .width(21.dp)
//                            .height(55.dp)
//                    ){
//                    }
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .clip(shape = RoundedCornerShape(14.dp))
//                            .weight(1f)
//                            .background(
//                                color = Color(0xFFFFFFFF),
//                                shape = RoundedCornerShape(14.dp)
//                            )
//                            .padding(vertical = 14.dp,horizontal = 19.dp,)
//                    ){
//                        CoilImage(
//                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/p1k0y9fe_expires_30_days.png"},
//                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                            modifier = Modifier
//                                .clip(shape = RoundedCornerShape(14.dp))
//                                .width(18.dp)
//                                .height(18.dp)
//                        )
//                        Column(
//                            modifier = Modifier
//                                .weight(1f)
//                        ){
//                            Column(
//                                modifier = Modifier
//                                    .width(16.dp)
//                                    .height(27.dp)
//                            ){
//                            }
//                            Text("搜尋標題、網址或個人筆記備註...",
//                                color = Color(0xFF3A3A3A),
//                                fontSize = 18.sp,
//                                modifier = Modifier
//                                    .padding(start = 15.dp,)
//                            )
//                        }
//                    }
//                }
//                Column(
//                    modifier = Modifier
//                        .height(24.dp)
//                        .fillMaxWidth()
//                ){
//                }
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ){
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .clip(shape = RoundedCornerShape(6.dp))
//                            .background(
//                                color = Color(0xFF4F5F4B),
//                                shape = RoundedCornerShape(6.dp)
//                            )
//                            .shadow(
//                                elevation = 2.dp,
//                                spotColor = Color(0x0D000000),
//                            )
//                            .padding(vertical = 12.dp,horizontal = 16.dp,)
//                    ){
//                        Text("全部平台",
//                            color = Color(0xFFFFFFFF),
//                            fontSize = 15.sp,
//                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier
//                                .width(15.dp)
//                        )
//                        Column(
//                            modifier = Modifier
//                                .width(20.dp)
//                                .height(88.dp)
//                        ){
//                        }
//                        Text("48",
//                            color = Color(0xFFFFFFFF),
//                            fontSize = 13.sp,
//                            fontWeight = FontWeight.Bold,
//                        )
//                    }
//                    Column(
//                        modifier = Modifier
//                            .width(12.dp)
//                            .height(112.dp)
//                    ){
//                    }
//                    Row(
//                        horizontalArrangement = Arrangement.Center,
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .padding(end = 1.dp,)
//                            .clip(shape = RoundedCornerShape(6.dp))
//                            .weight(1f)
//                            .background(
//                                color = Color(0xFFECE9E2),
//                                shape = RoundedCornerShape(6.dp)
//                            )
//                            .padding(vertical = 43.dp,)
//                    ){
//                        CoilImage(
//                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/y225kofb_expires_30_days.png"},
//                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                            modifier = Modifier
//                                .clip(shape = RoundedCornerShape(6.dp))
//                                .width(13.dp)
//                                .height(14.dp)
//                        )
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            modifier = Modifier
//                                .padding(end = 2.dp,)
//                        ){
//                            Column(
//                                modifier = Modifier
//                                    .padding(end = 7.dp,)
//                            ){
//                                Column(
//                                    modifier = Modifier
//                                        .width(8.dp)
//                                        .height(22.dp)
//                                ){
//                                }
//                                Text("Threads",
//                                    color = Color(0xFF5F5F5F),
//                                    fontSize = 14.sp,
//                                    modifier = Modifier
//                                        .padding(start = 7.dp,)
//                                )
//                            }
//                        }
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                        ){
//                            Column(
//                                modifier = Modifier
//                                    .padding(end = 13.dp,)
//                            ){
//                                Column(
//                                    modifier = Modifier
//                                        .width(8.dp)
//                                        .height(22.dp)
//                                ){
//                                }
//                                Text("14",
//                                    color = Color(0xFF5F5F5F),
//                                    fontSize = 14.sp,
//                                )
//                            }
//                        }
//                    }
//                    Column(
//                        modifier = Modifier
//                            .width(12.dp)
//                            .height(112.dp)
//                    ){
//                    }
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .clip(shape = RoundedCornerShape(6.dp))
//                            .weight(1f)
//                            .background(
//                                color = Color(0xFFECE9E2),
//                                shape = RoundedCornerShape(6.dp)
//                            )
//                            .padding(vertical = 43.dp,horizontal = 12.dp,)
//                    ){
//                        CoilImage(
//                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/iw13xu5j_expires_30_days.png"},
//                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                            modifier = Modifier
//                                .clip(shape = RoundedCornerShape(6.dp))
//                                .width(13.dp)
//                                .height(14.dp)
//                        )
//                        Column(
//                            modifier = Modifier
//                                .padding(end = 3.dp,)
//                                .weight(1f)
//                        ){
//                            Column(
//                                modifier = Modifier
//                                    .width(8.dp)
//                                    .height(22.dp)
//                            ){
//                            }
//                            Text("Instagram",
//                                color = Color(0xFF5F5F5F),
//                                fontSize = 14.sp,
//                                modifier = Modifier
//                                    .padding(start = 7.dp,)
//                            )
//                        }
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                        ){
//                            Column(
//                                modifier = Modifier
//                                    .padding(end = 13.dp,)
//                            ){
//                                Column(
//                                    modifier = Modifier
//                                        .width(8.dp)
//                                        .height(22.dp)
//                                ){
//                                }
//                                Text("12",
//                                    color = Color(0xFF5F5F5F),
//                                    fontSize = 14.sp,
//                                )
//                            }
//                        }
//                    }
//                }
//                Column(
//                    modifier = Modifier
//                        .height(56.dp)
//                        .fillMaxWidth()
//                ){
//                }
//                SearchResultCards()
//            }
//        }
//    }
//}
//
//private data class SearchResult(val source:String,val tag:String,val date:String,val title:String,val description:String,val image:String,val note:String?=null,val link:String?=null,val badge:String?=null)
//
//@Composable private fun SearchResultCards(){
// val results=listOf(
// SearchResult("Instagram","#設計靈感","2024/04/08","昭和復古喫茶店的招牌字體特輯","手繪圓體、金屬壓印與暖黃霓虹管。在神保町與谷中的角落，收集舊時代喫茶館...","https://images.unsplash.com/photo-1445116572660-236099ec97a0?w=240","招牌字型的圓弧處理很值得在下次品牌專案借鏡參考。",badge="1/6"),
// SearchResult("Web 專題","#深度閱讀","2025/03/11","專注的技術：擺脫演算法餵養的「慢讀」練習","在即時訊息過載的時代，用紙本書與手寫筆記重構深度思考的心理空間，重拾閱讀...","https://images.unsplash.com/photo-1455390582262-044c424a9841?w=240",link="theverge.com/features/...",badge="專欄"),
// SearchResult("Threads","#建築空間","2024/10/02","京都「町家」改造手記：光影流動與舊木料的共存","百年土壁在自然天光之下的沉靜表情。透過將綠側外移，重新梳理庭院枯山水與屋...","https://images.unsplash.com/photo-1516483638261-f4dbaf036963?w=240","縱側與日式庭院採光的斜角度值得紀錄在改造清單。"))
// Column(Modifier.fillMaxWidth().padding(bottom=36.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){results.forEach{SearchResultCard(it)}}
//}
//
//@Composable private fun SearchResultCard(item:SearchResult){
// Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(11.dp)).background(Color.White).border(1.dp,Color(0xFFEAE6DE),RoundedCornerShape(11.dp)).padding(15.dp)){
// Row(verticalAlignment=Alignment.CenterVertically){Text(item.source,color=Color(0xFF66665F),fontSize=12.sp,fontWeight=FontWeight.Medium,modifier=Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0xFFF3F0E9)).padding(horizontal=8.dp,vertical=5.dp));Spacer(Modifier.width(9.dp));Text(item.tag,color=Color(0xFFAA5634),fontSize=12.sp,fontWeight=FontWeight.SemiBold);Text("  ·  ",color=Color(0xFF8A8A82),fontSize=12.sp);Text(item.date,color=Color(0xFF85857D),fontSize=12.sp)}
// Spacer(Modifier.height(9.dp));Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(item.title,color=Color(0xFF282721),fontSize=16.sp,fontWeight=FontWeight.Bold,lineHeight=21.sp,maxLines=2);Spacer(Modifier.height(3.dp));Text(item.description,color=Color(0xFF56564F),fontSize=13.sp,lineHeight=20.sp,maxLines=2)};Spacer(Modifier.width(10.dp));Box(Modifier.size(84.dp).clip(RoundedCornerShape(5.dp))){CoilImage(imageModel={item.image},imageOptions=ImageOptions(contentScale=ContentScale.Crop),modifier=Modifier.fillMaxSize());item.badge?.let{Text(it,color=Color.White,fontSize=11.sp,fontWeight=FontWeight.Bold,modifier=Modifier.align(Alignment.BottomEnd).clip(RoundedCornerShape(topStart=4.dp)).background(Color(0x99000000)).padding(horizontal=6.dp,vertical=3.dp))}}
// }
// item.note?.let{Spacer(Modifier.height(10.dp));Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(5.dp)).background(Color(0xFFF4F2EC)).padding(horizontal=10.dp,vertical=9.dp),verticalAlignment=Alignment.Top){Text("☷",color=Color(0xFF696D62),fontSize=14.sp);Spacer(Modifier.width(9.dp));Text("筆記：$it",color=Color(0xFF5F5F58),fontSize=12.sp,lineHeight=18.sp,modifier=Modifier.weight(1f))}}
// item.link?.let{Spacer(Modifier.height(12.dp));Text("↪  $it",color=Color(0xFF85857D),fontSize=12.sp)}
// }
//}
