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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
) {
    var hour by remember { mutableIntStateOf(time.hour) }
    var minute  by remember { mutableIntStateOf(time.minute) }

    LaunchedEffect(hour, minute) {
        val localTime = LocalTime.of(hour, minute)
        onTimeChanged(localTime)
    }

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
                value = hour,
                range = 0..23,
                onValueChange = { hour = it },
            )
            Text(
                text = stringResource(id = R.string.meeting_time_separator),
                style = OdyTheme.typography.pretendardBold28.copy(color = OdyTheme.colors.tertiary),
            )
            OdyNumberPicker(
                value = minute,
                range = 0..59,
                onValueChange = { minute = it },
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun MeetingTimeScreenPreview() {
    OdyTheme {
        MeetingTimeScreen(time = LocalTime.now(), onTimeChanged = {})
    }
}
