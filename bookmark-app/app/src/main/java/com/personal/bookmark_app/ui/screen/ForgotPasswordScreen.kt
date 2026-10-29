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
import androidx.compose.ui.text.style.*
import androidx.compose.ui.layout.*
import com.personal.bookmark_app.ui.components.TextFieldView
import com.skydoves.landscapist.*
import com.skydoves.landscapist.coil3.*
import androidx.compose.ui.tooling.preview.Preview
import com.personal.bookmark_app.ui.theme.BookmarkappTheme

@Preview(
    name = "忘記密碼",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ForgotPasswordScreenPreview() {
    BookmarkappTheme { ForgotPasswordScreen() }
}

@Composable
fun ForgotPasswordScreen() {
    val textField1 = remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
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
                        color = Color(0xFFF8F5EE),
                    )
                    .padding(top = 48.dp,start = 24.dp,end = 24.dp,)
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 1.dp,)
                ){
                    Text("重設密碼",
                        color = Color(0xFF1F1A16),
                        fontSize = 33.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Column(
                        modifier = Modifier
                            .height(19.dp)
                            .fillMaxWidth()
                    ){
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp,)
                    ){
                        Text("請輸入您註冊時使用的電子郵件信箱。我們將發送 \n一封包含重設密碼連結的驗證信件給您。 ",
                            color = Color(0xFF5E5A54),
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .height(34.dp)
                        .fillMaxWidth()
                ){
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(bottom = 24.dp,)
                        .fillMaxWidth()
                ){
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Text("註冊電子郵件",
                            color = Color(0xFF2F2A24),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text("必填",
                            color = Color(0xFF7A746B),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
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
                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/yxnqqath_expires_30_days.png"},
                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                            modifier = Modifier
                                .padding(start = 18.dp,)
                                .clip(shape = RoundedCornerShape(12.dp))
                                .width(22.dp)
                                .height(22.dp)
                        )
                        Column(
                            modifier = Modifier
                                .width(12.dp)
                                .height(22.dp)
                        ){
                        }
                        TextFieldView(
                            placeholder = "name@example.com",
                            value = textField1.value,
                            onValueChange = { newText -> textField1.value = newText },
                            textStyle = TextStyle(
                                color = Color(0xFF4A4540),
                                fontSize = 18.sp,
                            ),
                            modifier = Modifier
                                .padding(end = 4.dp,)
                                .weight(1f)
                                .padding(vertical = 18.dp,)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                    ){
                    }
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    ) {
                        Text("ⓘ", color = Color(0xFF7A746B), fontSize = 17.sp, lineHeight = 22.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "若 5 分鐘內未收到信件，請檢查垃圾郵件或重新發送。",
                            color = Color(0xFF7A746B),
                            fontSize = 16.sp,
                            lineHeight = 21.sp,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Column(
                        modifier = Modifier
                            .height(34.dp)
                            .fillMaxWidth()
                    ){
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(10.dp))
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF4E5F49),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .shadow(
                                elevation = 8.dp,
                                spotColor = Color(0x1F000000),
                            )
                            .padding(vertical = 16.dp,)
                    ){
                        CoilImage(
                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/im17w4rl_expires_30_days.png"},
                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(10.dp))
                                .width(18.dp)
                                .height(18.dp)
                        )
                        Column(
                            modifier = Modifier
                                .width(12.dp)
                                .height(18.dp)
                        ){
                        }
                        Text("發送重設密碼連結",
                            color = Color(0xFFFFFFFF),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth()
                    ){
                    }
                    Text("想起密碼了？ 返回登入",
                        color = Color(0xFF4E5F49),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}