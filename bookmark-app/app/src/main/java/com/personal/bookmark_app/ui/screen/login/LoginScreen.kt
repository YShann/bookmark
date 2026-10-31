package com.personal.bookmark_app.ui.screen.login

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.unit.dp
import com.personal.bookmark_app.ui.components.TextFieldView

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            // 品牌識別
            LoginHeader()

            Spacer(modifier = Modifier.height(40.dp))

            // 登入表單
            LoginForm(
                uiState = uiState,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onRememberMeChange = onRememberMeChange,
                onPasswordVisibilityChange = onPasswordVisibilityChange,
                onLoginClick = onLoginClick,
                onForgotPasswordClick = onForgotPasswordClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 其他登入方式
            OtherLoginSection(
                onGoogleLoginClick = onGoogleLoginClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 註冊入口
            RegisterSection(
                onRegisterClick = onRegisterClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 服務條款
            Text(
                text = "登入即代表您同意服務條款與隱私權政策",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun LoginHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "藏",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "聚藏",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "收藏每一份值得再次閱讀的美好",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LoginForm(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "歡迎回來",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "登入帳號，繼續整理你的收藏",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Email
        Text(
            text = "電子郵件",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextFieldView(
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = "name@example.com",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "密碼",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "忘記密碼？",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(
                    onClick = onForgotPasswordClick
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextFieldView(
            value = uiState.password,
            onValueChange = onPasswordChange,
            placeholder = "請輸入密碼",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = onPasswordVisibilityChange
                ) {
                    Icon(
                        imageVector = if (uiState.isPasswordVisible) {
                            Icons.Outlined.Visibility
                        } else {
                            Icons.Outlined.VisibilityOff
                        },
                        contentDescription = if (uiState.isPasswordVisible) {
                            "隱藏密碼"
                        } else {
                            "顯示密碼"
                        },
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Remember Me
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = uiState.rememberMe,
                onCheckedChange = onRememberMeChange
            )

            Text(
                text = "記住我的登入狀態",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Error Message
        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = uiState.errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = onLoginClick,
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "登入聚藏",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun OtherLoginSection(
    onGoogleLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Text(
                text = "其他登入方式",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = onGoogleLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Text(
                text = "G",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "使用 Google 帳號登入",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun RegisterSection(
    onRegisterClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "還沒有聚藏帳號？",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "立即註冊",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(
                onClick = onRegisterClick
            )
        )
    }
}

//package com.personal.bookmark_app.ui.screen
//
//import androidx.compose.runtime.*
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
//import androidx.compose.ui.text.*
//import androidx.compose.ui.layout.*
//import com.personal.bookmark_app.ui.components.TextFieldView
//import com.skydoves.landscapist.*
//import com.skydoves.landscapist.coil3.*
//import androidx.compose.ui.tooling.preview.Preview
//import com.personal.bookmark_app.ui.theme.BookmarkappTheme
//
//@Preview(
//    name = "登入",
//    showBackground = true,
//    showSystemUi = true
//)
//@Composable
//fun LoginScreenPreview() {
//    BookmarkappTheme { LoginScreen() }
//}
//
//@Composable
//fun LoginScreen() {
//    val textField1 = remember { mutableStateOf("") }
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
//                        color = Color(0xFFF8F5EE),
//                    )
//                    .padding(top = 55.dp,bottom = 31.dp,start = 24.dp,end = 24.dp,)
//            ){
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ){
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                    ){
//                        Column(
//                            modifier = Modifier
//                                .clip(shape = RoundedCornerShape(12.dp))
//                                .background(
//                                    color = Color(0xFFEFEDE6),
//                                    shape = RoundedCornerShape(12.dp)
//                                )
//                                .padding(top = 27.dp,)
//                        ){
//                            Column(
//                                modifier = Modifier
//                                    .padding(end = 13.dp,)
//                                    .padding(start = 27.dp,)
//                            ){
//                                Column(
//                                    modifier = Modifier
//                                        .border(
//                                            width = 2.dp,
//                                            color = Color(0xFF4F5F4D),
//                                            shape = RoundedCornerShape(4.dp)
//                                        )
//                                        .clip(shape = RoundedCornerShape(4.dp))
//                                        .width(42.dp)
//                                        .height(32.dp)
//                                        .padding(vertical = 11.dp,horizontal = 18.dp,)
//                                ){
//                                }
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .width(82.dp)
//                                    .height(9.dp)
//                            ){
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .padding(bottom = 2.dp,)
//                                    .padding(start = 64.dp,end = 2.dp,)
//                            ){
//                                Column(
//                                    modifier = Modifier
//                                        .clip(shape = RoundedCornerShape(9999.dp))
//                                        .width(16.dp)
//                                        .height(16.dp)
//                                        .background(
//                                            color = Color(0xFF9A4F31),
//                                            shape = RoundedCornerShape(9999.dp)
//                                        )
//                                ){
//                                }
//                            }
//                        }
//                        Column(
//                            modifier = Modifier
//                                .height(21.dp)
//                                .fillMaxWidth()
//                        ){
//                        }
//                        Text("聚藏",
//                            color = Color(0xFF1F1F1B),
//                            fontSize = 34.sp,
//                            fontWeight = FontWeight.Bold,
//                        )
//                        Column(
//                            modifier = Modifier
//                                .height(14.dp)
//                                .fillMaxWidth()
//                        ){
//                        }
//                    }
//                    Column(
//                        modifier = Modifier
//                            .height(38.dp)
//                            .fillMaxWidth()
//                    ){
//                    }
//                }
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .padding(bottom = 77.dp,)
//                        .fillMaxWidth()
//                ){
//                    Row(
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                    ){
//                        Text("電子郵件",
//                            color = Color(0xFF4A4A43),
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Bold,
//                        )
//                        Text("帳號信箱",
//                            color = Color(0xFF8A8A82),
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Bold,
//                        )
//                    }
//                    Column(
//                        modifier = Modifier
//                            .height(8.dp)
//                            .fillMaxWidth()
//                    ){
//                    }
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .clip(shape = RoundedCornerShape(8.dp))
//                            .fillMaxWidth()
//                            .background(
//                                color = Color(0xFFF3F0E8),
//                                shape = RoundedCornerShape(8.dp)
//                            )
//                    ){
//                        CoilImage(
//                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/uyezzjj6_expires_30_days.png"},
//                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                            modifier = Modifier
//                                .padding(start = 19.dp,)
//                                .clip(shape = RoundedCornerShape(8.dp))
//                                .width(22.dp)
//                                .height(22.dp)
//                        )
//                        Column(
//                            modifier = Modifier
//                                .width(15.dp)
//                                .height(27.dp)
//                        ){
//                        }
//                        TextFieldView(
//                            placeholder = "name@example.com",
//                            value = textField1.value,
//                            onValueChange = { newText -> textField1.value = newText },
//                            textStyle = TextStyle(
//                                color = Color(0xFF3A3A35),
//                                fontSize = 18.sp,
//                            ),
//                            modifier = Modifier
//                                .padding(end = 4.dp,)
//                                .weight(1f)
//                                .padding(vertical = 14.dp,)
//                        )
//                    }
//                    Column(
//                        modifier = Modifier
//                            .height(20.dp)
//                            .fillMaxWidth()
//                    ){
//                    }
//                    Row(
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                    ){
//                        Text("密碼",
//                            color = Color(0xFF4A4A43),
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Bold,
//                        )
//                        Text("8 碼以上字符",
//                            color = Color(0xFF8A8A82),
//                            fontSize = 15.sp,
//                            fontWeight = FontWeight.Bold,
//                        )
//                    }
//                    Column(
//                        modifier = Modifier
//                            .height(8.dp)
//                            .fillMaxWidth()
//                    ){
//                    }
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .clip(shape = RoundedCornerShape(8.dp))
//                            .fillMaxWidth()
//                            .background(
//                                color = Color(0xFFF3F0E8),
//                                shape = RoundedCornerShape(8.dp)
//                            )
//                            .padding(vertical = 14.dp,horizontal = 20.dp,)
//                    ){
//                        CoilImage(
//                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/22ar4gnq_expires_30_days.png"},
//                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                            modifier = Modifier
//                                .clip(shape = RoundedCornerShape(8.dp))
//                                .width(17.dp)
//                                .height(20.dp)
//                        )
//                        Column(
//                            modifier = Modifier
//                                .width(16.dp)
//                                .height(27.dp)
//                        ){
//                        }
//                        Text("請輸入 8 碼以上密碼",
//                            color = Color(0xFF3A3A35),
//                            fontSize = 17.sp,
//                            modifier = Modifier
//                                .padding(end = 2.dp,)
//                                .weight(1f)
//                        )
//                        Column(
//                            modifier = Modifier
//                                .padding(end = 1.dp,)
//                                .height(27.dp)
//                                .weight(1f)
//                        ){
//                        }
//                        CoilImage(
//                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/4izsxnnq_expires_30_days.png"},
//                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                            modifier = Modifier
//                                .clip(shape = RoundedCornerShape(8.dp))
//                                .width(22.dp)
//                                .height(17.dp)
//                        )
//                    }
//                    Column(
//                        modifier = Modifier
//                            .height(24.dp)
//                            .fillMaxWidth()
//                    ){
//                    }
//                    Row(
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                    ){
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                        ){
//                            Column(
//                                modifier = Modifier
//                                    .width(20.dp)
//                                    .height(20.dp)
//                                    .background(
//                                        color = Color(0xFFE7E4DC),
//                                    )
//                            ){
//                            }
//                            Column(
//                                modifier = Modifier
//                                    .width(8.dp)
//                                    .height(24.dp)
//                            ){
//                            }
//                            Text("記住我的登入狀態",
//                                color = Color(0xFF5A5A52),
//                                fontSize = 16.sp,
//                            )
//                        }
//                        Text("忘記密碼？",
//                            color = Color(0xFF5A5A52),
//                            fontSize = 16.sp,
//                        )
//                    }
//                    Column(
//                        modifier = Modifier
//                            .height(32.dp)
//                            .fillMaxWidth()
//                    ){
//                    }
//                    Row(
//                        horizontalArrangement = Arrangement.Center,
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .clip(shape = RoundedCornerShape(12.dp))
//                            .fillMaxWidth()
//                            .background(
//                                color = Color(0xFF4F5F4D),
//                                shape = RoundedCornerShape(12.dp)
//                            )
//                            .shadow(
//                                elevation = 8.dp,
//                                spotColor = Color(0x2E4F5F4D),
//                            )
//                            .padding(vertical = 15.dp,)
//                    ){
//                        Text("登入聚藏",
//                            color = Color(0xFFFFFFFF),
//                            fontSize = 20.sp,
//                            fontWeight = FontWeight.Bold,
//                        )
//                        Column(
//                            modifier = Modifier
//                                .width(12.dp)
//                                .height(30.dp)
//                        ){
//                        }
//                        CoilImage(
//                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/td8fosff_expires_30_days.png"},
//                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                            modifier = Modifier
//                                .clip(shape = RoundedCornerShape(12.dp))
//                                .width(15.dp)
//                                .height(18.dp)
//                        )
//                    }
//                    Column(
//                        modifier = Modifier
//                            .height(32.dp)
//                            .fillMaxWidth()
//                    ){
//                    }
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                    ){
//                        Column(
//                            modifier = Modifier
//                                .height(1.dp)
//                                .weight(1f)
//                                .background(
//                                    color = Color(0xFFDDD8CF),
//                                )
//                        ){
//                        }
//                        Column(
//                            modifier = Modifier
//                                .width(16.dp)
//                                .height(22.dp)
//                        ){
//                        }
//                        Text("或透過其他方式登入",
//                            color = Color(0xFF8A8A82),
//                            fontSize = 15.sp,
//                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier
//                                .padding(end = 12.dp,)
//                                .weight(1f)
//                        )
//                        Column(
//                            modifier = Modifier
//                                .width(15.dp)
//                                .height(22.dp)
//                        ){
//                        }
//                        Column(
//                            modifier = Modifier
//                                .height(1.dp)
//                                .weight(1f)
//                                .background(
//                                    color = Color(0xFFDDD8CF),
//                                )
//                        ){
//                        }
//                    }
//                    Column(
//                        modifier = Modifier
//                            .height(24.dp)
//                            .fillMaxWidth()
//                    ){
//                    }
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .clip(shape = RoundedCornerShape(12.dp))
//                            .fillMaxWidth()
//                            .background(
//                                color = Color(0xFFF3F0E8),
//                                shape = RoundedCornerShape(12.dp)
//                            )
//                            .padding(vertical = 18.dp,horizontal = 20.dp,)
//                    ){
//                        Column(
//                            modifier = Modifier
//                                .border(
//                                    width = 6.dp,
//                                    color = Color(0xFFEA4335),
//                                    shape = RoundedCornerShape(9999.dp)
//                                )
//                                .clip(shape = RoundedCornerShape(9999.dp))
//                                .clickable { println("Pressed!") }
//                                .padding(vertical = 15.dp,horizontal = 16.dp,)
//                        ){
//                            Column(
//                                modifier = Modifier
//                                    .width(10.dp)
//                                    .height(6.dp)
//                                    .background(
//                                        color = Color(0xFF4285F4),
//                                    )
//                            ){
//                            }
//                        }
//                        Column(
//                            modifier = Modifier
//                                .width(16.dp)
//                                .height(36.dp)
//                        ){
//                        }
//                        Text("使用 Google 帳號快速登入",
//                            color = Color(0xFF1F1F1B),
//                            fontSize = 17.sp,
//                            modifier = Modifier
//                                .padding(end = 2.dp,)
//                        )
//                        Column(
//                            modifier = Modifier
//                                .width(75.dp)
//                                .height(36.dp)
//                        ){
//                        }
//                        CoilImage(
//                            imageModel = {"https://storage.googleapis.com/tagjs-prod.appspot.com/v1/dgVhxlMbDH/ix1n8xa4_expires_30_days.png"},
//                            imageOptions = ImageOptions(contentScale = ContentScale.Crop),
//                            modifier = Modifier
//                                .clip(shape = RoundedCornerShape(12.dp))
//                                .width(10.dp)
//                                .height(15.dp)
//                        )
//                    }
//                    Column(
//                        modifier = Modifier
//                            .height(24.dp)
//                            .fillMaxWidth()
//                    ){
//                    }
//                    Text("還沒有聚藏帳號？ 立即註冊 ",
//                        color = Color(0xFF000000),
//                        fontSize = 17.sp,
//                    )
//                    Column(
//                        modifier = Modifier
//                            .height(16.dp)
//                            .fillMaxWidth()
//                    ){
//                    }
//                    Text("登入即代表您同意 服務條款 與 隱私權政策 ",
//                        color = Color(0xFF000000),
//                        fontSize = 14.sp,
//                        fontWeight = FontWeight.Bold,
//                    )
//                }
//            }
//        }
//    }
//}