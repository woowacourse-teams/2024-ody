package com.mulberry.ody.presentation.feature.room.etadashboard

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.NoRippleInteractionSource
import com.mulberry.ody.presentation.common.modifier.noRippleClickable
import com.mulberry.ody.presentation.component.OdyTooltip
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.feature.room.MeetingRoomViewModel
import com.mulberry.ody.presentation.feature.room.etadashboard.model.EtaStatusUiModel
import com.mulberry.ody.presentation.feature.room.etadashboard.model.MateEtaUiModel
import com.mulberry.ody.presentation.theme.Gray400
import com.mulberry.ody.presentation.theme.OdyTheme
import com.mulberry.ody.presentation.theme.White
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
        verticalArrangement = Arrangement.spacedBy(42.dp),
    ) {
        items(items = mateEtas, key = { it.userId }) { mateEta ->
            EtaDashboardItem(
                mateEta = mateEta,
                onClickNudge = onClickNudge,
            )
        }
    }
}

@Composable
private fun EtaDashboardItem(
    mateEta: MateEtaUiModel,
    onClickNudge: (MateEtaUiModel) -> Unit,
    modifier: Modifier = Modifier,
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
private fun EtaDashboardScreenPreview() {
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
                    userId = 2L,
                    mateId = 2L,
                ),
                MateEtaUiModel(
                    "올리브",
                    EtaStatusUiModel.ArrivalSoon(20),
                    userId = 3L,
                    mateId = 3L,
                ),
                MateEtaUiModel(
                    "올리브",
                    EtaStatusUiModel.Late(30),
                    userId = 4L,
                    mateId = 4L,
                ),
                MateEtaUiModel(
                    "올리브",
                    EtaStatusUiModel.LateWarning(20),
                    userId = 5L,
                    mateId = 5L,
                ),
            )
        EtaDashboardContent(
            mateEtas = mateEtas,
            onClickNudge = { },
        )
    }
}
