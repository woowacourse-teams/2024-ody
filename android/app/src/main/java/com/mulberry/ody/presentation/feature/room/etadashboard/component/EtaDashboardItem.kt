package com.mulberry.ody.presentation.feature.room.etadashboard.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.NoRippleInteractionSource
import com.mulberry.ody.presentation.common.modifier.noRippleClickable
import com.mulberry.ody.presentation.component.OdyTooltip
import com.mulberry.ody.presentation.feature.room.etadashboard.model.EtaStatusUiModel
import com.mulberry.ody.presentation.feature.room.etadashboard.model.MateEtaUiModel
import com.mulberry.ody.presentation.theme.Gray400
import com.mulberry.ody.presentation.theme.OdyTheme
import com.mulberry.ody.presentation.theme.White

@Composable
fun EtaDashboardItem(
    mateEta: MateEtaUiModel,
    modifier: Modifier = Modifier,
    onClickNudge: (MateEtaUiModel) -> Unit = { },
) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = mateEta.nickname,
            style = OdyTheme.typography.pretendardBold20.copy(OdyTheme.colors.quinary),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        EtaBadge(
            etaStatus = mateEta.status,
            onClick = { onClickNudge(mateEta) },
            enabled = mateEta.canNudge,
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = mateEta.status.etaStatusMessage(context),
                style = OdyTheme.typography.pretendardRegular16.copy(OdyTheme.colors.quinary),
            )
            if (mateEta.isMissing) {
                Spacer(modifier = Modifier.width(4.dp))
                MissingGuideButton(isUserSelf = mateEta.isUserSelf)
            }
        }
    }
}

@Composable
private fun EtaBadge(
    etaStatus: EtaStatusUiModel,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = if (enabled) 0.85f else 1.0f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "",
    )

    Button(
        onClick = onClick,
        modifier =
            modifier
                .width(80.dp)
                .height(32.dp)
                .scale(scale),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = etaStatus.badgeColor,
                disabledContainerColor = etaStatus.badgeColor,
            ),
        contentPadding = PaddingValues(all = 0.dp),
        shape = RoundedCornerShape(15.dp),
        interactionSource = NoRippleInteractionSource,
        enabled = enabled,
    ) {
        Text(
            text = stringResource(id = etaStatus.badgeMessageId),
            style = OdyTheme.typography.pretendardRegular16.copy(White),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MissingGuideButton(isUserSelf: Boolean) {
    var showMissingTooltip by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Gray400)
                .noRippleClickable { showMissingTooltip = !showMissingTooltip },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(id = R.string.question_mark),
            style = OdyTheme.typography.pretendardRegular14.copy(White),
        )
        if (showMissingTooltip) {
            MissingTooltip(
                isUserSelf = isUserSelf,
                onCloseTooltip = { showMissingTooltip = false },
            )
        }
    }
}

@Composable
private fun MissingTooltip(
    isUserSelf: Boolean,
    onCloseTooltip: () -> Unit,
) {
    val tooltipSize = remember { mutableStateOf(IntSize.Zero) }
    val messageId =
        if (isUserSelf) {
            R.string.location_permission_self_guide
        } else {
            R.string.location_permission_friend_guide
        }

    OdyTooltip(
        title = stringResource(id = messageId),
        offset = IntOffset(-tooltipSize.value.width, -tooltipSize.value.height),
        onDismissRequest = onCloseTooltip,
        modifier =
            Modifier.onGloballyPositioned { coordinates ->
                tooltipSize.value = coordinates.size
            },
    )
}

@Composable
@Preview(showSystemUi = true)
private fun EtaDashboardItemPreview() {
    OdyTheme {
        Column {
            Spacer(modifier = Modifier.height(32.dp))
            EtaDashboardItem(
                mateEta =
                    MateEtaUiModel(
                        "올리브",
                        EtaStatusUiModel.Missing,
                        userId = 1L,
                        mateId = 1L,
                    ),
            )
            Spacer(modifier = Modifier.height(32.dp))
            EtaDashboardItem(
                mateEta =
                    MateEtaUiModel(
                        "올리브",
                        EtaStatusUiModel.Arrived,
                        userId = 1L,
                        mateId = 2L,
                    ),
            )
        }
    }
}
