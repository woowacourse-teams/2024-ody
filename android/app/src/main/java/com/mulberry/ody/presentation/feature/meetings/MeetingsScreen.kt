package com.mulberry.ody.presentation.feature.meetings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.ErrorSnackbarHandler
import com.mulberry.ody.presentation.common.NoRippleInteractionSource
import com.mulberry.ody.presentation.common.modifier.noRippleClickable
import com.mulberry.ody.presentation.component.OdyButton
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.feature.meetings.component.BackPressed
import com.mulberry.ody.presentation.feature.meetings.component.MeetingsLaunchPermission
import com.mulberry.ody.presentation.feature.meetings.model.MeetingUiModel
import com.mulberry.ody.presentation.feature.meetings.model.MeetingsUiState
import com.mulberry.ody.presentation.theme.Gray400
import com.mulberry.ody.presentation.theme.OdyTheme
import com.mulberry.ody.presentation.theme.White
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun MeetingsScreen(
    viewModel: MeetingsViewModel = hiltViewModel(),
    navigate: (MeetingsNavigateAction) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val showSnackbar: (Int) -> Unit = { id ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(context.getString(id))
        }
    }
    val meetingsUiState by viewModel.meetingsUiState.collectAsStateWithLifecycle()
    var isExpandedFloatingActionButton by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = OdyTheme.colors.primary,
        floatingActionButton = {
            MeetingsFloatingActionButton(
                isExpanded = isExpandedFloatingActionButton,
                onExpand = { isExpandedFloatingActionButton = !isExpandedFloatingActionButton },
                onCreate = { viewModel.onCreateMeeting() },
                onJoin = { viewModel.onJoinMeeting() },
            )
        },
        topBar = {
            OdyTopAppBar(
                title = stringResource(id = R.string.app_name),
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateToSetting() },
                        interactionSource = NoRippleInteractionSource,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_setting),
                            tint = OdyTheme.colors.tertiary,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (val state = meetingsUiState) {
            MeetingsUiState.Loading -> MeetingsLoading()
            MeetingsUiState.Empty -> MeetingsEmpty(modifier = Modifier.padding(innerPadding))
            is MeetingsUiState.Meetings ->
                MeetingsContent(
                    meetings = state.content,
                    onClickMeeting = { viewModel.navigateToNotificationLog(it.id) },
                    onClickOdy = { viewModel.navigateToEta(it) },
                    modifier = Modifier.padding(innerPadding),
                )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.inaccessibleEtaEvent.collect {
            showSnackbar(R.string.inaccessible_eta_guide)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.navigateAction.collect {
            navigate(it)
            isExpandedFloatingActionButton = false
        }
    }
    LifecycleEventEffect(event = Lifecycle.Event.ON_START) {
        viewModel.fetchMeetings()
    }
    ErrorSnackbarHandler(viewModel)
    MeetingsLaunchPermission(showSnackbar)
    BackPressed()
}

