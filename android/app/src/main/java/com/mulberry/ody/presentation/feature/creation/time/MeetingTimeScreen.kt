package com.mulberry.ody.presentation.feature.creation.time

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulberry.ody.R
import com.mulberry.ody.presentation.component.OdyNumberPicker
import com.mulberry.ody.presentation.feature.creation.MeetingCreationViewModel
import com.mulberry.ody.presentation.theme.OdyTheme
import java.time.LocalTime

@Composable
fun MeetingTimeScreen(
    time: LocalTime,
    onTimeChanged: (LocalTime) -> Unit,
    showSnackBar: (String) -> Unit,
    viewModel: MeetingCreationViewModel,
) {
    val context = LocalContext.current

    MeetingTimeContent(time = time, onTimeChanged = onTimeChanged)

    LaunchedEffect(Unit) {
        viewModel.invalidMeetingTimeEvent.collect {
            showSnackBar(context.getString(R.string.invalid_meeting_time))
        }
    }
}

@Composable
private fun MeetingTimeContent(
    time: LocalTime,
    onTimeChanged: (LocalTime) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text =
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(color = OdyTheme.colors.secondary),
                    ) {
                        append(stringResource(id = R.string.meeting_time_question_front))
                    }
                    withStyle(
                        style = SpanStyle(color = OdyTheme.colors.quinary),
                    ) {
                        append(stringResource(id = R.string.meeting_time_question_back))
                    }
                },
            style = OdyTheme.typography.pretendardBold24,
            modifier = Modifier.padding(top = 52.dp, bottom = 32.dp),
        )
        Spacer(modifier = Modifier.height(74.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            OdyNumberPicker(
                value = 0,
                range = 0..24,
                onValueChange = {},
            )
            Text(
                text = stringResource(id = R.string.meeting_time_separator),
                style = OdyTheme.typography.pretendardBold28.copy(color = OdyTheme.colors.tertiary),
            )
            OdyNumberPicker(
                value = 0,
                range = 0..59,
                onValueChange = {},
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun MeetingTimeContentPreview() {
    OdyTheme {
        MeetingTimeContent(time = LocalTime.now(), onTimeChanged = {})
    }
}
