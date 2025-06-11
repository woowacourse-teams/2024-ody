package com.mulberry.ody.presentation.feature.room.etadashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulberry.ody.R
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.feature.room.MeetingRoomViewModel
import com.mulberry.ody.presentation.feature.room.etadashboard.component.EtaDashboardItem
import com.mulberry.ody.presentation.feature.room.etadashboard.guide.EtaDashboardFirstGuideScreen
import com.mulberry.ody.presentation.feature.room.etadashboard.guide.EtaDashboardGuideScreen
import com.mulberry.ody.presentation.feature.room.etadashboard.guide.EtaDashboardSecondGuideScreen
import com.mulberry.ody.presentation.feature.room.etadashboard.model.EtaStatusUiModel
import com.mulberry.ody.presentation.feature.room.etadashboard.model.MateEtaUiModel
import com.mulberry.ody.presentation.theme.OdyTheme
import kotlinx.coroutines.launch

@Composable
fun EtaDashboardScreen(
    onClickBack: () -> Unit,
    viewModel: MeetingRoomViewModel,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val showSnackbar: (String) -> Unit = { message ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
    val meeting by viewModel.meeting.collectAsStateWithLifecycle()
    val mateEtas by viewModel.mateEtaUiModels.collectAsStateWithLifecycle()
    val graphicsLayer = rememberGraphicsLayer()

    val showGuide by viewModel.isFirstSeenEtaDashboard.collectAsStateWithLifecycle()
    if (showGuide) {
        EtaDashboardGuideScreen(onDismiss = { viewModel.updateEtaDashboardSeen() })
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = OdyTheme.colors.primary,
            topBar = {
                OdyTopAppBar(
                    title = meeting.name,
                    navigationIcon = {
                        IconButton(onClick = onClickBack) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_back),
                                tint = Color.Unspecified,
                                contentDescription = null,
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                    viewModel.shareEtaDashboard(
                                        title = context.getString(R.string.eta_dashboard_share_description),
                                        buttonTitle = context.getString(R.string.eta_dashboard_share_button),
                                        bitmap = bitmap,
                                    )
                                }
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_share),
                                tint = Color.Unspecified,
                                contentDescription = null,
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            EtaDashboardContent(
                mateEtas = mateEtas,
                onClickNudge = { viewModel.nudgeMate(it.userId, it.mateId) },
                modifier =
                Modifier
                    .padding(innerPadding)
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    }
                    .background(OdyTheme.colors.primary),
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.nudgeSuccessMate.collect { nickname ->
            showSnackbar(context.getString(R.string.nudge_success, nickname))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.nudgeFailMate.collect { nickname ->
            showSnackbar(context.getString(R.string.nudge_failure, nickname))
        }
    }
}

@Composable
private fun EtaDashboardContent(
    mateEtas: List<MateEtaUiModel>,
    onClickNudge: (MateEtaUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp),
    ) {
        items(items = mateEtas, key = { it.mateId }) { mateEta ->
            EtaDashboardItem(
                mateEta = mateEta,
                onClickNudge = onClickNudge,
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun EtaDashboardContentPreview() {
    OdyTheme {
        val mateEtas =
            listOf(
                MateEtaUiModel(
                    "올리브",
                    EtaStatusUiModel.Missing,
                    userId = 1L,
                    mateId = 1L,
                ),
                MateEtaUiModel(
                    "올리브",
                    EtaStatusUiModel.Arrived,
                    userId = 1L,
                    mateId = 2L,
                ),
                MateEtaUiModel(
                    "올리브",
                    EtaStatusUiModel.ArrivalSoon(20),
                    userId = 1L,
                    mateId = 3L,
                ),
                MateEtaUiModel(
                    "올리브",
                    EtaStatusUiModel.Late(30),
                    userId = 1L,
                    mateId = 4L,
                ),
                MateEtaUiModel(
                    "올리브",
                    EtaStatusUiModel.LateWarning(20),
                    userId = 1L,
                    mateId = 5L,
                ),
            )
        EtaDashboardContent(
            mateEtas = mateEtas,
            onClickNudge = { },
        )
    }
}
