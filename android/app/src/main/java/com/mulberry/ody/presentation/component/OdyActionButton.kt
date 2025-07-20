package com.mulberry.ody.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
fun OdyActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val backgroundColor = if (enabled) OdyTheme.colors.secondary else OdyTheme.colors.senary

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(size = 0.dp),
        contentPadding = PaddingValues(vertical = 18.dp),
        interactionSource = NoRippleInteractionSource,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(id = R.string.next_button),
            style = OdyTheme.typography.pretendardBold16.copy(color = White),
        )
    }
}

@Composable
@Preview
private fun OdyActionButtonPreview() {
    OdyTheme {
        Column {
            OdyActionButton(onClick = {})
            Spacer(modifier = Modifier.height(8.dp))
            OdyActionButton(onClick = {}, enabled = false)
        }
    }
}
