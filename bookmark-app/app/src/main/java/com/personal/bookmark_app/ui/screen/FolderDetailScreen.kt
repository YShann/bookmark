package com.personal.bookmark_app.ui.screen

import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.*
import androidx.compose.ui.layout.*
import com.personal.bookmark_app.ui.components.TextFieldView
import com.skydoves.landscapist.*
import com.skydoves.landscapist.coil3.*
import androidx.compose.ui.tooling.preview.Preview
import com.personal.bookmark_app.ui.theme.BookmarkappTheme

@Preview(
    name = "收藏分類夾預覽",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun FolderDetailScreenPreview() {
    BookmarkappTheme { FolderDetailScreen() }
}

@Composable
fun FolderDetailScreen() {
    val textField1 = remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                color = Color(0xFFFFFFFF),
            )
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF7F3EA),
                    )
                    .padding(top = 20.dp,start = 24.dp,end = 24.dp,)
            ){
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    CoilImage(
                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/8bd56qu5_expires_30_days.png"},
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                        modifier = Modifier
                            .padding(start = 13.dp,)
                            .width(13.dp)
                            .height(22.dp)
                    )
                    Text("未分類",
                        color = Color(0xFF2F2A24),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 2.dp,)
                            .clip(shape = RoundedCornerShape(9999.dp))
                            .background(
                                color = Color(0xFFEDE8DE),
                                shape = RoundedCornerShape(9999.dp)
                            )
                            .padding(vertical = 10.dp,horizontal = 16.dp,)
                    ){
                        CoilImage(
                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/0mwl5btt_expires_30_days.png"},
                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(9999.dp))
                                .width(15.dp)
                                .height(18.dp)
                        )
                        Column(
                            modifier = Modifier
                                .width(8.dp)
                                .height(22.dp)
                        ){
                        }
                        Text("批量管理",
                            color = Color(0xFF5A5A52),
                            fontSize = 15.sp,
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth()
                ){
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF3EFE6),
                            shape = RoundedCornerShape(16.dp)
                        )
                ){
                    CoilImage(
                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/rwvvirt0_expires_30_days.png"},
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                        modifier = Modifier
                            .padding(start = 20.dp,)
                            .clip(shape = RoundedCornerShape(16.dp))
                            .width(20.dp)
                            .height(20.dp)
                    )
                    Column(
                        modifier = Modifier
                            .width(12.dp)
                            .height(25.dp)
                    ){
                    }
                    TextFieldView(
                        placeholder = "搜尋這個收藏夾的內容...",
                        value = textField1.value,
                        onValueChange = { newText -> textField1.value = newText },
                        textStyle = TextStyle(
                            color = Color(0xFF8E8A80),
                            fontSize = 16.sp,
                        ),
                        modifier = Modifier
                            .padding(end = 4.dp,)
                            .weight(1f)
                            .padding(vertical = 16.dp,)
                    )
                }
                Column(
                    modifier = Modifier
                        .height(17.dp)
                        .fillMaxWidth()
                ){
                }
                Column(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF3EFE6),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/l9qf5z6d_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .width(13.dp)
                                    .height(18.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(31.dp)
                            ){
                            }
                            Text("標籤篩選",
                                color = Color(0xFF2F2A24),
                                fontSize = 16.sp,
                            )
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(31.dp)
                            ){
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ){
                                Column(
                                    modifier = Modifier
                                        .padding(bottom = 7.dp,)
                                ){
                                    Column(
                                        modifier = Modifier
                                            .padding(start = 15.dp,)
                                            .width(12.dp)
                                            .height(31.dp)
                                    ){
                                    }
                                    CoilImage(
                                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/cjtve16x_expires_30_days.png"},
                                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                        modifier = Modifier
                                            .width(16.dp)
                                            .height(16.dp)
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(9999.dp))
                                    .background(
                                        color = Color(0xFF4E624B),
                                        shape = RoundedCornerShape(9999.dp)
                                    )
                                    .clickable { println("Pressed!") }
                                    .padding(vertical = 5.dp,horizontal = 16.dp,)
                            ){
                                Text("已選 2 個標籤",
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .padding(end = 1.dp,)
                                .width(8.dp)
                                .height(31.dp)
                        ){
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Text("清除",
                                color = Color(0xFF8E8A80),
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(end = 2.dp,)
                            )
                            Column(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(24.dp)
                            ){
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ){
                                Text("展開",
                                    color = Color(0xFF4E624B),
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .padding(end = 2.dp,)
                                )
                                Column(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(24.dp)
                                ){
                                }
                                CoilImage(
                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/y4pj66j4_expires_30_days.png"},
                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                    modifier = Modifier
                                        .width(12.dp)
                                        .height(12.dp)
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth()
                    ){
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(4.dp))
                                    .background(
                                        color = Color(0xFF4E624B),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(vertical = 11.dp,horizontal = 20.dp,)
                            ){
                                CoilImage(
                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/3lc61rjb_expires_30_days.png"},
                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(4.dp))
                                        .width(12.dp)
                                        .height(13.dp)
                                )
                                Column(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(24.dp)
                                ){
                                }
                                Text("生活",
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(46.dp)
                            ){
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(4.dp))
                                    .background(
                                        color = Color(0xFF4E624B),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(vertical = 11.dp,horizontal = 20.dp,)
                            ){
                                CoilImage(
                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/pf812isq_expires_30_days.png"},
                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(4.dp))
                                        .width(12.dp)
                                        .height(13.dp)
                                )
                                Column(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(24.dp)
                                ){
                                }
                                Text("閱讀",
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(46.dp)
                            ){
                            }
                            Column(
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(4.dp))
                                    .background(
                                        color = Color(0xFFE8E4DB),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .clickable { println("Pressed!") }
                                    .padding(vertical = 11.dp,horizontal = 20.dp,)
                            ){
                                Text("美食",
                                    color = Color(0xFF5A5A52),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .height(12.dp)
                                .fillMaxWidth()
                        ){
                        }
                        Column(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(4.dp))
                                .background(
                                    color = Color(0xFFE8E4DB),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clickable { println("Pressed!") }
                                .padding(vertical = 11.dp,horizontal = 20.dp,)
                        ){
                            Text("旅遊",
                                color = Color(0xFF5A5A52),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth()
                ){
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp,)
                ){
                    Text("共 18 篇收藏",
                        color = Color(0xFF8A8478),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 1.dp,)
                    ){
                        CoilImage(
                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/xytjxhbt_expires_30_days.png"},
                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                            modifier = Modifier
                                .width(16.dp)
                                .height(14.dp)
                        )
                        Column(
                            modifier = Modifier
                                .width(9.dp)
                                .height(24.dp)
                        ){
                        }
                        Text("時間排序",
                            color = Color(0xFF6F6A60),
                            fontSize = 16.sp,
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth()
                ){
                }
                FolderBookmarkCards()
            }
        }
    }
}



private data class FolderBookmarkItem(val source:String,val date:String,val title:String,val description:String,val imageUrl:String,val tags:List<String>,val note:String?=null)

@Composable private fun FolderBookmarkCards(){
 val photos=listOf("https://images.unsplash.com/photo-1445116572660-236099ec97a0?w=240","https://images.unsplash.com/photo-1497250681960-ef046c08a56e?w=240","https://images.unsplash.com/photo-1516483638261-f4dbaf036963?w=240","https://images.unsplash.com/photo-1476224203421-9ac39bcb3327?w=240","https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=240")
 val items=listOf(
 FolderBookmarkItem("Threads","2024/06/12","台北巷弄裡的質感咖啡廳","在這座城市裡，總有一些不經意走進去...",photos[0],listOf("美食","生活"),"老闆推薦手沖那加雪菲，二樓窗邊位置採光最好，平日不限時，適合帶筆電或閱讀一整個下午。"),
 FolderBookmarkItem("Dcard","2024/06/10","讓生活變美好的 5 個習慣","從日常小事開始，慢慢變成更喜歡的自己...",photos[1],listOf("生活","閱讀")),
 FolderBookmarkItem("Instagram","2024/06/08","京都小旅行｜必去的 5 個景點","這次的京都行，記錄了幾個很喜歡的地...",photos[2],listOf("旅遊","生活")),
 FolderBookmarkItem("YouTube","2024/06/05","簡單好吃的家常料理｜番茄燉飯","食材簡單、步驟不難，在家也能輕鬆做...",photos[3],listOf("美食","料理")),
 FolderBookmarkItem("網頁","2024/06/02","如何建立自己的數位資料庫","整理收藏、管理資訊，讓靈感不再從指...",photos[4],listOf("設計","工具")))
 Column(Modifier.fillMaxWidth().padding(bottom=28.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){items.forEach{FolderBookmarkCard(it)}}
}

@Composable private fun FolderBookmarkCard(item:FolderBookmarkItem){
 Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).padding(14.dp),verticalAlignment=Alignment.CenterVertically){
 CoilImage(imageModel={item.imageUrl},imageOptions=ImageOptions(contentScale=ContentScale.Crop),modifier=Modifier.size(76.dp).clip(RoundedCornerShape(7.dp)))
 Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)){
 Row(verticalAlignment=Alignment.CenterVertically){Text("${item.source} · ${item.date}",color=Color(0xFF858178),fontSize=12.sp,maxLines=1,modifier=Modifier.weight(1f));Text("⋮",color=Color(0xFF858178),fontSize=20.sp)}
 Spacer(Modifier.height(3.dp));Text(item.title,color=Color(0xFF292720),fontSize=16.sp,fontWeight=FontWeight.SemiBold,maxLines=2,lineHeight=21.sp)
 Spacer(Modifier.height(2.dp));Text(item.description,color=Color(0xFF6C675E),fontSize=12.sp,maxLines=1)
 item.note?.let{note->Spacer(Modifier.height(8.dp));Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(5.dp)).background(Color(0xFFF4F2EC)).padding(9.dp)){Text("☷  筆記                                      ⌃",color=Color(0xFF50624E),fontSize=12.sp,fontWeight=FontWeight.SemiBold);Spacer(Modifier.height(5.dp));HorizontalDivider(color=Color(0xFFE2DED5));Spacer(Modifier.height(5.dp));Text(note,color=Color(0xFF49453E),fontSize=12.sp,lineHeight=18.sp,maxLines=4)}}
 Spacer(Modifier.height(7.dp));Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){item.tags.forEach{tag->Text(tag,color=Color(0xFF57564F),fontSize=11.sp,fontWeight=FontWeight.SemiBold,modifier=Modifier.clip(RoundedCornerShape(50)).background(Color(0xFFE9E7E1)).padding(horizontal=9.dp,vertical=3.dp))}}
 }}
}
