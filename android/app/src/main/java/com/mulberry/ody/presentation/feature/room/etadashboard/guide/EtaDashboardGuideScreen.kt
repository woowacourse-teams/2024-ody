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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mulberry.ody.R
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.feature.room.etadashboard.component.EtaDashboardItem
import com.mulberry.ody.presentation.feature.room.etadashboard.model.EtaStatusUiModel
import com.mulberry.ody.presentation.feature.room.etadashboard.model.MateEtaUiModel
import com.mulberry.ody.presentation.theme.Gray900Alpha50
import com.mulberry.ody.presentation.theme.OdyTheme
import com.tbuonomo.viewpagerdotsindicator.pxToDp

@Composable
fun EtaDashboardGuideScreen() {
    Scaffold(
        containerColor = OdyTheme.colors.primary,
        topBar = {
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
        },
    ) { innerPadding ->
        EtaDashboardGuideContent(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
private fun EtaDashboardGuideContent(modifier: Modifier = Modifier) {
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

    val overlayY = remember { mutableFloatStateOf(0f) }
    val boxMaxY = remember { mutableIntStateOf(0) }

    Box(
        modifier =
        modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                boxMaxY.intValue = coordinates.size.height
            },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                itemsIndexed(mateEtas, key = { _, item -> item.userId }) { index, mateEta ->
                    val topPadding = if (index == 0) 53.dp else 20.dp
                    val bottomPadding = if (index == mateEtas.size - 1) 53.dp else 20.dp
                    if (index == 1) {
                        Box {
                            EtaDashboardItem(
                                mateEta = mateEta,
                                modifier =
                                Modifier
                                    .padding(top = topPadding)
                                    .padding(bottom = bottomPadding),
                            )
                            Box(
                                modifier =
                                Modifier
                                    .matchParentSize()
                                    .background(Gray900Alpha50),
                            )
                            EtaDashboardItem(
                                mateEta = mateEta,
                                modifier =
                                Modifier
                                    .matchParentSize()
                                    .padding(horizontal = 18.dp)
                                    .padding(vertical = 10.dp)
                                    .background(
                                        color = OdyTheme.colors.primary,
                                        shape = RoundedCornerShape(20.dp),
                                    )
                                    .onGloballyPositioned { coordinates ->
                                        overlayY.floatValue = coordinates.positionInRoot().y
                                    },
                            )
                        }
                    } else {
                        Box {
                            EtaDashboardItem(
                                mateEta = mateEta,
                                modifier =
                                Modifier
                                    .padding(top = topPadding)
                                    .padding(bottom = bottomPadding),
                            )
                            Box(
                                modifier =
                                Modifier
                                    .matchParentSize()
                                    .background(Gray900Alpha50),
                            )
                        }
                    }
                }
            }
            Box(
                modifier =
                Modifier
                    .fillMaxSize()
                    .background(Gray900Alpha50),
            )
        }
        Box(
            modifier =
            Modifier
                .offset { IntOffset(0, overlayY.floatValue.toInt()) }
                .offset(y = (-20).dp)
                .fillMaxWidth()
                .height(
                    (boxMaxY.intValue - overlayY.floatValue)
                        .toInt()
                        .pxToDp(),
                ),
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
                    text = stringResource(id = R.string.eta_dashboard_guide_late_warning_nudge),
                    style = OdyTheme.typography.pretendardMedium20.copy(color = OdyTheme.colors.octonary),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(id = R.string.eta_dashboard_guide_before_meeting_time),
                    style = OdyTheme.typography.pretendardMedium20.copy(color = OdyTheme.colors.octonary),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                Text(
                    text = stringResource(id = R.string.next_button),
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
private fun EtaDashboardGuideScreenPreview() {
    OdyTheme {
        EtaDashboardGuideScreen()
    }
}
