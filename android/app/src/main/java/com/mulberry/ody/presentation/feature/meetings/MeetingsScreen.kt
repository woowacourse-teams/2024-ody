package com.mulberry.ody.presentation.feature.meetings

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.OnLifecycleEvent
import com.mulberry.ody.presentation.common.modifier.noRippleClickable
import com.mulberry.ody.presentation.component.OdyButton
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.feature.meetings.model.MeetingUiModel
import com.mulberry.ody.presentation.feature.meetings.model.MeetingsUiState
import com.mulberry.ody.presentation.theme.Gray350
import com.mulberry.ody.presentation.theme.Gray400
import com.mulberry.ody.presentation.theme.OdyTheme
import com.mulberry.ody.presentation.theme.White
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun MeetingsScreen(
    viewModel: MeetingsViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val onShowSnackbar: (String) -> Unit = { message ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
    val meetingsUiState by viewModel.meetingsUiState.collectAsStateWithLifecycle()

    CheckAndLaunchPermission(onShowSnackbar)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = OdyTheme.colors.primary,
        topBar = {
            OdyTopAppBar(
                title = stringResource(id = R.string.app_name),
                actions = {
                    Icon(
                        painter = painterResource(R.drawable.ic_setting),
                        modifier = Modifier
                            .noRippleClickable { viewModel.onClickSetting() }
                            .padding(end = 18.dp),
                        tint = OdyTheme.colors.tertiary,
                        contentDescription = null,
                    )
                },
            )
        },
    ) { innerPadding ->
        when (val state = meetingsUiState) {
            MeetingsUiState.Empty -> EmptyMeetingsContent(modifier = Modifier.padding(innerPadding))
            is MeetingsUiState.Meetings -> MeetingsContent(
                meetings = state.content,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }

    OnLifecycleEvent { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            viewModel.fetchMeetings()
        }
    }
}

@Composable
private fun CheckAndLaunchPermission(
    onShowSnackbar: (String) -> Unit
) {
    val context = LocalContext.current
    val backgroundLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            onShowSnackbar(context.getString(R.string.meetings_background_location_permission_required))
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.filter { !it.value }.isEmpty()
        if (isGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                backgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        } else {
            onShowSnackbar(context.getString(R.string.meetings_location_permission_required))
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        if (!isGranted) {
            onShowSnackbar(context.getString(R.string.meetings_notification_permission_required))
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }
}

@Composable
private fun EmptyMeetingsContent(modifier: Modifier = Modifier) {

}

@Composable
private fun MeetingsContent(
    meetings: List<MeetingUiModel>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 24.dp, start = 18.dp, end = 18.dp, bottom = 32.dp),
    ) {
        items(meetings) {
            MeetingItem(it)
        }
    }
}

@Composable
private fun MeetingItem(
    meeting: MeetingUiModel,
    initialIsFolded: Boolean = false,
    onClickMeeting: (MeetingUiModel) -> Unit = {},
    onClickOdy: () -> Unit = {},
) {
    val context = LocalContext.current
    var isFolded by rememberSaveable { mutableStateOf(initialIsFolded) }

    Card(
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = OdyTheme.colors.octonary),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .noRippleClickable { onClickMeeting(meeting) },
    ) {
        Column(
            modifier =
            Modifier
                .padding(horizontal = 22.dp)
                .padding(vertical = 12.dp)
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
                    modifier = Modifier
                        .padding(10.dp)
                        .noRippleClickable { isFolded = !isFolded },
                )
            }
            Text(
                text = meeting.dateTimeMessage(context),
                style = OdyTheme.typography.pretendardMedium16.copy(color = OdyTheme.colors.quinary),
            )
            if (isFolded) {
                Row(
                    modifier = Modifier
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(
                            id = R.string.meetings_eta_form,
                            meeting.durationMinutes
                        ),
                        style = OdyTheme.typography.pretendardBold20.copy(color = OdyTheme.colors.quinary),
                    )
                    Text(
                        text = stringResource(id = R.string.meetings_traffic_guide),
                        style = OdyTheme.typography.pretendardRegular14.copy(color = Gray400),
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                OdyButton(
                    isEnabled = meeting.isAccessible(),
                    onClick = onClickOdy,
                )
            }
        }
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
            MeetingItem(meeting)
            Spacer(Modifier.height(16.dp))
            MeetingItem(meeting, initialIsFolded = true)
        }
    }
}
