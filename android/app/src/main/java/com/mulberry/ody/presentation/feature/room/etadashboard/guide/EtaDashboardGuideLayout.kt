package com.mulberry.ody.presentation.feature.room.etadashboard.guide

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.modifier.noRippleClickable
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.feature.room.etadashboard.component.EtaDashboardItem
import com.mulberry.ody.presentation.feature.room.etadashboard.guide.model.EtaDashboardGuideUiModel
import com.mulberry.ody.presentation.feature.room.etadashboard.model.EtaStatusUiModel
import com.mulberry.ody.presentation.feature.room.etadashboard.model.MateEtaUiModel
import com.mulberry.ody.presentation.theme.Gray900Alpha50
import com.mulberry.ody.presentation.theme.OdyTheme
import com.tbuonomo.viewpagerdotsindicator.pxToDp

private const val GUIDE_TARGET_INDEX = 1

@Composable
fun EtaDashboardGuideLayout(
    guideUiModel: EtaDashboardGuideUiModel,
    onClick: () -> Unit,
) {
    Scaffold(
        containerColor = OdyTheme.colors.primary,
        topBar = { EtaDashboardGuideTopBar() },
    ) { innerPadding ->
        EtaDashboardGuideContent(
            guideUiModel = guideUiModel,
            onClick = onClick,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun EtaDashboardGuideTopBar() {
    Box {
        OdyTopAppBar(
            title = stringResource(id = R.string.eta_dashboard_guide_title),
            navigationIcon = {
                IconButton(onClick = { }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        tint = Color.Unspecified,
                        contentDescription = null,
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_share),
                        tint = Color.Unspecified,
                        contentDescription = null,
                    )
                }
            },
        )
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .background(Gray900Alpha50),
        )
    }
}

@Composable
private fun EtaDashboardGuideContent(
    guideUiModel: EtaDashboardGuideUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var guideOverlayStartY by remember { mutableIntStateOf(0) }
    var rootLayoutHeight by remember { mutableIntStateOf(0) }
    val guideOverlayHeight = (rootLayoutHeight - guideOverlayStartY).pxToDp()

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    rootLayoutHeight = coordinates.size.height
                },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                itemsIndexed(
                    guideUiModel.mateEtas,
                    key = { _, item -> item.mateId },
                ) { index, mateEta ->
                    EtaDashboardGuideItem(
                        mateEta = mateEta,
                        isTargetItem = index == GUIDE_TARGET_INDEX,
                        isFirst = index == 0,
                        isLast = index == guideUiModel.mateEtas.lastIndex,
                        onTargetItemPositioned = {
                            guideOverlayStartY = it.positionInRoot().y.toInt()
                        },
                    )
                }
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Gray900Alpha50),
            )
        }
        EtaDashboardGuideOverlay(
            guideUiModel = guideUiModel,
            onClick = onClick,
            modifier =
                Modifier
                    .offset { IntOffset(0, guideOverlayStartY) }
                    .offset(y = (-20).dp)
                    .fillMaxWidth()
                    .height(guideOverlayHeight),
        )
    }
}

@Composable
private fun EtaDashboardGuideItem(
    mateEta: MateEtaUiModel,
    isTargetItem: Boolean,
    onTargetItemPositioned: (LayoutCoordinates) -> Unit,
    isFirst: Boolean,
    isLast: Boolean,
) {
    val topPadding = if (isFirst) 53.dp else 20.dp
    val bottomPadding = if (isLast) 53.dp else 20.dp

    Box {
        EtaDashboardItem(
            mateEta = mateEta,
            modifier = Modifier.padding(top = topPadding, bottom = bottomPadding),
        )
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .background(Gray900Alpha50),
        )

        if (isTargetItem) {
            EtaDashboardItem(
                mateEta = mateEta,
                modifier =
                    Modifier
                        .matchParentSize()
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                        .background(OdyTheme.colors.primary, RoundedCornerShape(20.dp))
                        .onGloballyPositioned(onTargetItemPositioned),
            )
        }
    }
}

@Composable
private fun EtaDashboardGuideOverlay(
    guideUiModel: EtaDashboardGuideUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_diagonal_line),
                modifier =
                    Modifier
                        .padding(end = 12.dp)
                        .padding(bottom = 8.dp),
                contentDescription = null,
            )
            Text(
                text = stringResource(id = guideUiModel.nudgeMessageId),
                style = OdyTheme.typography.pretendardMedium20.copy(color = OdyTheme.colors.octonary),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = guideUiModel.messageId),
                style = OdyTheme.typography.pretendardMedium20.copy(color = OdyTheme.colors.octonary),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.noRippleClickable(onClick),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = guideUiModel.buttonImageId),
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                Text(
                    text = stringResource(id = guideUiModel.buttonMessageId),
                    style = OdyTheme.typography.pretendardRegular16.copy(color = OdyTheme.colors.octonary),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 64.dp),
                )
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun EtaDashboardGuideLayoutPreview() {
    OdyTheme {
        val mateEtas =
            listOf(
                MateEtaUiModel(
                    "올리브",
                    EtaStatusUiModel.ArrivalSoon(10),
                    userId = 1L,
                    mateId = 1L,
                ),
                MateEtaUiModel(
                    "콜리",
                    EtaStatusUiModel.LateWarning(28),
                    userId = 2L,
                    mateId = 2L,
                ),
                MateEtaUiModel(
                    "해음",
                    EtaStatusUiModel.Arrived,
                    userId = 3L,
                    mateId = 3L,
                ),
                MateEtaUiModel(
                    "제리",
                    EtaStatusUiModel.Arrived,
                    userId = 4L,
                    mateId = 4L,
                ),
            )
        EtaDashboardGuideLayout(
            guideUiModel =
                EtaDashboardGuideUiModel(
                    mateEtas = mateEtas,
                    nudgeMessageId = R.string.eta_dashboard_guide_late_warning_nudge,
                    messageId = R.string.eta_dashboard_guide_before_meeting_time,
                    buttonMessageId = R.string.next_button,
                    buttonImageId = R.drawable.ic_arrow_right,
                ),
            onClick = { },
        )
    }
}
