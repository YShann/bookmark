package com.personal.bookmark_app.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.layout.*
import com.skydoves.landscapist.*
import com.skydoves.landscapist.coil3.*
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "今日拾藏預覽",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun DailyPicksScreenPreview() {
    DailyPicksScreen()
}

@Composable
fun DailyPicksScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0xFFFBF9F3),
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
                    .background(color = Color(0xFFFBF9F3))
                    .padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 24.dp)
            ){
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 9.dp,)
                    ){
                        CoilImage(
                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/o2xun4ko_expires_30_days.png"},
                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                            modifier = Modifier
                                .width(13.dp)
                                .height(22.dp)
                        )
                        Column(
                            modifier = Modifier
                                .width(20.dp)
                        ){
                        }
                        Text("今日拾藏",
                            color = Color(0xFF1E1A17),
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(6.dp))
                            .background(
                                color = Color(0xFFEFECE4),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(vertical = 7.dp,horizontal = 16.dp,)
                    ){
                        CoilImage(
                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/698c1rl1_expires_30_days.png"},
                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(6.dp))
                                .width(14.dp)
                                .height(14.dp)
                        )
                        Column(
                            modifier = Modifier
                                .width(8.dp)
                                .height(24.dp)
                        ){
                        }
                        Text("換一組",
                            color = Color(0xFF6E6A63),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .height(8.dp)
                        .fillMaxWidth()
                ){
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp,)
                ){
                    CoilImage(
                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/mdxo0ojm_expires_30_days.png"},
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                        modifier = Modifier
                            .width(14.dp)
                            .height(15.dp)
                    )
                    Column(
                        modifier = Modifier
                            .width(8.dp)
                            .height(24.dp)
                    ){
                    }
                    Text("2025 年 5 月 18 日・星期日",
                        color = Color(0xFF7A746B),
                        fontSize = 14.sp,
                    )
                }
                Column(
                    modifier = Modifier
                        .height(10.dp)
                        .fillMaxWidth()
                ){
                }
                Column(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(10.dp))
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF4F0E8),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(vertical = 14.dp,horizontal = 16.dp)
                ){
                    Text("從遺忘的角落拾起 3 則收藏，回想當初的靈感。",
                        color = Color(0xFF5E5A54),
                        fontSize = 15.sp,
                    )
                }
                Column(
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth()
                ){
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Column(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color(0xFFE6E0D6),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(shape = RoundedCornerShape(12.dp))
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFFFDF9),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .shadow(
                                elevation = 2.dp,
                                spotColor = Color(0x08000000),
                            )
                            .padding(vertical = 16.dp,horizontal = 16.dp)
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(2.dp))
                                    .background(
                                        color = Color(0xFFE7E3DA),
                                        shape = RoundedCornerShape(2.dp)
                                    )
                                    .padding(vertical = 6.dp,horizontal = 12.dp,)
                            ){
                                CoilImage(
                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/hs8k3zdz_expires_30_days.png"},
                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(2.dp))
                                        .width(13.dp)
                                        .height(6.dp)
                                )
                                Column(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(15.dp)
                                ){
                                }
                                Text("Instagram",
                                    color = Color(0xFF6E6A63),
                                    fontSize = 14.sp,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(27.dp)
                            ){
                            }
                            Text("#設計靈感",
                                color = Color(0xFF9A5A3A),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(end = 1.dp,)
                            )
                            Column(
                                modifier = Modifier
                                    .padding(end = 5.dp,)
                                    .padding(top = 2.dp,bottom = 2.dp,start = 12.dp,)
                            ){
                                Text("•",
                                    color = Color(0xFF8A847B),
                                    fontSize = 12.sp,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(27.dp)
                            ){
                            }
                            Text("2024/04/08",
                                color = Color(0xFF8A847B),
                                fontSize = 14.sp,
                            )
                        }
                        Column(
                            modifier = Modifier
                                .height(16.dp)
                                .fillMaxWidth()
                        ){
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 2.dp,)
                            ){
                                Text("昭和復古喫茶店的招牌字體特輯",
                                    color = Color(0xFF1F1A17),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Column(
                                    modifier = Modifier
                                        .height(6.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Text("手繪圓體、金屬壓印與暖黃霓虹管。在神保町與谷中的角落，收集舊時代喫茶館...",
                                    color = Color(0xFF5F5A54),
                                    fontSize = 14.sp,
                                )
                            }
                            Thumbnail(
                                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuA4ARgPC51l_ckRmiA8kyJZqWPUkfd_W-veWKgBxW34JItRWgTMezn9V4u2Y2GyTrtV-bS01M7_NLnz2ysXn6jIOZhl00y49eK77IOnYwiulNJ91auzb5COr5OSQ4Rh0-E5PKjndOJq7sQofVC5zwt3M_dws6RmHYW2js9ynGvTPftj1NHUuEYF9MQ2kqLdun9NLc-vkGa3D3uVycQn-p4Wztnwpwm_STqenkCOLw8pJPpoXQVT1Ck85Q",
                                badge = "1/6",
                            )
                        }
                        Column(
                            modifier = Modifier
                                .height(12.dp)
                                .fillMaxWidth()
                        ){
                        }
                        Row(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(2.dp))
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFF7F3EC),
                                    shape = RoundedCornerShape(2.dp)
                                )
                                .padding(vertical = 16.dp,horizontal = 12.dp,)
                        ){
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/wt591oz4_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(2.dp))
                                    .width(14.dp)
                                    .height(14.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(46.dp)
                            ){
                            }
                            Text("筆記：招牌字型的圓弧處理很值得在下次品牌專案借鏡參考。",
                                color = Color(0xFF6A655D),
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .weight(1f)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .height(24.dp)
                            .fillMaxWidth()
                    ){
                    }
                    Column(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color(0xFFE6E0D6),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(shape = RoundedCornerShape(12.dp))
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFFFDF9),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .shadow(
                                elevation = 2.dp,
                                spotColor = Color(0x08000000),
                            )
                            .padding(vertical = 16.dp,horizontal = 16.dp)
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(2.dp))
                                    .background(
                                        color = Color(0xFFE7E3DA),
                                        shape = RoundedCornerShape(2.dp)
                                    )
                                    .padding(vertical = 6.dp,horizontal = 12.dp,)
                            ){
                                CoilImage(
                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/11xhezna_expires_30_days.png"},
                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(2.dp))
                                        .width(13.dp)
                                        .height(6.dp)
                                )
                                Column(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(15.dp)
                                ){
                                }
                                Text("Web 專題",
                                    color = Color(0xFF6E6A63),
                                    fontSize = 14.sp,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(27.dp)
                            ){
                            }
                            Text("#深度閱讀",
                                color = Color(0xFF9A5A3A),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(end = 1.dp,)
                            )
                            Column(
                                modifier = Modifier
                                    .padding(end = 5.dp,)
                                    .padding(top = 2.dp,bottom = 2.dp,start = 12.dp,)
                            ){
                                Text("•",
                                    color = Color(0xFF8A847B),
                                    fontSize = 12.sp,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(27.dp)
                            ){
                            }
                            Text("2025/03/11",
                                color = Color(0xFF8A847B),
                                fontSize = 14.sp,
                            )
                        }
                        Column(
                            modifier = Modifier
                                .height(16.dp)
                                .fillMaxWidth()
                        ){
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 2.dp,)
                            ){
                                Text("專注的技術：擺脫演算法餵養的「慢讀」練習",
                                    color = Color(0xFF1F1A17),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Column(
                                    modifier = Modifier
                                        .height(6.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Text("在即時訊息過載的時代，用紙本書與手寫筆記重構深度思考的心理空間，重拾閱...",
                                    color = Color(0xFF5F5A54),
                                    fontSize = 14.sp,
                                )
                            }
                            Thumbnail(
                                imageUrl = "https://images.unsplash.com/photo-1499750310107-5fef28a66643?auto=format&fit=crop&w=800&q=80",
                                badge = "專欄",
                            )
                        }
                        Column(
                            modifier = Modifier
                                .height(16.dp)
                                .fillMaxWidth()
                        ){
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp,start = 2.dp,end = 2.dp,)
                        ){
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/m0kaeoo3_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .width(14.dp)
                                    .height(11.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(10.dp)
                                    .height(22.dp)
                            ){
                            }
                            Text("theverge.com/features/...",
                                color = Color(0xFF7A746B),
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
                    Column(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color(0xFFE6E0D6),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(shape = RoundedCornerShape(12.dp))
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFFFDF9),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .shadow(
                                elevation = 2.dp,
                                spotColor = Color(0x08000000),
                            )
                            .padding(vertical = 16.dp,horizontal = 16.dp)
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(2.dp))
                                    .background(
                                        color = Color(0xFFE7E3DA),
                                        shape = RoundedCornerShape(2.dp)
                                    )
                                    .padding(vertical = 6.dp,horizontal = 13.dp,)
                            ){
                                CoilImage(
                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/v8ns6io9_expires_30_days.png"},
                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(2.dp))
                                        .width(13.dp)
                                        .height(11.dp)
                                )
                                Column(
                                    modifier = Modifier
                                        .width(9.dp)
                                        .height(15.dp)
                                ){
                                }
                                Text("YouTube",
                                    color = Color(0xFF6E6A63),
                                    fontSize = 14.sp,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(27.dp)
                            ){
                            }
                            Text("#生活提案",
                                color = Color(0xFF9A5A3A),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(end = 1.dp,)
                            )
                            Column(
                                modifier = Modifier
                                    .padding(end = 5.dp,)
                                    .padding(top = 2.dp,bottom = 2.dp,start = 12.dp,)
                            ){
                                Text("•",
                                    color = Color(0xFF8A847B),
                                    fontSize = 12.sp,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(27.dp)
                            ){
                            }
                            Text("2025/04/30",
                                color = Color(0xFF8A847B),
                                fontSize = 14.sp,
                            )
                        }
                        Column(
                            modifier = Modifier
                                .height(16.dp)
                                .fillMaxWidth()
                        ){
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 2.dp,)
                            ){
                                Text("京都巷弄裡的老書店巡禮：只賣一本書的店",
                                    color = Color(0xFF1F1A17),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        
                                )
                                Column(
                                    modifier = Modifier
                                        .height(6.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Text("藏身在石板小徑後的窄窄木屋，每週僅精選一冊主題讀物，在微光與茶香中感受...",
                                    color = Color(0xFF5F5A54),
                                    fontSize = 14.sp,
                                )
                            }
                            Thumbnail(
                                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDOlyQg8DRF63oSYFbqOlXiAdqMqQes07uZnzbKWyMGO31KVw6Be0r0nweJINfiRj7-QtufYJ-1bkP413-Bz2bvfGomr_bybaGyFgrCIU2k7ckwHTdqMVeCFhxPAwi4cDStTfMlNO03xsCCDh4KjMc8uzLDWGjK7f2me2rXDWsjt1G7Llyt4KiW_ZKFzzbrP7_tJV4OSVEMJI7UGVWeYXgcBLCMkmiXNqQewe9qPXVBkoy57JCl_yIPDg",
                                badge = "14:20",
                            )
                        }
                        Column(
                            modifier = Modifier
                                .height(12.dp)
                                .fillMaxWidth()
                        ){
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp,start = 2.dp,end = 2.dp,)
                        ){
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/k5bv0pq6_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .width(14.dp)
                                    .height(11.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(10.dp)
                                    .height(22.dp)
                            ){
                            }
                            Text("youtube.com/watch?v=...",
                                color = Color(0xFF7A746B),
                                fontSize = 15.sp,
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(12.dp))
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFE8E4DC),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 12.dp,)
                    ){
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Column(
                                modifier = Modifier
                                    .padding(bottom = 6.dp,)
                            ){
                                Column(
                                    modifier = Modifier
                                        .padding(start = 17.dp,)
                                        .width(12.dp)
                                        .height(30.dp)
                                ){
                                }
                                CoilImage(
                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/co6a948i_expires_30_days.png"},
                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(12.dp))
                                        .width(18.dp)
                                        .height(18.dp)
                                )
                            }
                        }
                        Text("換一組推薦",
                            color = Color(0xFF2B2B2B),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth()
                    ){
                    }
                    Text("每日清晨 06:00 固定拾新，亦可隨時手動刷新。",
                        color = Color(0xFF7A746B),
                        fontSize = 15.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun Thumbnail(imageUrl: String, badge: String) {
    Box(
        modifier = Modifier
            .padding(start = 12.dp)
            .size(width = 80.dp, height = 80.dp)
            .clip(RoundedCornerShape(8.dp)),
    ) {
        CoilImage(
            imageModel = { imageUrl },
            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
            modifier = Modifier.fillMaxSize(),
        )
        Text(
            text = badge,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(topStart = 4.dp))
                .background(Color(0xB3000000))
                .padding(horizontal = 6.dp, vertical = 3.dp),
        )
    }
}
