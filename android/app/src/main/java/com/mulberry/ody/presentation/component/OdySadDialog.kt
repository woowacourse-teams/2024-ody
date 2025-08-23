package com.mulberry.ody.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.modifier.noRippleClickable
import com.mulberry.ody.presentation.theme.Cream
import com.mulberry.ody.presentation.theme.OdyTheme
import com.mulberry.ody.presentation.theme.Red

@Composable
fun OdySadDialog(
    title: @Composable () -> Unit,
    onClickConfirm: () -> Unit,
    onClickCancel: () -> Unit,
    confirmButtonText: String,
    modifier: Modifier = Modifier,
    subtitle: (@Composable () -> Unit)? = null,
    confirmButtonTextColor: Color = Red,
) {
    Dialog(
        onDismissRequest = onClickCancel,
    ) {
        Card(
            modifier = modifier.wrapContentSize(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(width = 1.dp, color = Cream),
            colors = CardDefaults.cardColors().copy(containerColor = OdyTheme.colors.primary),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_sad_ody),
                    modifier =
                        Modifier
                            .width(150.dp)
                            .padding(top = 30.dp)
                            .padding(bottom = 34.dp),
                    contentDescription = null,
                )
                title()
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    subtitle()
                }
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Row(
                    modifier = Modifier.wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(vertical = 22.dp)
                                .noRippleClickable { onClickCancel() },
                        text = stringResource(id = R.string.cancel_button),
                        style = OdyTheme.typography.pretendardMedium18.copy(color = OdyTheme.colors.quinary),
                        textAlign = TextAlign.Center,
                    )
                    VerticalDivider(modifier = Modifier.height(36.dp))
                    Text(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(vertical = 22.dp)
                                .noRippleClickable(onClickConfirm),
                        text = confirmButtonText,
                        style = OdyTheme.typography.pretendardMedium18.copy(color = confirmButtonTextColor),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun OdySadDialogPreview() {
    OdyTheme {
        OdySadDialog(
            title = {
                Text(
                    text = "슬픈 오디 다이얼로그 타이틀",
                    style = OdyTheme.typography.pretendardBold24.copy(OdyTheme.colors.quinary),
                )
            },
            subtitle = {
                Text(
                    text = "서브 타이틀",
                    style = OdyTheme.typography.pretendardMedium16.copy(OdyTheme.colors.quinary),
                )
            },
            onClickCancel = {},
            onClickConfirm = {},
            confirmButtonText = "확인",
        )
    }
}
