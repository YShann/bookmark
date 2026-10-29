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
    name = "註冊",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun RegisterScreenPreview() {
    BookmarkappTheme { RegisterScreen() }
}

@Composable
fun RegisterScreen() {
    val textField1 = remember { mutableStateOf("") }
    val textField2 = remember { mutableStateOf("") }
    val textField3 = remember { mutableStateOf("") }
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
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF7F3EA),
                    )
                    .padding(top = 60.dp,bottom = 38.dp,)
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp,)
                ){
                    Column(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(16.dp))
                            .width(72.dp)
                            .height(72.dp)
                            .background(
                                color = Color(0xFFEFEDE6),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ){
                        Canvas(Modifier.fillMaxSize().padding(20.dp)) {
                            val ink = Color(0xFF4F5F4D)
                            val stroke = 2.dp.toPx()
                            val x = size.width
                            val y = size.height
                            drawCircle(ink, radius = x * 0.085f, center = androidx.compose.ui.geometry.Offset(x * 0.5f, y * 0.13f), style = androidx.compose.ui.graphics.drawscope.Stroke(stroke))
                            drawLine(ink, androidx.compose.ui.geometry.Offset(x * 0.14f, y * 0.34f), androidx.compose.ui.geometry.Offset(x * 0.14f, y * 0.78f), stroke)
                            drawLine(ink, androidx.compose.ui.geometry.Offset(x * 0.14f, y * 0.34f), androidx.compose.ui.geometry.Offset(x * 0.5f, y * 0.43f), stroke)
                            drawLine(ink, androidx.compose.ui.geometry.Offset(x * 0.5f, y * 0.43f), androidx.compose.ui.geometry.Offset(x * 0.5f, y * 0.86f), stroke)
                            drawLine(ink, androidx.compose.ui.geometry.Offset(x * 0.5f, y * 0.43f), androidx.compose.ui.geometry.Offset(x * 0.86f, y * 0.34f), stroke)
                            drawLine(ink, androidx.compose.ui.geometry.Offset(x * 0.86f, y * 0.34f), androidx.compose.ui.geometry.Offset(x * 0.86f, y * 0.78f), stroke)
                            drawLine(ink, androidx.compose.ui.geometry.Offset(x * 0.14f, y * 0.78f), androidx.compose.ui.geometry.Offset(x * 0.5f, y * 0.86f), stroke)
                            drawLine(ink, androidx.compose.ui.geometry.Offset(x * 0.5f, y * 0.86f), androidx.compose.ui.geometry.Offset(x * 0.86f, y * 0.78f), stroke)
                        }
                    }
                    Column(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                    ){
                    }
                    Column(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(9999.dp))
                            .width(14.dp)
                            .height(8.dp)
                            .background(
                                color = Color(0xFFA85A3A),
                                shape = RoundedCornerShape(9999.dp)
                            )
                    ){
                    }
                    Column(
                        modifier = Modifier
                            .height(12.dp)
                            .fillMaxWidth()
                    ){
                    }
                    Text("建立新帳號",
                        color = Color(0xFF1F1F1F),
                        fontSize = 29.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column(
                    modifier = Modifier
                        .height(74.dp)
                        .fillMaxWidth()
                ){
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp,)
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Text("暱稱或使用者名稱",
                            color = Color(0xFF5A5A52),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Column(
                            modifier = Modifier
                                .height(10.dp)
                                .fillMaxWidth()
                        ){
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
                                .clip(shape = RoundedCornerShape(12.dp))
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFFFFFFF),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ){
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/xppofw0w_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .padding(start = 16.dp,)
                                    .clip(shape = RoundedCornerShape(12.dp))
                                    .width(14.dp)
                                    .height(15.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(14.dp)
                                    .height(27.dp)
                            ){
                            }
                            TextFieldView(
                                placeholder = "例如：書海漫遊者",
                                value = textField1.value,
                                onValueChange = { newText -> textField1.value = newText },
                                textStyle = TextStyle(
                                    color = Color(0xFF2F2F2F),
                                    fontSize = 18.sp,
                                ),
                                modifier = Modifier
                                    .padding(end = 4.dp,)
                                    .weight(1f)
                                    .padding(vertical = 16.dp,)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .height(18.dp)
                            .fillMaxWidth()
                    ){
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Text("電子郵件",
                            color = Color(0xFF5A5A52),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Column(
                            modifier = Modifier
                                .height(10.dp)
                                .fillMaxWidth()
                        ){
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
                                .clip(shape = RoundedCornerShape(12.dp))
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFFFFFFF),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ){
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/91zjwncb_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .padding(start = 16.dp,)
                                    .clip(shape = RoundedCornerShape(12.dp))
                                    .width(14.dp)
                                    .height(14.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(14.dp)
                                    .height(27.dp)
                            ){
                            }
                            TextFieldView(
                                placeholder = "name@example.com",
                                value = textField2.value,
                                onValueChange = { newText -> textField2.value = newText },
                                textStyle = TextStyle(
                                    color = Color(0xFF2F2F2F),
                                    fontSize = 18.sp,
                                ),
                                modifier = Modifier
                                    .padding(end = 4.dp,)
                                    .weight(1f)
                                    .padding(vertical = 16.dp,)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .height(18.dp)
                            .fillMaxWidth()
                    ){
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Text("設定密碼",
                            color = Color(0xFF5A5A52),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Column(
                            modifier = Modifier
                                .height(10.dp)
                                .fillMaxWidth()
                        ){
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
                                .clip(shape = RoundedCornerShape(12.dp))
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFFFFFFF),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp)
                        ){
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/6l3jya95_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(12.dp))
                                    .width(12.dp)
                                    .height(12.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(14.dp)
                                    .height(27.dp)
                            ){
                            }
                            Text("8 碼以上，含英數字",
                                color = Color(0xFF2F2F2F),
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(end = 3.dp,)
                                    .weight(1f)
                            )
                            Column(
                                modifier = Modifier
                                    .height(27.dp)
                                    .weight(1f)
                            ){
                            }
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/oe6vc4lv_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(12.dp))
                                    .width(17.dp)
                                    .height(13.dp)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .height(18.dp)
                            .fillMaxWidth()
                    ){
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Text("再次輸入密碼",
                            color = Color(0xFF5A5A52),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Column(
                            modifier = Modifier
                                .height(10.dp)
                                .fillMaxWidth()
                        ){
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
                                .clip(shape = RoundedCornerShape(12.dp))
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFFFFFFF),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ){
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/3ryp24ha_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .padding(start = 16.dp,)
                                    .clip(shape = RoundedCornerShape(12.dp))
                                    .width(12.dp)
                                    .height(13.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .width(14.dp)
                                    .height(27.dp)
                            ){
                            }
                            TextFieldView(
                                placeholder = "請再次確認密碼",
                                value = textField3.value,
                                onValueChange = { newText -> textField3.value = newText },
                                textStyle = TextStyle(
                                    color = Color(0xFF2F2F2F),
                                    fontSize = 18.sp,
                                ),
                                modifier = Modifier
                                    .padding(end = 4.dp,)
                                    .weight(1f)
                                    .padding(vertical = 16.dp,)
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .height(18.dp)
                        .fillMaxWidth()
                ){
                }
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp,)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF3F0E7),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(vertical = 12.dp,)
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(start = 16.dp,)
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Text("●",
                                color = Color(0xFF5A6A58),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(end = 3.dp,)
                            )
                            Column(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(21.dp)
                            ){
                            }
                            Text("至少 8 個字元",
                                color = Color(0xFF4F5A4D),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Column(
                            modifier = Modifier
                                .width(18.dp)
                                .height(21.dp)
                        ){
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Text("●",
                                color = Color(0xFF5A6A58),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(end = 3.dp,)
                            )
                            Column(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(21.dp)
                            ){
                            }
                            Text("包含英數字組合",
                                color = Color(0xFF4F5A4D),
                                fontSize = 14.sp,
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
                            .padding(start = 16.dp,)
                    ){
                        Text("●",
                            color = Color(0xFFB0B0A8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(end = 3.dp,)
                        )
                        Column(
                            modifier = Modifier
                                .width(8.dp)
                                .height(21.dp)
                        ){
                        }
                        Text("區分英文大小寫",
                            color = Color(0xFF4F5A4D),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .height(18.dp)
                        .fillMaxWidth()
                ){
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp,)
                ){
                    Column(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color(0xFFECE7DC),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clip(shape = RoundedCornerShape(4.dp))
                            .width(26.dp)
                            .height(26.dp)
                            .background(
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(4.dp)
                            )
                    ){
                    }
                    Column(
                        modifier = Modifier
                            .width(12.dp)
                            .height(48.dp)
                    ){
                    }
                    Text("我已閱讀並同意 聚藏使用者條款 與 隱私權保護政策 ",
                        color = Color(0xFF4A4A44),
                        fontSize = 16.sp,
                        modifier = Modifier
                            .width(244.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .height(28.dp)
                        .fillMaxWidth()
                ){
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 24.dp,)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF4F624D),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .shadow(
                            elevation = 14.dp,
                            spotColor = Color(0x1F000000),
                        )
                        .clickable { println("Pressed!") }
                        .padding(vertical = 16.dp,)
                ){
                    Text("註冊",
                        color = Color(0xFFFFFFFF),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column(
                    modifier = Modifier
                        .height(22.dp)
                        .fillMaxWidth()
                ){
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp,)
                ){
                    Column(
                        modifier = Modifier
                            .height(1.dp)
                            .weight(1f)
                            .background(
                                color = Color(0xFFE5DFD2),
                            )
                    ){
                    }
                    Column(
                        modifier = Modifier
                            .width(14.dp)
                            .height(21.dp)
                    ){
                    }
                    Text("或使用現有平台註冊",
                        color = Color(0xFF8A8A82),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 6.dp,)
                            .weight(1f)
                    )
                    Column(
                        modifier = Modifier
                            .width(14.dp)
                            .height(21.dp)
                    ){
                    }
                    Column(
                        modifier = Modifier
                            .height(1.dp)
                            .weight(1f)
                            .background(
                                color = Color(0xFFE5DFD2),
                            )
                    ){
                    }
                }
                Column(
                    modifier = Modifier
                        .height(18.dp)
                        .fillMaxWidth()
                ){
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 24.dp,)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 14.dp,)
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ){
                        Column(
                            modifier = Modifier
                                .padding(bottom = 2.dp,)
                        ){
                            Column(
                                modifier = Modifier
                                    .padding(start = 21.dp,)
                                    .width(12.dp)
                                    .height(27.dp)
                            ){
                            }
                            CoilImage(
                                imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/dhwf25os_expires_30_days.png"},
                                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(12.dp))
                                    .width(22.dp)
                                    .height(22.dp)
                            )
                        }
                    }
                    Text("Google",
                        color = Color(0xFF2F2F2F),
                        fontSize = 18.sp,
                    )
                }
                Column(
                    modifier = Modifier
                        .height(29.dp)
                        .fillMaxWidth()
                ){
                }
                Text("已經有聚藏帳號？ 前往登入 ↪ ",
                    color = Color(0xFF4A4A44),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(bottom = 28.dp,)
                )
            }
        }
    }
}