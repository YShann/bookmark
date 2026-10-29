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

@Composable
fun SearchBookmarkScreen() {
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
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp,)
                ){
                    CoilImage(
                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/3njkep1p_expires_30_days.png"},
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                        modifier = Modifier
                            .padding(end = 1.dp,)
                            .width(13.dp)
                            .height(22.dp)
                    )
                    Column(
                        modifier = Modifier
                            .width(21.dp)
                            .height(55.dp)
                    ){
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(14.dp))
                            .weight(1f)
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(vertical = 14.dp,horizontal = 19.dp,)
                    ){
                        CoilImage(
                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/p1k0y9fe_expires_30_days.png"},
                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(14.dp))
                                .width(18.dp)
                                .height(18.dp)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ){
                            Column(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(27.dp)
                            ){
                            }
                            Text("搜尋標題、網址或個人筆記備註...",
                                color = Color(0xFF3A3A3A),
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(start = 15.dp,)
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
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(6.dp))
                            .background(
                                color = Color(0xFF4F5F4B),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .shadow(
                                elevation = 2.dp,
                                spotColor = Color(0x0D000000),
                            )
                            .padding(vertical = 12.dp,horizontal = 16.dp,)
                    ){
                        Text("全部平台",
                            color = Color(0xFFFFFFFF),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .width(15.dp)
                        )
                        Column(
                            modifier = Modifier
                                .width(20.dp)
                                .height(88.dp)
                        ){
                        }
                        Text("48",
                            color = Color(0xFFFFFFFF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .width(12.dp)
                            .height(112.dp)
                    ){
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 1.dp,)
                            .clip(shape = RoundedCornerShape(6.dp))
                            .weight(1f)
                            .background(
                                color = Color(0xFFECE9E2),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(vertical = 43.dp,)
                    ){
                        CoilImage(
                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/y225kofb_expires_30_days.png"},
                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(6.dp))
                                .width(13.dp)
                                .height(14.dp)
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(end = 2.dp,)
                        ){
                            Column(
                                modifier = Modifier
                                    .padding(end = 7.dp,)
                            ){
                                Column(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(22.dp)
                                ){
                                }
                                Text("Threads",
                                    color = Color(0xFF5F5F5F),
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .padding(start = 7.dp,)
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Column(
                                modifier = Modifier
                                    .padding(end = 13.dp,)
                            ){
                                Column(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(22.dp)
                                ){
                                }
                                Text("14",
                                    color = Color(0xFF5F5F5F),
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .width(12.dp)
                            .height(112.dp)
                    ){
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(6.dp))
                            .weight(1f)
                            .background(
                                color = Color(0xFFECE9E2),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(vertical = 43.dp,horizontal = 12.dp,)
                    ){
                        CoilImage(
                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/iw13xu5j_expires_30_days.png"},
                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(6.dp))
                                .width(13.dp)
                                .height(14.dp)
                        )
                        Column(
                            modifier = Modifier
                                .padding(end = 3.dp,)
                                .weight(1f)
                        ){
                            Column(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(22.dp)
                            ){
                            }
                            Text("Instagram",
                                color = Color(0xFF5F5F5F),
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(start = 7.dp,)
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Column(
                                modifier = Modifier
                                    .padding(end = 13.dp,)
                            ){
                                Column(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(22.dp)
                                ){
                                }
                                Text("12",
                                    color = Color(0xFF5F5F5F),
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                ){
                }
                Column(
                    modifier = Modifier
                        .padding(bottom = 40.dp,)
                        .fillMaxWidth()
                ){
                    Column(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color(0xFFE6E1D8),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(shape = RoundedCornerShape(12.dp))
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .shadow(
                                elevation = 6.dp,
                                spotColor = Color(0x08000000),
                            )
                            .padding(18.dp)
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(4.dp))
                                    .background(
                                        color = Color(0xFFF3EFE6),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(vertical = 3.dp,horizontal = 10.dp,)
                            ){
                                CoilImage(
                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/mn3527kv_expires_30_days.png"},
                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(4.dp))
                                        .width(11.dp)
                                        .height(12.dp)
                                )
                                Column(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height(21.dp)
                                ){
                                }
                                Text("Instagram",
                                    color = Color(0xFF6A6A6A),
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
                                color = Color(0xFFB45A2A),
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(end = 2.dp,)
                            )
                            Column(
                                modifier = Modifier
                                    .padding(end = 5.dp,)
                                    .padding(top = 3.dp,bottom = 3.dp,start = 12.dp,)
                            ){
                                Text("•",
                                    color = Color(0xFF8A8A8A),
                                    fontSize = 14.sp,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(27.dp)
                            ){
                            }
                            Text("2024/04/08",
                                color = Color(0xFF8A8A8A),
                                fontSize = 14.sp,
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
                        ){
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 2.dp,)
                            ){
                                Text("昭和復古喫茶店的招牌字體特輯",
                                    color = Color(0xFF222222),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Column(
                                    modifier = Modifier
                                        .height(12.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 11.dp,)
                                ){
                                    Text("手繪圓體、金屬壓印與暖黃霓虹管。在神保町與谷中的角落，收集舊時代咖茶館...",
                                        color = Color(0xFF4F4F4F),
                                        fontSize = 15.sp,
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(133.dp)
                            ){
                            }
                            Column(
                                modifier = Modifier
                                    .padding(top = 68.dp,bottom = 5.dp,start = 57.dp,end = 4.dp,)
                                    .clip(shape = RoundedCornerShape(4.dp))
                                    .background(
                                        color = Color(0x8A000000),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(vertical = 1.dp,horizontal = 8.dp,)
                            ){
                                Text("1/6",
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .height(16.dp)
                                .fillMaxWidth()
                        ){
                        }
                        Row(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(6.dp))
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFF4F1E8),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(vertical = 15.dp,horizontal = 16.dp,)
                        ){
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/5qaroglq_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(6.dp))
                                    .width(12.dp)
                                    .height(13.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(44.dp)
                            ){
                            }
                            Text("筆記：招牌字型的圓弧處理很值得在下次品牌專案借鏡參考。",
                                color = Color(0xFF5F5F5F),
                                fontSize = 14.sp,
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
                                color = Color(0xFFE6E1D8),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(shape = RoundedCornerShape(12.dp))
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .shadow(
                                elevation = 6.dp,
                                spotColor = Color(0x08000000),
                            )
                            .padding(18.dp)
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(4.dp))
                                    .background(
                                        color = Color(0xFFF3EFE6),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(vertical = 3.dp,horizontal = 10.dp,)
                            ){
                                CoilImage(
                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/srr3wpwx_expires_30_days.png"},
                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(4.dp))
                                        .width(13.dp)
                                        .height(13.dp)
                                )
                                Column(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height(21.dp)
                                ){
                                }
                                Text("Web 專題",
                                    color = Color(0xFF6A6A6A),
                                    fontSize = 13.sp,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(27.dp)
                            ){
                            }
                            Text("#深度閱讀",
                                color = Color(0xFFB45A2A),
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(end = 2.dp,)
                            )
                            Column(
                                modifier = Modifier
                                    .padding(end = 5.dp,)
                                    .padding(top = 3.dp,bottom = 3.dp,start = 12.dp,)
                            ){
                                Text("•",
                                    color = Color(0xFF8A8A8A),
                                    fontSize = 14.sp,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(27.dp)
                            ){
                            }
                            Text("2025/03/11",
                                color = Color(0xFF8A8A8A),
                                fontSize = 14.sp,
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
                        ){
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(top = 1.dp,)
                            ){
                                Text("專注的技術：擺脫演算法餵養的「慢讀」練習",
                                    color = Color(0xFF222222),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Column(
                                    modifier = Modifier
                                        .height(12.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 11.dp,)
                                ){
                                    Text("在即時訊息過載的時代，用紙本書與手寫筆記重構深度思考的心理空間，重拾閱...",
                                        color = Color(0xFF4F4F4F),
                                        fontSize = 15.sp,
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .height(19.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 1.dp,)
                                ){
                                    CoilImage(
                                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/78ct8eze_expires_30_days.png"},
                                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                        modifier = Modifier
                                            .width(13.dp)
                                            .height(10.dp)
                                    )
                                    Column(
                                        modifier = Modifier
                                            .width(9.dp)
                                            .height(21.dp)
                                    ){
                                    }
                                    Text("theverge.com/features/...",
                                        color = Color(0xFF8A8A8A),
                                        fontSize = 14.sp,
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(170.dp)
                            ){
                            }
                            Column(
                                modifier = Modifier
                                    .padding(top = 67.dp,bottom = 5.dp,start = 50.dp,end = 4.dp,)
                                    .clip(shape = RoundedCornerShape(4.dp))
                                    .background(
                                        color = Color(0x8A000000),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(vertical = 1.dp,horizontal = 8.dp,)
                            ){
                                Text("專欄",
                                    color = Color(0xFFFFFFFF),
                                    fontSize = 13.sp,
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
                    Column(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color(0xFFE6E1D8),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(shape = RoundedCornerShape(12.dp))
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .shadow(
                                elevation = 6.dp,
                                spotColor = Color(0x08000000),
                            )
                            .padding(18.dp)
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(4.dp))
                                    .background(
                                        color = Color(0xFFF3EFE6),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(vertical = 3.dp,horizontal = 10.dp,)
                            ){
                                CoilImage(
                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/fsvwclof_expires_30_days.png"},
                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(4.dp))
                                        .width(11.dp)
                                        .height(12.dp)
                                )
                                Column(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height(21.dp)
                                ){
                                }
                                Text("Threads",
                                    color = Color(0xFF6A6A6A),
                                    fontSize = 13.sp,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(27.dp)
                            ){
                            }
                            Text("#建築空間",
                                color = Color(0xFFB45A2A),
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(end = 3.dp,)
                            )
                            Column(
                                modifier = Modifier
                                    .padding(end = 5.dp,)
                                    .padding(top = 3.dp,bottom = 3.dp,start = 12.dp,)
                            ){
                                Text("•",
                                    color = Color(0xFF8A8A8A),
                                    fontSize = 14.sp,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(27.dp)
                            ){
                            }
                            Text("2024/10/02",
                                color = Color(0xFF8A8A8A),
                                fontSize = 14.sp,
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
                        ){
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 1.dp,)
                            ){
                                Text("京都「町家」改造手記：光影流動與舊木料的共存",
                                    color = Color(0xFF222222),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Column(
                                    modifier = Modifier
                                        .height(12.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 11.dp,)
                                ){
                                    Text("百年土壁在自然天光之下的沉靜表情。透過將綠側外移，重新梳理庭院枯山水與居...",
                                        color = Color(0xFF4F4F4F),
                                        fontSize = 15.sp,
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(131.dp)
                            ){
                            }
                            CoilImage(
                                imageModel = {"https://placehold.co/180x180/png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .width(96.dp)
                                    .height(96.dp)
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
                                .clip(shape = RoundedCornerShape(6.dp))
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFF4F1E8),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(vertical = 15.dp,horizontal = 16.dp,)
                        ){
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/fia1936f_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(6.dp))
                                    .width(12.dp)
                                    .height(13.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(44.dp)
                            ){
                            }
                            Text("筆記：縱側與日式庭院採光的斜角度值得紀錄在改造清單。",
                                color = Color(0xFF5F5F5F),
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}