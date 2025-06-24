package com.mulberry.ody.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.mulberry.ody.presentation.theme.Gray400Alpha70
import com.mulberry.ody.presentation.theme.OdyTheme
import com.mulberry.ody.presentation.theme.White

@Composable
fun OdyTooltip(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: IntOffset = IntOffset(0, 0),
    shape: RoundedCornerShape = RoundedCornerShape(size = 20.dp),
) {
    Popup(
        alignment = Alignment.TopStart,
        offset = offset,
        properties = PopupProperties(focusable = true),
        onDismissRequest = onDismissRequest,
    ) {
        Box(
            modifier =
                modifier
                    .wrapContentSize()
                    .clip(shape)
                    .background(Gray400Alpha70),
        ) {
            Text(
                text = title,
                style = OdyTheme.typography.pretendardRegular12.copy(color = White),
                modifier =
                    Modifier
                        .padding(vertical = 8.dp)
                        .padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun OdyTooltipPreview() {
    OdyTheme {
        OdyTooltip(
            title = "위치 권한을 켜서 오디인지 공유해 보세요.",
            offset = IntOffset(0, 0),
            onDismissRequest = { },
        )
    }
}
