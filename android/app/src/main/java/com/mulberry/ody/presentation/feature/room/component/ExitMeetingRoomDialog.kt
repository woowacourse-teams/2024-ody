package com.mulberry.ody.presentation.feature.room.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mulberry.ody.R
import com.mulberry.ody.presentation.component.OdySadDialog
import com.mulberry.ody.presentation.theme.OdyTheme

@Composable
fun ExitMeetingRoomDialog(
    meetingName: String,
    onClickExit: () -> Unit,
    onClickCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OdySadDialog(
        title = {
            Text(
                text = meetingName,
                style = OdyTheme.typography.pretendardBold24.copy(color = OdyTheme.colors.secondary),
            )
        },
        subtitle = {
            Text(
                text = stringResource(id = R.string.exit_meeting_room_title),
                style = OdyTheme.typography.pretendardBold18.copy(color = OdyTheme.colors.quinary),
            )
        },
        onClickConfirm = onClickExit,
        onClickCancel = onClickCancel,
        confirmButtonText = stringResource(id = R.string.exit_meeting_room),
        modifier = modifier,
    )
}
