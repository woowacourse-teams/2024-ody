package com.mulberry.ody.presentation.feature.room.log

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.mulberry.ody.R
import com.mulberry.ody.domain.model.NotificationLogType
import com.mulberry.ody.presentation.common.NoRippleInteractionSource
import com.mulberry.ody.presentation.component.OdyLoading
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.feature.room.MeetingRoomViewModel
import com.mulberry.ody.presentation.feature.room.component.ExitMeetingRoomDialog
import com.mulberry.ody.presentation.feature.room.log.model.NotificationLogUiModel
import com.mulberry.ody.presentation.theme.Cream
import com.mulberry.ody.presentation.theme.Gray350
import com.mulberry.ody.presentation.theme.OdyTheme

@Composable
fun NotificationLogScreen(
    onBack: () -> Unit,
    viewModel: MeetingRoomViewModel,
) {
    val detailMeeting by viewModel.meeting.collectAsStateWithLifecycle()
    val notificationLogs by viewModel.notificationLogs.collectAsStateWithLifecycle()
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
        NotificationLogContent(
            notificationLogs = notificationLogs,
            modifier = Modifier.padding(innerPadding)
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
                }
            )
        }
    }
}

@Composable
private fun NotificationLogContent(
    notificationLogs: List<NotificationLogUiModel>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(vertical = 24.dp, horizontal = 20.dp),
    ) {
        items(items = notificationLogs) { notificationLog ->
            NotificationLogItem(
                notificationLog = notificationLog
            )
        }
    }
}

@Composable
private fun NotificationLogItem(
    notificationLog: NotificationLogUiModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SubcomposeAsyncImage(
            model =
            ImageRequest.Builder(context = context)
                .data(notificationLog.imageUrl)
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
                .size(44.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = OdyTheme.colors.quinary)) {
                        append(notificationLog.nickname)
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Medium, color = OdyTheme.colors.quinary)) {
                        append(notificationLog.type.getMessage(context))
                    }
                },
                style = OdyTheme.typography.pretendardMedium18,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = notificationLog.created,
                style = OdyTheme.typography.pretendardRegular14.copy(color = OdyTheme.colors.senary),
            )
        }
    }
}

@Composable
@Preview
private fun NotificationLogContentPreview() {
    val notificationLogs = listOf(
        NotificationLogUiModel(
            type = NotificationLogType.ENTRY,
            nickname = "올리브올리브올리브올리브올리브",
            created = "2024-08-16 18:35",
            imageUrl = "",
        ),
        NotificationLogUiModel(
            type = NotificationLogType.ENTRY,
            nickname = "올리브",
            created = "2024-08-16 20:12",
            imageUrl = "",
        ),
        NotificationLogUiModel(
            type = NotificationLogType.DEPARTURE_REMINDER,
            nickname = "해음",
            created = "2024-08-18 13:01",
            imageUrl = "",
        ),
    )
    OdyTheme {
        NotificationLogContent(
            notificationLogs = notificationLogs,
            modifier = Modifier.background(Cream),
        )
    }
}
