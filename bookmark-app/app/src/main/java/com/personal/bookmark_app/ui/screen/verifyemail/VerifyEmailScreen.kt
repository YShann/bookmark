package com.personal.bookmark_app.ui.screen.verifyemail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun VerifyEmailScreen(
    uiState: VerifyEmailUiState,
    onCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            VerifyEmailHeader(
                email = uiState.email
            )

            Spacer(modifier = Modifier.height(36.dp))

            OtpCodeInput(
                value = uiState.verificationCode,
                onValueChange = onCodeChange
            )

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            VerifyEmailActions(
                resendCountdown = uiState.resendCountdown,
                isVerifyEnabled = uiState.isVerifyEnabled,
                canResend = uiState.canResend,
                isLoading = uiState.isLoading,
                onResendClick = onResendClick,
                onVerifyClick = onVerifyClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "請於 10 分鐘內完成電子信箱驗證",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun VerifyEmailHeader(
    email: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.MailOutline,
            contentDescription = "電子信箱",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "驗證你的電子信箱",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "我們已將 6 位數安全驗證碼寄送至",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = email,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun OtpCodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                onValueChange(newValue)
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                repeat(6) { index ->

                    val digit = value.getOrNull(index)?.toString() ?: ""

                    val isFocused = index == value.length && value.length < 6

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (isFocused) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outlineVariant
                            }
                        )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = digit,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // 保留實際輸入欄位，畫面則由上方六個格子呈現
            Box(
                modifier = Modifier.size(1.dp)
            ) {
                innerTextField()
            }
        }
    )
}

