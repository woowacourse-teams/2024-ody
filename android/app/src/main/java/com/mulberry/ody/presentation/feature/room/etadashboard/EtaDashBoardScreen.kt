package com.mulberry.ody.presentation.feature.room.etadashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.NoRippleInteractionSource
import com.mulberry.ody.presentation.common.modifier.noRippleClickable
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.feature.room.etadashboard.model.EtaStatusUiModel
import com.mulberry.ody.presentation.feature.room.etadashboard.model.MateEtaUiModel
import com.mulberry.ody.presentation.theme.Gray400
import com.mulberry.ody.presentation.theme.Gray400Alpha70
import com.mulberry.ody.presentation.theme.OdyTheme
import com.mulberry.ody.presentation.theme.White
import kotlin.math.roundToInt

@Composable
fun EtaDashboardScreen() {
    Scaffold(
        containerColor = OdyTheme.colors.primary,
        topBar = {
            OdyTopAppBar(
                title = "약속 이름",
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            tint = Color.Unspecified,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_share),
                            tint = Color.Unspecified,
                            contentDescription = null,
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        EtaDashboardContent(
            mateEtas = listOf(
                MateEtaUiModel("올리브", EtaStatusUiModel.Arrived, true, false, 1L, 1L),
                MateEtaUiModel("올리브", EtaStatusUiModel.Missing, true, false, 2L, 2L),
                MateEtaUiModel("올리브", EtaStatusUiModel.ArrivalSoon(20), false, false, 3L, 3L),
                MateEtaUiModel("올리브", EtaStatusUiModel.LateWarning(20), false, false, 4L, 4L),
                MateEtaUiModel("올리브", EtaStatusUiModel.Late(30), false, false, 5L, 5L),
            ),
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun EtaDashboardContent(
    mateEtas: List<MateEtaUiModel>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(42.dp),
    ) {
        items(mateEtas) { mateEta ->
            EtaDashboardItem(mateEta)
        }
    }
}

@Composable
private fun EtaDashboardItem(
    mateEta: MateEtaUiModel,
) {
    val context = LocalContext.current
    var showMissingPopup by rememberSaveable { mutableStateOf(false) }
    val popupSize = remember { mutableStateOf(IntSize.Zero) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = mateEta.nickname,
            style = OdyTheme.typography.pretendardBold20.copy(OdyTheme.colors.quinary),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Button(
            onClick = { },
            modifier = Modifier
                .width(80.dp)
                .height(32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = mateEta.etaStatusUiModel.badgeColor,
                disabledContainerColor = mateEta.etaStatusUiModel.badgeColor,
            ),
            contentPadding = PaddingValues(all = 0.dp),
            shape = RoundedCornerShape(15.dp),
            interactionSource = NoRippleInteractionSource,
            enabled = mateEta.etaStatusUiModel.canNudge()
        ) {
            Text(
                text = stringResource(id = mateEta.etaStatusUiModel.badgeMessageId),
                style = OdyTheme.typography.pretendardRegular16.copy(White),
                textAlign = TextAlign.Center,
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = mateEta.etaStatusUiModel.etaStatusMessage(context),
                style = OdyTheme.typography.pretendardRegular16.copy(OdyTheme.colors.quinary),
            )
            if (mateEta.isMissing) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Gray400)
                        .noRippleClickable { showMissingPopup = !showMissingPopup },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(id = R.string.question_mark),
                        style = OdyTheme.typography.pretendardRegular14.copy(White),
                    )
                    if (showMissingPopup) {
                        val messageId =
                            if (mateEta.isUserSelf) R.string.location_permission_self_guide else R.string.location_permission_friend_guide

                        Popup(
                            alignment = Alignment.TopStart,
                            offset = IntOffset(-popupSize.value.width, -popupSize.value.height),
                            properties = PopupProperties(focusable = true),
                            onDismissRequest = { showMissingPopup = false }
                        ) {
                            Box(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .background(Color.Transparent)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 20.dp,
                                            topEnd = 20.dp,
                                            bottomStart = 20.dp
                                        )
                                    )
                                    .background(Gray400Alpha70)
                                    .onGloballyPositioned { coordinates ->
                                        popupSize.value = coordinates.size
                                    },
                            ) {
                                Text(
                                    text = stringResource(id = messageId),
                                    style = OdyTheme.typography.pretendardRegular12.copy(color = White),
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .padding(horizontal = 16.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun EtaDashboardScreenPreview() {
    OdyTheme {
        EtaDashboardScreen()
    }
}
