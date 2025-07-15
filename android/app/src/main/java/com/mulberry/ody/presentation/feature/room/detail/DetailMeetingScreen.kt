package com.mulberry.ody.presentation.feature.room.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.NoRippleInteractionSource
import com.mulberry.ody.presentation.common.modifier.noRippleClickable
import com.mulberry.ody.presentation.component.OdyLoading
import com.mulberry.ody.presentation.component.OdyTooltip
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.feature.room.MeetingRoomViewModel
import com.mulberry.ody.presentation.feature.room.component.ExitMeetingRoomDialog
import com.mulberry.ody.presentation.feature.room.detail.model.DetailMeetingUiModel
import com.mulberry.ody.presentation.feature.room.detail.model.InviteCodeCopyInfo
import com.mulberry.ody.presentation.feature.room.detail.model.MateUiModel
import com.mulberry.ody.presentation.theme.Cream
import com.mulberry.ody.presentation.theme.Gray300
import com.mulberry.ody.presentation.theme.Gray350
import com.mulberry.ody.presentation.theme.Gray500
import com.mulberry.ody.presentation.theme.OdyTheme

@Composable
fun DetailMeetingScreen(
    onBack: () -> Unit,
    onCopyInviteCode: (InviteCodeCopyInfo) -> Unit,
    viewModel: MeetingRoomViewModel,
) {
    val mates by viewModel.mates.collectAsStateWithLifecycle()
    val detailMeeting by viewModel.meeting.collectAsStateWithLifecycle()
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = OdyTheme.colors.primary,
        topBar = {
            OdyTopAppBar(
                title = detailMeeting.name,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            tint = Color.Unspecified,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showExitDialog = true },
                        interactionSource = NoRippleInteractionSource,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_exit),
                            tint = Color.Unspecified,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        DetailMeetingContent(
            mates = mates,
            detailMeeting = detailMeeting,
            navigateToEta = { viewModel.navigateToEtaDashboard() },
            navigateToLog = { viewModel.navigateToNotificationLog() },
            onCopyInviteCode = {
                val inviteCodeCopyInfo = InviteCodeCopyInfo(detailMeeting.name, detailMeeting.inviteCode)
                onCopyInviteCode(inviteCodeCopyInfo)
            },
            modifier = Modifier.padding(innerPadding),
        )
        if (showExitDialog) {
            ExitMeetingRoomDialog(
                meetingName = detailMeeting.name,
                onClickExit = {
                    viewModel.exitMeetingRoom()
                    onBack()
                },
                onClickCancel = {
                    showExitDialog = false
                },
            )
        }
    }
}

@Composable
private fun DetailMeetingContent(
    mates: List<MateUiModel>,
    detailMeeting: DetailMeetingUiModel,
    navigateToEta: () -> Unit,
    navigateToLog: () -> Unit,
    onCopyInviteCode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MatesCard(
            mates = mates,
            onCopyInviteCode = onCopyInviteCode,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        DetailMeetingInformation(
            detailMeeting = detailMeeting,
            modifier = Modifier.padding(top = 28.dp),
        )
        Spacer(modifier = modifier.weight(1f))
        DetailMeetingNavigation(
            navigateToEta = navigateToEta,
            navigateToLog = navigateToLog,
            modifier = Modifier.padding(bottom = 24.dp),
        )
    }
}

@Composable
private fun MatesCard(
    mates: List<MateUiModel>,
    onCopyInviteCode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape =
            RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 30.dp,
                bottomEnd = 30.dp,
            ),
        colors = CardDefaults.cardColors(containerColor = OdyTheme.colors.primary),
        border = BorderStroke(width = 1.dp, color = Cream),
        modifier =
            modifier
                .fillMaxWidth()
                .offset(y = (-1).dp)
                .height(136.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        LazyRow(
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
        ) {
            items(count = mates.size) { index ->
                MateItem(mates[index])
                if (index == mates.lastIndex) {
                    Spacer(modifier = Modifier.width(36.dp))
                    InviteCodeCopyItem(onClick = onCopyInviteCode)
                }
            }
        }
    }
}