@Composable
private fun VerifyEmailActions(
    resendCountdown: Int,
    isVerifyEnabled: Boolean,
    canResend: Boolean,
    isLoading: Boolean,
    onResendClick: () -> Unit,
    onVerifyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "沒有收到驗證碼？",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TextButton(
                onClick = onResendClick,
                enabled = canResend
            ) {
                Text(
                    text = if (canResend) {
                        "重新寄送"
                    } else {
                        "重新寄送（${resendCountdown} 秒）"
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onVerifyClick,
            enabled = isVerifyEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (isLoading) {
                    "驗證中..."
                } else {
                    "驗證並建立帳號"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


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
//import androidx.compose.ui.tooling.preview.Preview
//import com.personal.bookmark_app.ui.theme.BookmarkappTheme
//
//@Preview(
//    name = "驗證電子信箱",
//    showBackground = true,
//    showSystemUi = true
//)
//@Composable
//fun VerifyEmailScreenPreview() {
//    BookmarkappTheme { VerifyEmailScreen() }
//}
//
//@Composable
//fun VerifyEmailScreen() {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .fillMaxSize()
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
//                        color = Color(0xFFF8F5EF),
//                    )
//                    .padding(top = 24.dp,)
//            ){
//                CoilImage(
//                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/e5dfm3lj_expires_30_days.png"},
//                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                    modifier = Modifier
//                        .width(70.dp)
//                        .height(48.dp)
//                )
//                Column(
//                    modifier = Modifier
//                        .height(48.dp)
//                        .fillMaxWidth()
//                ){
//                }
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ){
//                    Column(
//                        modifier = Modifier
//                            .clip(shape = RoundedCornerShape(18.dp))
//                            .background(
//                                color = Color(0xFFFFFFFF),
//                                shape = RoundedCornerShape(18.dp)
//                            )
//                            .padding(top = 8.dp,)
//                    ){
//                        Column(
//                            modifier = Modifier
//                                .padding(bottom = 2.dp,)
//                        ){
//                            Column(
//                                modifier = Modifier
//                                    .padding(start = 64.dp,)
//                                    .border(
//                                        width = 4.dp,
//                                        color = Color(0xFFB45A3C),
//                                        shape = RoundedCornerShape(9999.dp)
//                                    )
//                                    .clip(shape = RoundedCornerShape(9999.dp))
//                                    .width(18.dp)
//                                    .height(18.dp)
//                                    .background(
//                                        color = Color(0xFFF8F5EF),
//                                        shape = RoundedCornerShape(9999.dp)
//                                    )
//                            ){
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .width(92.dp)
//                                    .height(2.dp)
//                            ){
//                            }
//                        }
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            modifier = Modifier
//                                .padding(bottom = 28.dp,end = 29.dp,)
//                                .padding(bottom = 2.dp,start = 29.dp,)
//                        ){
//                            Column(
//                                modifier = Modifier
//                                    .padding(top = 2.dp,bottom = 22.dp,start = 22.dp,end = 2.dp,)
//                                    .clip(shape = RoundedCornerShape(9999.dp))
//                                    .width(10.dp)
//                                    .height(10.dp)
//                                    .background(
//                                        color = Color(0xFF5A6A5A),
//                                        shape = RoundedCornerShape(9999.dp)
//                                    )
//                            ){
//                            }
//                        }
//                    }
//                }
//                Column(
//                    modifier = Modifier
//                        .height(47.dp)
//                        .fillMaxWidth()
//                ){
//                }
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ){
//                    Text("驗證你的電子信箱",
//                        color = Color(0xFF2F2A24),
//                        fontSize = 32.sp,
//                        fontWeight = FontWeight.Bold,
//                    )
//                }
//                Column(
//                    modifier = Modifier
//                        .height(16.dp)
//                        .fillMaxWidth()
//                ){
//                }
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ){
//                    Text("我們已將 6 位數安全驗證碼寄送至",
//                        color = Color(0xFF5F5A52),
//                        fontSize = 17.sp,
//                    )
//                }
//                Column(
//                    modifier = Modifier
//                        .height(20.dp)
//                        .fillMaxWidth()
//                ){
//                }
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ){
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .clip(shape = RoundedCornerShape(9999.dp))
//                            .background(
//                                color = Color(0xFFF1EDE5),
//                                shape = RoundedCornerShape(9999.dp)
//                            )
//                            .padding(vertical = 12.dp,horizontal = 20.dp,)
//                    ){
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                        ){
//                            Column(
//                            ){
//                                CoilImage(
//                                    imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/o6yen8cn_expires_30_days.png"},
//                                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                                    modifier = Modifier
//                                        .clip(shape = RoundedCornerShape(9999.dp))
//                                        .width(29.dp)
//                                        .height(27.dp)
//                                )
//                                Column(
//                                    modifier = Modifier
//                                        .padding(start = 28.dp,)
//                                        .width(12.dp)
//                                        .height(27.dp)
//                                ){
//                                }
//                            }
//                        }
//                        Text("shuhai.reader@cang.app",
//                            color = Color(0xFF2F2A24),
//                            fontSize = 17.sp,
//                            modifier = Modifier
//                                .padding(end = 1.dp,)
//                        )
//                        Column(
//                            modifier = Modifier
//                                .width(12.dp)
//                                .height(27.dp)
//                        ){
//                        }
//                        Column(
//                            modifier = Modifier
//                                .clip(shape = RoundedCornerShape(9999.dp))
//                                .width(8.dp)
//                                .height(8.dp)
//                                .background(
//                                    color = Color(0xFFB8C8B0),
//                                    shape = RoundedCornerShape(9999.dp)
//                                )
//                        ){
//                        }
//                    }
//                }
//                Column(
//                    modifier = Modifier
//                        .height(32.dp)
//                        .fillMaxWidth()
//                ){
//                }
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 24.dp)
//                ){
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier
//                            .clip(shape = RoundedCornerShape(6.dp))
//                            .weight(1f)
//                            .background(
//                                color = Color(0xFFFFFFFF),
//                                shape = RoundedCornerShape(6.dp)
//                            )
//                            .clickable { println("Pressed!") }
//                            .padding(vertical = 13.dp,)
//                    ){
//                        Text("5",
//                            color = Color(0xFF2F2A24),
//                            fontSize = 26.sp,
//                            fontWeight = FontWeight.Bold,
//                        )
//                    }
//                    Column(
//                        modifier = Modifier
//                            .width(11.dp)
//                            .height(70.dp)
//                    ){
//                    }
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier
//                            .clip(shape = RoundedCornerShape(6.dp))
//                            .weight(1f)
//                            .background(
//                                color = Color(0xFFFFFFFF),
//                                shape = RoundedCornerShape(6.dp)
//                            )
//                            .clickable { println("Pressed!") }
//                            .padding(vertical = 13.dp,)
//                    ){
//                        Text("8",
//                            color = Color(0xFF2F2A24),
//                            fontSize = 26.sp,
//                            fontWeight = FontWeight.Bold,
//                        )
//                    }
//                    Column(
//                        modifier = Modifier
//                            .width(12.dp)
//                            .height(70.dp)
//                    ){
//                    }
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier
//                            .clip(shape = RoundedCornerShape(6.dp))
//                            .weight(1f)
//                            .background(
//                                color = Color(0xFFFFFFFF),
//                                shape = RoundedCornerShape(6.dp)
//                            )
//                            .clickable { println("Pressed!") }
//                            .padding(vertical = 13.dp,)
//                    ){
//                        Text("2",
//                            color = Color(0xFF2F2A24),
//                            fontSize = 26.sp,
//                            fontWeight = FontWeight.Bold,
//                        )
//                    }
//                    Column(
//                        modifier = Modifier
//                            .width(12.dp)
//                            .height(70.dp)
//                    ){
//                    }
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier
//                            .clip(shape = RoundedCornerShape(6.dp))
//                            .weight(1f)
//                            .background(
//                                color = Color(0xFFFFFFFF),
//                                shape = RoundedCornerShape(6.dp)
//                            )
//                            .clickable { println("Pressed!") }
//                            .padding(vertical = 13.dp,)
//                    ){
//                        Text("9",
//                            color = Color(0xFF2F2A24),
//                            fontSize = 26.sp,
//                            fontWeight = FontWeight.Bold,
//                        )
//                    }
//                    Column(
//                        modifier = Modifier
//                            .width(12.dp)
//                            .height(70.dp)
//                    ){
//                    }
//                    Box(
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(6.dp))
//                            .height(64.dp)
//                            .weight(1f)
//                            .background(Color.White),
//                        contentAlignment = Alignment.Center,
//                    ) {
//                        Box(Modifier.width(2.dp).height(22.dp).background(Color(0xFFB45A3C)))
//                    }
//                    Box(
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(6.dp))
//                            .height(64.dp)
//                            .weight(1f)
//                            .background(Color(0xFFF3F0E8)),
//                        contentAlignment = Alignment.Center,
//                    ) {
//                        Box(Modifier.size(7.dp).clip(RoundedCornerShape(50)).background(Color(0xFFD7D5CF)))
//                    }
//                }
//                Column(
//                    modifier = Modifier
//                        .height(20.dp)
//                        .fillMaxWidth()
//                ){
//                }
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ){
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .padding(horizontal = 2.dp,)
//                    ){
//                        CoilImage(
//                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/csmrjerq_expires_30_days.png"},
//                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                            modifier = Modifier
//                                .width(14.dp)
//                                .height(11.dp)
//                        )
//                        Column(
//                            modifier = Modifier
//                                .width(10.dp)
//                                .height(22.dp)
//                        ){
//                        }
//                        Text("支援長按粘貼剪貼簿驗證碼",
//                            color = Color(0xFF8A8478),
//                            fontSize = 15.sp,
//                        )
//                    }
//                }
//                Column(
//                    modifier = Modifier
//                        .height(40.dp)
//                        .fillMaxWidth()
//                ){
//                }
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ){
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                    ){
//                        Text("沒有收到驗證碼？",
//                            color = Color(0xFF6F6A61),
//                            fontSize = 16.sp,
//                            modifier = Modifier
//                                .padding(end = 5.dp,)
//                        )
//                        Column(
//                            modifier = Modifier
//                                .width(8.dp)
//                                .height(24.dp)
//                        ){
//                        }
//                        CoilImage(
//                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/sbxnehuw_expires_30_days.png"},
//                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                            modifier = Modifier
//                                .width(14.dp)
//                                .height(14.dp)
//                        )
//                        Column(
//                            modifier = Modifier
//                                .width(8.dp)
//                                .height(24.dp)
//                        ){
//                        }
//                        Text("重新寄送（45 秒）",
//                            color = Color(0xFF6F6A61),
//                            fontSize = 16.sp,
//                        )
//                    }
//                }
//                Column(
//                    modifier = Modifier
//                        .height(48.dp)
//                        .fillMaxWidth()
//                ){
//                }
//                Row(
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .padding(horizontal = 24.dp,)
//                        .clip(shape = RoundedCornerShape(4.dp))
//                        .fillMaxWidth()
//                        .background(
//                            color = Color(0xFF4F634D),
//                            shape = RoundedCornerShape(4.dp)
//                        )
//                        .padding(vertical = 16.dp,)
//                ){
//                    Text("驗證並建立帳號",
//                        color = Color(0xFFFFFFFF),
//                        fontSize = 22.sp,
//                        fontWeight = FontWeight.Bold,
//                    )
//                    Column(
//                        modifier = Modifier
//                            .width(12.dp)
//                            .height(33.dp)
//                    ){
//                    }
//                    CoilImage(
//                        imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/o2p1zhgr_expires_30_days.png"},
//                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                        modifier = Modifier
//                            .clip(shape = RoundedCornerShape(4.dp))
//                            .width(17.dp)
//                            .height(20.dp)
//                    )
//                }
//                Column(
//                    modifier = Modifier
//                        .height(16.dp)
//                        .fillMaxWidth()
//                ){
//                }
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ){
//                    Text("請於 10 分鐘內完成電子信箱驗證",
//                        color = Color(0xFF7A756B),
//                        fontSize = 15.sp,
//                        fontWeight = FontWeight.Bold,
//                    )
//                }
//                Column(
//                    modifier = Modifier
//                        .height(72.dp)
//                        .fillMaxWidth()
//                ){
//                }
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .padding(bottom = 40.dp,)
//                        .fillMaxWidth()
//                ){
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                    ){
//                        Text("原型展示狀態切換",
//                            color = Color(0xFF8A8478),
//                            fontSize = 14.sp,
//                        )
//                        Column(
//                            modifier = Modifier
//                                .width(262.dp)
//                                .height(16.dp)
//                        ){
//                        }
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier
//                                .clip(shape = RoundedCornerShape(9999.dp))
//                                .background(
//                                    color = Color(0xFFE7E2D8),
//                                    shape = RoundedCornerShape(9999.dp)
//                                )
//                                .padding(4.dp)
//                        ){
//                            Column(
//                                modifier = Modifier
//                                    .clip(shape = RoundedCornerShape(9999.dp))
//                                    .background(
//                                        color = Color(0xFFFFFFFF),
//                                        shape = RoundedCornerShape(9999.dp)
//                                    )
//                                    .shadow(
//                                        elevation = 2.dp,
//                                        spotColor = Color(0x12000000),
//                                    )
//                                    .clickable { println("Pressed!") }
//                                    .padding(vertical = 7.dp,horizontal = 20.dp,)
//                            ){
//                                Text("輸入中",
//                                    color = Color(0xFF2F2A24),
//                                    fontSize = 14.sp,
//                                    fontWeight = FontWeight.Bold,
//                                )
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .width(20.dp)
//                                    .height(35.dp)
//                            ){
//                            }
//                            Text("錯誤提示",
//                                color = Color(0xFF6F6A61),
//                                fontSize = 14.sp,
//                                fontWeight = FontWeight.Bold,
//                                modifier = Modifier
//                                    .padding(end = 1.dp,)
//                            )
//                            Column(
//                                modifier = Modifier
//                                    .width(40.dp)
//                                    .height(35.dp)
//                            ){
//                            }
//                            Text("驗證成功",
//                                color = Color(0xFF6F6A61),
//                                fontSize = 14.sp,
//                                fontWeight = FontWeight.Bold,
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}