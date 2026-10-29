package com.personal.bookmark_app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun TextFieldView(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "placeholder...",
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color.Gray,
                        fontSize = textStyle.fontSize,
                        style = textStyle
                    )
                }
                innerTextField()
            }
        },
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        modifier = modifier
    )
}