@Composable
private fun MateItem(
    mate: MateUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .wrapContentWidth()
                .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
    ) {
        SubcomposeAsyncImage(
            model =
                ImageRequest.Builder(context = LocalContext.current)
                    .data(mate.imageUrl)
                    .build(),
            loading = {
                OdyLoading(modifier = Modifier.wrapContentSize())
            },
            error = {
                Box(modifier = Modifier.background(Gray350))
            },
            contentDescription = null,
            modifier =
                Modifier
                    .size(58.dp)
                    .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = mate.nickname,
            style = OdyTheme.typography.pretendardRegular14.copy(color = OdyTheme.colors.quinary),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(58.dp),
        )
    }
}

@Composable
private fun InviteCodeCopyItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .wrapContentWidth()
                .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(id = R.string.detail_meeting_invite_code_guide),
            style = OdyTheme.typography.pretendardRegular14.copy(color = OdyTheme.colors.quinary),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Gray300),
            shape = RoundedCornerShape(15.dp),
            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 14.dp),
        ) {
            Text(
                text = stringResource(id = R.string.detail_meeting_invite_code_copy),
                style = OdyTheme.typography.pretendardRegular14.copy(color = Gray500),
            )
        }
    }
}

@Composable
private fun DetailMeetingInformation(
    detailMeeting: DetailMeetingUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 28.dp),
    ) {
        Text(
            text = stringResource(id = R.string.detail_meeting_time),
            style = OdyTheme.typography.pretendardBold18.copy(color = OdyTheme.colors.secondary),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = detailMeeting.dateTime,
            style = OdyTheme.typography.pretendardRegular16.copy(color = OdyTheme.colors.quinary),
        )
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = stringResource(id = R.string.detail_meeting_address),
            style = OdyTheme.typography.pretendardBold18.copy(color = OdyTheme.colors.secondary),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = detailMeeting.destinationAddress,
            style = OdyTheme.typography.pretendardRegular16.copy(color = OdyTheme.colors.quinary),
        )
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = stringResource(id = R.string.detail_meeting_departure_address),
            style = OdyTheme.typography.pretendardBold18.copy(color = OdyTheme.colors.secondary),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = detailMeeting.departureAddress,
            style = OdyTheme.typography.pretendardRegular16.copy(color = OdyTheme.colors.quinary),
        )
        Spacer(modifier = Modifier.height(28.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = R.string.detail_meeting_departure_time),
                style = OdyTheme.typography.pretendardBold18.copy(color = OdyTheme.colors.secondary),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.wrapContentSize()) {
                var showTooltip by rememberSaveable { mutableStateOf(false) }
                var tooltipSize by remember { mutableStateOf(IntSize.Zero) }
                var iconSize by remember { mutableStateOf(IntSize.Zero) }

                Icon(
                    painter = painterResource(id = R.drawable.ic_information),
                    contentDescription = null,
                    tint = OdyTheme.colors.senary,
                    modifier =
                        Modifier
                            .noRippleClickable { showTooltip = !showTooltip }
                            .onGloballyPositioned { coordinates ->
                                iconSize = coordinates.size
                            },
                )

                if (showTooltip) {
                    OdyTooltip(
                        title = stringResource(id = R.string.detail_meeting_departure_time_guide),
                        offset = IntOffset(iconSize.width, -tooltipSize.height),
                        onDismissRequest = { showTooltip = !showTooltip },
                        shape =
                            RoundedCornerShape(
                                topStart = 20.dp,
                                topEnd = 20.dp,
                                bottomEnd = 20.dp,
                            ),
                        modifier =
                            Modifier.onGloballyPositioned { coordinates ->
                                tooltipSize = coordinates.size
                            },
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text =
                stringResource(
                    id = R.string.detail_meeting_departure_time_content,
                    detailMeeting.departureTime,
                    detailMeeting.durationTime,
                ),
            style = OdyTheme.typography.pretendardRegular16.copy(color = OdyTheme.colors.quinary),
        )
    }
}

