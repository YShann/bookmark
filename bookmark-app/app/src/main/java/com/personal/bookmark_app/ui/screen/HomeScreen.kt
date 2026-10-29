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
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import androidx.compose.ui.tooling.preview.Preview
import com.personal.bookmark_app.ui.theme.BookmarkappTheme

@Preview(
    name = "聚藏首頁預覽",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun HomeScreenPreview() {
    BookmarkappTheme {
        HomeScreen()
    }
}


@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0xFFFAF9F4),
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
                        color = Color(0xFFFAF9F4),
                    )
                    .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
            ){
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        CoilImage(
                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/fg1q4rmy_expires_30_days.png"},
                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                            modifier = Modifier
                                .padding(end = 1.dp,)
                                .clip(shape = RoundedCornerShape(16.dp))
                                .size(40.dp)
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            Column(
                            ){
                                Text("聚藏",
                                    color = Color(0xFF111111),
                                fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(start = 12.dp,)
                                )
                            }
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        CoilImage(
                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/syibdhx6_expires_30_days.png"},
                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(16.dp))
                                .size(40.dp)
                        )
                        Column(
                            modifier = Modifier
                                .width(8.dp)
                        ){
                        }
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
                                .padding(vertical = 10.dp, horizontal = 12.dp)
                        ){
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/587dtapt_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(6.dp))
                                    .size(16.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(8.dp)
                            ){
                            }
                            Text("新增收藏",
                                color = Color(0xFFFFFFFF),
                                fontSize = 16.sp,
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF4F1E8),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 16.dp, horizontal = 18.dp)
                ){
                    Column(
                        modifier = Modifier
                            .padding(end = 1.dp,)
                            .weight(1f)
                            .padding(bottom = 1.dp,)
                    ){
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 21.dp,)
                        ){
                            Text("今日拾藏 • 3 則精選已抽出",
                                color = Color(0xFF222222),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        Column(
                            modifier = Modifier
                                .height(9.dp)
                                .fillMaxWidth()
                        ){
                        }
                        Text("沉澱數位碎片，翻開為你挑選的安靜篇章。",
                            color = Color(0xFF6F6A60),
                            fontSize = 16.sp,
                        )
                    }
                        CoilImage(
                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/64hg8oiy_expires_30_days.png"},
                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(12.dp))
                                .size(40.dp)
                        )
                }
                Spacer(modifier = Modifier.height(28.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("收藏分類夾", color = Color(0xFF222222), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("5 個分類", color = Color(0xFF7A7468), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        CoilImage(
                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/2o833yek_expires_30_days.png"},
                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(10.dp))
                                .width(64.dp)
                                .height(40.dp)
                        )
                        Column(
                            modifier = Modifier
                                .width(8.dp)
                        ){
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/xla93nhy_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(16.dp)
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ){
                                Column(
                                    modifier = Modifier
                                        .padding(end = 7.dp,)
                                ){
                                    Text("編輯",
                                        color = Color(0xFF4A4A4A),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .padding(start = 7.dp,)
                                    )
                                    Column(
                                        modifier = Modifier
                                            .width(8.dp)
                                            .height(24.dp)
                                    ){
                                    }
                                }
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier.height(14.dp).fillMaxWidth()
                ){
                }
                Column(
                    modifier = Modifier
                        .padding(bottom = 40.dp,)
                        .fillMaxWidth()
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Column(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(12.dp))
                                .weight(1f)
                                .background(
                                    color = Color(0xFFFFFFFF),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .shadow(
                                    elevation = 2.dp,
                                    spotColor = Color(0x12000000),
                                )
                                .padding(bottom = 11.dp,)
                        ){
                            CoilImage(
                                imageModel = {"https://placehold.co/600x420/png?text=Desk+with+laptop+and+notebook+by+a+window"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(12.dp))
                                    .fillMaxWidth()
                                    .aspectRatio(1.52f)
                            )
                            Column(
                                modifier = Modifier
                                    .height(7.dp)
                                    .fillMaxWidth()
                            ){
                            }
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp,)
                            ){
                                Text("未分類 (18) ",
                                    color = Color(0xFF2A2A2A),
                                    fontSize = 20.sp,
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .width(12.dp)
                                .height(185.dp)
                        ){
                        }
                        Column(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(12.dp))
                                .weight(1f)
                                .background(
                                    color = Color(0xFFFFFFFF),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .shadow(
                                    elevation = 2.dp,
                                    spotColor = Color(0x12000000),
                                )
                                .padding(bottom = 11.dp,)
                        ){
                            CoilImage(
                                imageModel = {"https://placehold.co/600x420/png?text=Old+street+tea+shop+front+with+signboard"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(12.dp))
                                    .fillMaxWidth()
                                    .aspectRatio(1.52f)
                            )
                            Column(
                                modifier = Modifier
                                    .height(7.dp)
                                    .fillMaxWidth()
                            ){
                            }
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp,)
                            ){
                                Text("工作 (21) ",
                                    color = Color(0xFF2A2A2A),
                                    fontSize = 20.sp,
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .height(12.dp)
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
                                .clip(shape = RoundedCornerShape(12.dp))
                                .weight(1f)
                                .background(
                                    color = Color(0xFFFFFFFF),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .shadow(
                                    elevation = 2.dp,
                                    spotColor = Color(0x12000000),
                                )
                                .padding(bottom = 11.dp,)
                        ){
                            CoilImage(
                                imageModel = {"https://placehold.co/600x420/png?text=Old+street+tea+shop+front+with+signboard"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(12.dp))
                                    .fillMaxWidth()
                                    .aspectRatio(1.52f)
                            )
                            Column(
                                modifier = Modifier
                                    .height(7.dp)
                                    .fillMaxWidth()
                            ){
                            }
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp,)
                            ){
                                Text("生活靈感 (9) ",
                                    color = Color(0xFF2A2A2A),
                                    fontSize = 20.sp,
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .width(12.dp)
                                .height(231.dp)
                        ){
                        }
                        Column(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(12.dp))
                                .weight(1f)
                                .background(
                                    color = Color(0xFFFFFFFF),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .shadow(
                                    elevation = 2.dp,
                                    spotColor = Color(0x12000000),
                                )
                                .padding(bottom = 11.dp,)
                        ){
                            CoilImage(
                                imageModel = {"https://placehold.co/600x420/png?text=Desk+with+laptop+and+notebook+by+a+window"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(12.dp))
                                    .fillMaxWidth()
                                    .aspectRatio(1.52f)
                            )
                            Column(
                                modifier = Modifier
                                    .height(7.dp)
                                    .fillMaxWidth()
                            ){
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp,)
                            ){
                                Text("京都喫茶店與老鋪書店選書漫遊指南 (14) ",
                                    color = Color(0xFF2A2A2A),
                                    fontSize = 20.sp,
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .height(12.dp)
                            .fillMaxWidth()
                    ){
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(12.dp))
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .shadow(
                                elevation = 8.dp,
                                spotColor = Color(0x12000000),
                            )
                            .padding(bottom = 10.dp,)
                    ){
                        CoilImage(
                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/2nl27x0f_expires_30_days.png"},
                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                            modifier = Modifier
                                .padding(bottom = 1.dp,)
                                .clip(shape = RoundedCornerShape(12.dp))
                                .fillMaxWidth()
                                .aspectRatio(1.52f)
                        )
                        Column(
                        ){
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                            ){
                            }
                            Column(
                                modifier = Modifier
                                    .padding(end = 61.dp,)
                                    .padding(horizontal = 12.dp,)
                            ){
                                Text("閱讀筆記 (0) ",
                                    color = Color(0xFF2A2A2A),
                                    fontSize = 20.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