@Composable
private fun MeetingsFloatingActionButton(
    isExpanded: Boolean = false,
    onExpand: () -> Unit,
    onCreate: () -> Unit,
    onJoin: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier =
            Modifier
                .padding(bottom = 16.dp)
                .padding(end = 16.dp),
    ) {
        if (isExpanded) {
            Column(
                modifier =
                    Modifier
                        .width(164.dp)
                        .wrapContentHeight()
                        .background(
                            color = OdyTheme.colors.primaryVariant,
                            shape = RoundedCornerShape(10.dp),
                        )
                        .padding(all = 18.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.meetings_create_meeting),
                    style = OdyTheme.typography.pretendardMedium18.copy(color = OdyTheme.colors.secondaryVariant),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                            .noRippleClickable { onCreate() },
                )
                Text(
                    text = stringResource(id = R.string.meetings_join_meeting),
                    style = OdyTheme.typography.pretendardMedium18.copy(color = OdyTheme.colors.secondaryVariant),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                            .noRippleClickable { onJoin() },
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        FloatingActionButton(
            onClick = onExpand,
            shape = CircleShape,
            containerColor = OdyTheme.colors.primaryVariant,
        ) {
            val iconId = if (isExpanded) R.drawable.ic_cancel else R.drawable.ic_plus
            Icon(
                painter = painterResource(id = iconId),
                tint = OdyTheme.colors.secondaryVariant,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun MeetingsLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MeetingsEmpty(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_sad_ody),
            contentDescription = null,
            modifier = Modifier.padding(bottom = 36.dp),
        )
        Text(
            text = stringResource(id = R.string.meetings_empty),
            style = OdyTheme.typography.pretendardBold28.copy(OdyTheme.colors.quinary),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MeetingsContent(
    meetings: List<MeetingUiModel>,
    onClickMeeting: (MeetingUiModel) -> Unit,
    onClickOdy: (MeetingUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 24.dp, start = 18.dp, end = 18.dp, bottom = 32.dp),
    ) {
        items(items = meetings, key = { it.id }) {
            MeetingItem(
                meeting = it,
                onClickMeeting = onClickMeeting,
                onClickOdy = onClickOdy,
            )
        }
    }
}

@Composable
private fun MeetingItem(
    meeting: MeetingUiModel,
    onClickMeeting: (MeetingUiModel) -> Unit,
    onClickOdy: (MeetingUiModel) -> Unit,
    initialIsFolded: Boolean = false,
) {
    val context = LocalContext.current
    var isFolded by rememberSaveable { mutableStateOf(initialIsFolded) }

    Card(
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = OdyTheme.colors.octonary),
        border = BorderStroke(width = 1.dp, color = White),
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .noRippleClickable { onClickMeeting(meeting) },
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 22.dp)
                    .padding(vertical = 12.dp),
        ) {
            Row(modifier = Modifier.padding(top = 6.dp)) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = meeting.name,
                    style = OdyTheme.typography.pretendardBold24.copy(color = OdyTheme.colors.quinary),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                )
                Icon(
                    painter = painterResource(id = if (isFolded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down),
                    tint = OdyTheme.colors.secondary,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .padding(10.dp)
                            .noRippleClickable { isFolded = !isFolded },
                )
            }
            Text(
                text = meeting.dateTimeMessage(context),
                style = OdyTheme.typography.pretendardMedium16.copy(color = OdyTheme.colors.quinary),
            )
            AnimatedVisibility(
                visible = isFolded,
                enter = fadeIn(tween(durationMillis = 300)) + expandVertically(tween(durationMillis = 300)),
                exit = fadeOut(tween(durationMillis = 300)) + shrinkVertically(tween(durationMillis = 300)),
            ) {
                Column {
                    Row(
                        modifier =
                            Modifier
                                .padding(top = 6.dp)
                                .padding(bottom = 4.dp),
                    ) {
                        Text(
                            text = meeting.originAddress,
                            style = OdyTheme.typography.pretendardRegular14.copy(color = OdyTheme.colors.secondary),
                        )
                        Text(
                            text = stringResource(id = R.string.meetings_arrival_postfix),
                            style = OdyTheme.typography.pretendardRegular14.copy(color = OdyTheme.colors.quarternary),
                        )
                    }
                    Row {
                        Text(
                            text = meeting.targetAddress,
                            style = OdyTheme.typography.pretendardRegular14.copy(color = OdyTheme.colors.secondary),
                        )
                        Text(
                            text = stringResource(id = R.string.meetings_departure_postfix),
                            style = OdyTheme.typography.pretendardRegular14.copy(color = OdyTheme.colors.quarternary),
                        )
                    }
                }
            }
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text =
                            stringResource(
                                id = R.string.meetings_eta_form,
                                meeting.durationMinutes,
                            ),
                        style = OdyTheme.typography.pretendardBold20.copy(color = OdyTheme.colors.quinary),
                    )
                    Text(
                        text = stringResource(id = R.string.meetings_traffic_guide),
                        style = OdyTheme.typography.pretendardRegular12.copy(color = Gray400),
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                OdyButton(
                    isEnabled = !meeting.isAccessible(),
                    onClick = { onClickOdy(meeting) },
                )
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun EmptyMeetingsContentPreview() {
    OdyTheme {
        MeetingsEmpty()
    }
}

@Composable
@Preview(showSystemUi = true)
private fun MeetingItemPreview() {
    val meeting =
        MeetingUiModel(
            id = 1L,
            name = "올리브와 저녁 마라탕",
            datetime = LocalDateTime.now().plusMinutes(20),
            originAddress = "서울 강남구 테헤란로 411",
            targetAddress = "서울특별시 송파구 올림픽로35다길 42",
            durationMinutes = "1시간 10분",
        )
    OdyTheme {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            MeetingItem(meeting, {}, {})
            Spacer(Modifier.height(16.dp))
            MeetingItem(meeting, {}, {}, initialIsFolded = true)
        }
    }
}