@Composable
private fun DetailMeetingNavigation(
    navigateToEta: () -> Unit,
    navigateToLog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showNavigationButton by rememberSaveable { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
        ) {
            AnimatedVisibility(
                visible = showNavigationButton,
                enter =
                    fadeIn(tween(durationMillis = 300)) +
                        expandHorizontally(
                            tween(
                                durationMillis = 300,
                            ),
                        ),
                exit =
                    fadeOut(tween(durationMillis = 300)) +
                        shrinkHorizontally(
                            tween(
                                durationMillis = 300,
                            ),
                        ),
            ) {
                Button(
                    onClick = navigateToEta,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(15.dp),
                    border = BorderStroke(width = 1.dp, color = OdyTheme.colors.secondary),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                    interactionSource = NoRippleInteractionSource,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_ody),
                        contentDescription = null,
                        tint = OdyTheme.colors.secondary,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(id = R.string.ody_button),
                        style = OdyTheme.typography.pretendardBold16.copy(color = OdyTheme.colors.secondary),
                    )
                }
            }
        }
        Button(
            onClick = { showNavigationButton = !showNavigationButton },
            colors = ButtonDefaults.buttonColors(containerColor = OdyTheme.colors.secondary),
            shape = RoundedCornerShape(10.dp),
            modifier =
                Modifier
                    .padding(horizontal = 18.dp)
                    .size(55.dp),
            contentPadding = PaddingValues(all = 6.dp),
            interactionSource = NoRippleInteractionSource,
        ) {
            Image(
                painter = painterResource(id = if (showNavigationButton) R.drawable.ic_cancel else R.drawable.ic_ody),
                contentDescription = null,
            )
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Start,
        ) {
            AnimatedVisibility(
                visible = showNavigationButton,
                enter =
                    fadeIn(tween(durationMillis = 300)) +
                        expandHorizontally(
                            tween(
                                durationMillis = 300,
                            ),
                        ),
                exit =
                    fadeOut(tween(durationMillis = 300)) +
                        shrinkHorizontally(
                            tween(
                                durationMillis = 300,
                            ),
                        ),
            ) {
                Button(
                    onClick = navigateToLog,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(15.dp),
                    border = BorderStroke(width = 1.dp, color = OdyTheme.colors.secondary),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                    interactionSource = NoRippleInteractionSource,
                ) {
                    Text(
                        text = stringResource(id = R.string.detail_meeting_log),
                        style = OdyTheme.typography.pretendardBold16.copy(color = OdyTheme.colors.secondary),
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun DetailMeetingContentPreview() {
    OdyTheme {
        val mates =
            listOf(
                MateUiModel(
                    "올리브1",
                    "https://thumbnail.10x10.co.kr/webimage/image/basic600/168/B001689583.jpg?cmd=thumb&w=400&h=400&fit=true&ws=false",
                ),
                MateUiModel(
                    "올리브2",
                    "https://thumbnail.10x10.co.kr/webimage/image/basic600/168/B001689583.jpg?cmd=thumb&w=400&h=400&fit=true&ws=false",
                ),
                MateUiModel(
                    "올리브3",
                    "https://thumbnail.10x10.co.kr/webimage/image/basic600/168/B001689583.jpg?cmd=thumb&w=400&h=400&fit=true&ws=false",
                ),
                MateUiModel(
                    "올리브4",
                    "https://thumbnail.10x10.co.kr/webimage/image/basic600/168/B001689583.jpg?cmd=thumb&w=400&h=400&fit=true&ws=false",
                ),
            )
        val detailMeeting =
            DetailMeetingUiModel(
                id = 1L,
                name = "성수에서 감자탕 먹기",
                destinationAddress = "서울특별시 강남구 테헤란로 411 (성담빌딩)",
                departureAddress = "서울특별시 송파구 올림픽로 35다길 (한국루터회관)",
                dateTime = "2024년 7월 11일 오후 3시",
                departureTime = "오후 2시 30분",
                durationTime = "20분",
                inviteCode = "123456",
            )
        DetailMeetingContent(
            detailMeeting = detailMeeting,
            mates = mates,
            navigateToEta = { },
            navigateToLog = { },
            onCopyInviteCode = { },
        )
    }
}
