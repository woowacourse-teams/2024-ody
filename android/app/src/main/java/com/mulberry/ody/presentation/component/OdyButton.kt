package com.mulberry.ody.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.NoRippleInteractionSource
import com.mulberry.ody.presentation.theme.Gray350
import com.mulberry.ody.presentation.theme.OdyTheme
import com.mulberry.ody.presentation.theme.White

@Composable
fun OdyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val backgroundColor = if (enabled) OdyTheme.colors.secondary else OdyTheme.colors.octonary
    val odyIconColor = if (enabled) White else Gray350
    val textColor = if (enabled) OdyTheme.colors.secondaryVariant else Gray350

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(width = if (enabled) 0.dp else 1.dp, color = Gray350),
        contentPadding = PaddingValues(all = 10.dp),
        interactionSource = NoRippleInteractionSource,
        modifier = modifier,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_ody),
                tint = odyIconColor,
                contentDescription = null,
                modifier = Modifier.padding(end = 4.dp),
            )
            Text(
                text = stringResource(id = R.string.ody_button),
                style = OdyTheme.typography.pretendardBold16.copy(color = textColor),
            )
        }
    }
}

@Composable
@Preview
fun OdyButtonPreview() {
    OdyTheme {
        Column {
            OdyButton(
                onClick = {},
                enabled = true,
            )
            OdyButton(
                onClick = {},
                enabled = false,
            )
        }
    }
}
