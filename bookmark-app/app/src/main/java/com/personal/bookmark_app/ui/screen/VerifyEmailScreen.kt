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
fun VerifyEmailScreen() {
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
                        color = Color(0xFFF8F5EF),
                    )
                    .padding(top = 24.dp,)
            ){
                CoilImage(
                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/e5dfm3lj_expires_30_days.png"},
                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                    modifier = Modifier
                        .width(70.dp)
                        .height(48.dp)
                )
                Column(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                ){
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Column(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(18.dp))
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .padding(top = 8.dp,)
                    ){
                        Column(
                            modifier = Modifier
                                .padding(bottom = 2.dp,)
                        ){
                            Column(
                                modifier = Modifier
                                    .padding(start = 64.dp,)
                                    .border(
                                        width = 4.dp,
                                        color = Color(0xFFB45A3C),
                                        shape = RoundedCornerShape(9999.dp)
                                    )
                                    .clip(shape = RoundedCornerShape(9999.dp))
                                    .width(18.dp)
                                    .height(18.dp)
                                    .background(
                                        color = Color(0xFFF8F5EF),
                                        shape = RoundedCornerShape(9999.dp)
                                    )
                            ){
                            }
                            Column(
                                modifier = Modifier
                                    .width(92.dp)
                                    .height(2.dp)
                            ){
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(bottom = 28.dp,end = 29.dp,)
                                .padding(bottom = 2.dp,start = 29.dp,)
                        ){
                            Column(
                                modifier = Modifier
                                    .padding(top = 2.dp,bottom = 22.dp,start = 22.dp,end = 2.dp,)
                                    .clip(shape = RoundedCornerShape(9999.dp))
                                    .width(10.dp)
                                    .height(10.dp)
                                    .background(
                                        color = Color(0xFF5A6A5A),
                                        shape = RoundedCornerShape(9999.dp)
                                    )
                            ){
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .height(47.dp)
                        .fillMaxWidth()
                ){
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Text("驗證你的電子信箱",
                        color = Color(0xFF2F2A24),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth()
                ){
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Text("我們已將 6 位數安全驗證碼寄送至",
                        color = Color(0xFF5F5A52),
                        fontSize = 17.sp,
                    )
                }
                Column(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                ){
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(9999.dp))
                            .background(
                                color = Color(0xFFF1EDE5),
                                shape = RoundedCornerShape(9999.dp)
                            )
                            .padding(vertical = 12.dp,horizontal = 20.dp,)
                    ){
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Column(
                            ){
                                CoilImage(
                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/o6yen8cn_expires_30_days.png"},
                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                    modifier = Modifier
                                        .clip(shape = RoundedCornerShape(9999.dp))
                                        .width(29.dp)
                                        .height(27.dp)
                                )
                                Column(
                                    modifier = Modifier
                                        .padding(start = 28.dp,)
                                        .width(12.dp)
                                        .height(27.dp)
                                ){
                                }
                            }
                        }
                        Text("shuhai.reader@cang.app",
                            color = Color(0xFF2F2A24),
                            fontSize = 17.sp,
                            modifier = Modifier
                                .padding(end = 1.dp,)
                        )
                        Column(
                            modifier = Modifier
                                .width(12.dp)
                                .height(27.dp)
                        ){
                        }
                        Column(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(9999.dp))
                                .width(8.dp)
                                .height(8.dp)
                                .background(
                                    color = Color(0xFFB8C8B0),
                                    shape = RoundedCornerShape(9999.dp)
                                )
                        ){
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                ){
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(6.dp))
                            .weight(1f)
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { println("Pressed!") }
                            .padding(vertical = 13.dp,)
                    ){
                        Text("5",
                            color = Color(0xFF2F2A24),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .width(11.dp)
                            .height(70.dp)
                    ){
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(6.dp))
                            .weight(1f)
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { println("Pressed!") }
                            .padding(vertical = 13.dp,)
                    ){
                        Text("8",
                            color = Color(0xFF2F2A24),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .width(12.dp)
                            .height(70.dp)
                    ){
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(6.dp))
                            .weight(1f)
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { println("Pressed!") }
                            .padding(vertical = 13.dp,)
                    ){
                        Text("2",
                            color = Color(0xFF2F2A24),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .width(12.dp)
                            .height(70.dp)
                    ){
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(6.dp))
                            .weight(1f)
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { println("Pressed!") }
                            .padding(vertical = 13.dp,)
                    ){
                        Text("9",
                            color = Color(0xFF2F2A24),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .width(12.dp)
                            .height(70.dp)
                    ){
                    }
                    Column(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(6.dp))
                            .height(70.dp)
                            .weight(1f)
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(6.dp)
                            )
                    ){
                    }
                    Column(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(6.dp))
                            .height(70.dp)
                            .weight(1f)
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(6.dp)
                            )
                    ){
                    }
                }
                Column(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                ){
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 2.dp,)
                    ){
                        CoilImage(
                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/csmrjerq_expires_30_days.png"},
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
                        Text("支援長按粘貼剪貼簿驗證碼",
                            color = Color(0xFF8A8478),
                            fontSize = 15.sp,
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ){
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        Text("沒有收到驗證碼？",
                            color = Color(0xFF6F6A61),
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(end = 5.dp,)
                        )
                        Column(
                            modifier = Modifier
                                .width(8.dp)
                                .height(24.dp)
                        ){
                        }
                        CoilImage(
                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/sbxnehuw_expires_30_days.png"},
                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                            modifier = Modifier
                                .width(14.dp)
                                .height(14.dp)
                        )
                        Column(
                            modifier = Modifier
                                .width(8.dp)
                                .height(24.dp)
                        ){
                        }
                        Text("重新寄送（45 秒）",
                            color = Color(0xFF6F6A61),
                            fontSize = 16.sp,
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                ){
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 24.dp,)
                        .clip(shape = RoundedCornerShape(4.dp))
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF4F634D),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(vertical = 20.dp,)
                ){
                    Text("驗證並建立帳號",
                        color = Color(0xFFFFFFFF),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Column(
                        modifier = Modifier
                            .width(12.dp)
                            .height(33.dp)
                    ){
                    }
                    CoilImage(
                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/o2p1zhgr_expires_30_days.png"},
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(4.dp))
                            .width(17.dp)
                            .height(20.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth()
                ){
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Text("請於 10 分鐘內完成電子信箱驗證",
                        color = Color(0xFF7A756B),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column(
                    modifier = Modifier
                        .height(96.dp)
                        .fillMaxWidth()
                ){
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(bottom = 40.dp,)
                        .fillMaxWidth()
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ){
                        Text("原型展示狀態切換",
                            color = Color(0xFF8A8478),
                            fontSize = 14.sp,
                        )
                        Column(
                            modifier = Modifier
                                .width(262.dp)
                                .height(16.dp)
                        ){
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(9999.dp))
                                .background(
                                    color = Color(0xFFE7E2D8),
                                    shape = RoundedCornerShape(9999.dp)
                                )
                                .padding(4.dp)
                        ){
                            Column(
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(9999.dp))
                                    .background(
                                        color = Color(0xFFFFFFFF),
                                        shape = RoundedCornerShape(9999.dp)
                                    )
                                    .shadow(
                                        elevation = 2.dp,
                                        spotColor = Color(0x12000000),
                                    )
                                    .clickable { println("Pressed!") }
                                    .padding(vertical = 7.dp,horizontal = 20.dp,)
                            ){
                                Text("輸入中",
                                    color = Color(0xFF2F2A24),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(35.dp)
                            ){
                            }
                            Text("錯誤提示",
                                color = Color(0xFF6F6A61),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(end = 1.dp,)
                            )
                            Column(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(35.dp)
                            ){
                            }
                            Text("驗證成功",
                                color = Color(0xFF6F6A61),
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