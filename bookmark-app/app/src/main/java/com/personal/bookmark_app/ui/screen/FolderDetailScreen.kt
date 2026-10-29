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
                Column(
                    modifier = Modifier
                        .padding(bottom = 40.dp,)
                        .fillMaxWidth()
                ){
                    Column(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(14.dp))
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(16.dp)
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ){
                                Text("Threads · 2024/06/12",
                                    color = Color(0xFF8A8478),
                                    fontSize = 14.sp,
                                )
                                Column(
                                    modifier = Modifier
                                        .height(8.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Text("台北巷弄裡的質感咖啡廳",
                                    color = Color(0xFF2F2A24),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Column(
                                    modifier = Modifier
                                        .height(4.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Text("在這座城市裡，總有一些不經意走進去...",
                                    color = Color(0xFF5F5A52),
                                    fontSize = 15.sp,
                                )
                            }
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/1qs9qdeg_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(18.dp)
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
                            CoilImage(
                                imageModel = {"https://placehold.co/110x110"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(6.dp))
                                    .width(110.dp)
                                    .height(110.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(150.dp)
                            ){
                            }
                            Column(
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFFE2DDD1),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clip(shape = RoundedCornerShape(6.dp))
                                    .weight(1f)
                                    .background(
                                        color = Color(0xFFF3EFE6),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(vertical = 11.dp,)
                            ){
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(horizontal = 13.dp,)
                                        .fillMaxWidth()
                                ){
                                    CoilImage(
                                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/vvw7z57i_expires_30_days.png"},
                                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                        modifier = Modifier
                                            .width(12.dp)
                                            .height(13.dp)
                                    )
                                    Column(
                                        modifier = Modifier
                                            .width(8.dp)
                                            .height(22.dp)
                                    ){
                                    }
                                    Text("筆記",
                                        color = Color(0xFF4E624B),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .padding(end = 1.dp,)
                                    )
                                    Column(
                                        modifier = Modifier
                                            .height(22.dp)
                                            .weight(1f)
                                    ){
                                    }
                                    CoilImage(
                                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/9cs7fqpc_expires_30_days.png"},
                                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                        modifier = Modifier
                                            .width(12.dp)
                                            .height(12.dp)
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 1.dp,)
                                        .height(10.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 1.dp,)
                                        .fillMaxWidth()
                                        .padding(horizontal = 13.dp,)
                                ){
                                    Text("老闆推薦手沖那加雪菲，二樓窗邊位置採光最好，平日不限時，適合帶筆電或閱讀一整個下午。",
                                        color = Color(0xFF3A352E),
                                        fontSize = 16.sp,
                                    )
                                }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .height(9.dp)
                                .fillMaxWidth()
                        ){
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Column(
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(9999.dp))
                                    .background(
                                        color = Color(0xFFE8E4DB),
                                        shape = RoundedCornerShape(9999.dp)
                                    )
                                    .clickable { println("Pressed!") }
                                    .padding(vertical = 3.dp,horizontal = 12.dp,)
                            ){
                                Text("美食",
                                    color = Color(0xFF5A5A52),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(27.dp)
                            ){
                            }
                            Column(
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(9999.dp))
                                    .background(
                                        color = Color(0xFFE8E4DB),
                                        shape = RoundedCornerShape(9999.dp)
                                    )
                                    .clickable { println("Pressed!") }
                                    .padding(vertical = 3.dp,horizontal = 12.dp,)
                            ){
                                Text("生活",
                                    color = Color(0xFF5A5A52),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
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
                            .clip(shape = RoundedCornerShape(14.dp))
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(16.dp)
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ){
                                Text("Dcard · 2024/06/10",
                                    color = Color(0xFF8A8478),
                                    fontSize = 14.sp,
                                )
                                Column(
                                    modifier = Modifier
                                        .height(8.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Text("讓生活變美好的 5 個習慣",
                                    color = Color(0xFF2F2A24),
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Column(
                                    modifier = Modifier
                                        .height(4.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Text("從日常小事開始，慢慢變成更喜歡的自...",
                                    color = Color(0xFF5F5A52),
                                    fontSize = 15.sp,
                                )
                            }
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/7va09esv_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(18.dp)
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
                            CoilImage(
                                imageModel = {"https://placehold.co/110x110"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(6.dp))
                                    .width(110.dp)
                                    .height(110.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(110.dp)
                            ){
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                            ){
                                Column(
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(9999.dp))
                                        .background(
                                            color = Color(0xFFE8E4DB),
                                            shape = RoundedCornerShape(9999.dp)
                                        )
                                        .clickable { println("Pressed!") }
                                        .padding(vertical = 3.dp,horizontal = 12.dp,)
                                ){
                                    Text("生活",
                                        color = Color(0xFF5A5A52),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(27.dp)
                                ){
                                }
                                Column(
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(9999.dp))
                                        .background(
                                            color = Color(0xFFE8E4DB),
                                            shape = RoundedCornerShape(9999.dp)
                                        )
                                        .clickable { println("Pressed!") }
                                        .padding(vertical = 3.dp,horizontal = 12.dp,)
                                ){
                                    Text("閱讀",
                                        color = Color(0xFF5A5A52),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
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
                            .clip(shape = RoundedCornerShape(14.dp))
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(16.dp)
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ){
                                Text("Instagram · 2024/06/08",
                                    color = Color(0xFF8A8478),
                                    fontSize = 14.sp,
                                )
                                Column(
                                    modifier = Modifier
                                        .height(8.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Text("京都小旅行｜必去的 5 個景點",
                                    color = Color(0xFF2F2A24),
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Column(
                                    modifier = Modifier
                                        .height(4.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Text("這次的京都行，記錄了幾個很喜歡的地...",
                                    color = Color(0xFF5F5A52),
                                    fontSize = 15.sp,
                                )
                            }
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/qf16ghf0_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(18.dp)
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
                            CoilImage(
                                imageModel = {"https://placehold.co/110x110"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(6.dp))
                                    .width(110.dp)
                                    .height(110.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(110.dp)
                            ){
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                            ){
                                Column(
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(9999.dp))
                                        .background(
                                            color = Color(0xFFE8E4DB),
                                            shape = RoundedCornerShape(9999.dp)
                                        )
                                        .clickable { println("Pressed!") }
                                        .padding(vertical = 3.dp,horizontal = 12.dp,)
                                ){
                                    Text("旅遊",
                                        color = Color(0xFF5A5A52),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(27.dp)
                                ){
                                }
                                Column(
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(9999.dp))
                                        .background(
                                            color = Color(0xFFE8E4DB),
                                            shape = RoundedCornerShape(9999.dp)
                                        )
                                        .clickable { println("Pressed!") }
                                        .padding(vertical = 3.dp,horizontal = 12.dp,)
                                ){
                                    Text("生活",
                                        color = Color(0xFF5A5A52),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
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
                            .clip(shape = RoundedCornerShape(14.dp))
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(vertical = 15.dp,horizontal = 16.dp,)
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ){
                                Text("YouTube · 2024/06/05",
                                    color = Color(0xFF8A8478),
                                    fontSize = 14.sp,
                                )
                                Column(
                                    modifier = Modifier
                                        .height(8.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Text("簡單好吃的家常料理｜番茄燉飯",
                                    color = Color(0xFF2F2A24),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(bottom = 2.dp,)
                                )
                                Column(
                                    modifier = Modifier
                                        .height(4.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Text("食材簡單、步驟不難，在家也能輕鬆做...",
                                    color = Color(0xFF5F5A52),
                                    fontSize = 15.sp,
                                )
                            }
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/mdfpzunc_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(18.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .height(13.dp)
                                .fillMaxWidth()
                        ){
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            CoilImage(
                                imageModel = {"https://placehold.co/110x110"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(6.dp))
                                    .width(110.dp)
                                    .height(110.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(110.dp)
                            ){
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                            ){
                                Column(
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(9999.dp))
                                        .background(
                                            color = Color(0xFFE8E4DB),
                                            shape = RoundedCornerShape(9999.dp)
                                        )
                                        .clickable { println("Pressed!") }
                                        .padding(vertical = 3.dp,horizontal = 12.dp,)
                                ){
                                    Text("美食",
                                        color = Color(0xFF5A5A52),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(27.dp)
                                ){
                                }
                                Column(
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(9999.dp))
                                        .background(
                                            color = Color(0xFFE8E4DB),
                                            shape = RoundedCornerShape(9999.dp)
                                        )
                                        .clickable { println("Pressed!") }
                                        .padding(vertical = 3.dp,horizontal = 12.dp,)
                                ){
                                    Text("料理",
                                        color = Color(0xFF5A5A52),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .height(17.dp)
                            .fillMaxWidth()
                    ){
                    }
                    Column(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(14.dp))
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(16.dp)
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ){
                                Text("網頁 · 2024/06/02",
                                    color = Color(0xFF8A8478),
                                    fontSize = 14.sp,
                                )
                                Column(
                                    modifier = Modifier
                                        .height(8.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Text("如何建立自己的數位資料庫",
                                    color = Color(0xFF2F2A24),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Column(
                                    modifier = Modifier
                                        .height(4.dp)
                                        .fillMaxWidth()
                                ){
                                }
                                Text("整理收藏、管理資訊，讓靈感不再從指...",
                                    color = Color(0xFF5F5A52),
                                    fontSize = 15.sp,
                                )
                            }
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/18hc3u3p_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(18.dp)
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
                            CoilImage(
                                imageModel = {"https://placehold.co/110x110"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(6.dp))
                                    .width(110.dp)
                                    .height(110.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(110.dp)
                            ){
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                            ){
                                Column(
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(9999.dp))
                                        .background(
                                            color = Color(0xFFE8E4DB),
                                            shape = RoundedCornerShape(9999.dp)
                                        )
                                        .clickable { println("Pressed!") }
                                        .padding(vertical = 3.dp,horizontal = 12.dp,)
                                ){
                                    Text("設計",
                                        color = Color(0xFF5A5A52),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(27.dp)
                                ){
                                }
                                Column(
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(9999.dp))
                                        .background(
                                            color = Color(0xFFE8E4DB),
                                            shape = RoundedCornerShape(9999.dp)
                                        )
                                        .clickable { println("Pressed!") }
                                        .padding(vertical = 3.dp,horizontal = 12.dp,)
                                ){
                                    Text("工具",
                                        color = Color(0xFF5A5A52),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}