package com.mulberry.ody.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.text.style.TextAlign.Companion.End
import androidx.compose.ui.text.style.TextAlign.Companion.Right
import androidx.compose.ui.text.style.TextAlign.Companion.Start
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulberry.ody.presentation.theme.Gray300
import com.mulberry.ody.presentation.theme.Gray350
import com.mulberry.ody.presentation.theme.Gray400
import com.mulberry.ody.presentation.theme.Gray500
import com.mulberry.ody.presentation.theme.OdyTheme

@Composable
fun OdyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    textAlign: TextAlign = Start,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var isFocused by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val modifiedKeyboardActions = KeyboardActions(
        onDone = { invokeAndClearFocus(keyboardActions.onDone, focusManager) },
        onGo = { invokeAndClearFocus(keyboardActions.onGo, focusManager) },
        onNext = { invokeAndClearFocus(keyboardActions.onNext, focusManager) },
        onPrevious = { invokeAndClearFocus(keyboardActions.onPrevious, focusManager) },
        onSearch = { invokeAndClearFocus(keyboardActions.onSearch, focusManager) },
        onSend = { invokeAndClearFocus(keyboardActions.onSend, focusManager) },
    )

    val textSelectionColors =
        TextSelectionColors(
            handleColor = Gray400,
            backgroundColor = Gray300,
        )

    CompositionLocalProvider(
        LocalTextSelectionColors provides textSelectionColors,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.onFocusChanged { isFocused = it.isFocused },
            textStyle =
            OdyTheme.typography.pretendardMedium20.copy(
                color = OdyTheme.colors.quinary,
                textAlign = textAlign,
            ),
            singleLine = true,
            cursorBrush = SolidColor(Gray500),
            keyboardOptions = keyboardType,
            keyboardActions = modifiedKeyboardActions,
            decorationBox = { innerTextField ->
                OdyTextFieldDecorationBox(
                    innerTextField = innerTextField,
                    indicatorColor = OdyTheme.colors.secondary,
                    textAlign = textAlign,
                    placeholder = placeholder,
                    isPlaceHolderVisible = { value.isBlank() && placeholder.isNotBlank() && !isFocused },
                    trailingIcon = trailingIcon,
                )
            },
        )
    }
}

private fun KeyboardActionScope.invokeAndClearFocus(
    action: (KeyboardActionScope.() -> Unit)?,
    focusManager: FocusManager,
) {
    action?.let { it() }
    focusManager.clearFocus()
}

@Composable
private fun OdyTextFieldDecorationBox(
    innerTextField: @Composable () -> Unit,
    indicatorColor: Color,
    textAlign: TextAlign,
    placeholder: String,
    isPlaceHolderVisible: () -> Boolean,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Column {
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            Box(
                modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = textAlign.toAlignment(),
            ) {
                innerTextField()
                if (isPlaceHolderVisible()) {
                    Text(
                        text = placeholder,
                        maxLines = 1,
                        style =
                        OdyTheme.typography.pretendardMedium20.copy(
                            color = Gray350,
                            textAlign = textAlign,
                        ),
                    )
                }
            }
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(6.dp))
                trailingIcon()
            }
        }
        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(color = indicatorColor),
        )
    }
}

private fun TextAlign.toAlignment(): Alignment {
    return when (this) {
        Center -> Alignment.Center
        Right, End -> Alignment.CenterEnd
        else -> Alignment.CenterStart
    }
}

@Composable
@Preview(showSystemUi = true)
private fun OdyTextFieldPreview() {
    OdyTheme {
        Column(modifier = Modifier.padding(horizontal = 40.dp)) {
            OdyTextField(
                modifier = Modifier.padding(top = 100.dp),
                value = "텍스트",
                onValueChange = {},
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        tint = Gray400,
                        contentDescription = null,
                    )
                },
            )
            OdyTextField(
                modifier = Modifier.padding(top = 100.dp),
                value = "",
                placeholder = "placeholder",
                textAlign = Center,
                onValueChange = {},
            )
        }
    }
}
