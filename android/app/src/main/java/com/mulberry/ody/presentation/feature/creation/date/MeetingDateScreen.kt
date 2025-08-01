package com.mulberry.ody.presentation.feature.creation.date

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulberry.ody.R
import com.mulberry.ody.domain.common.millisToLocalDate
import com.mulberry.ody.domain.common.toMilliSeconds
import com.mulberry.ody.presentation.common.toMessage
import com.mulberry.ody.presentation.theme.OdyTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingDateScreen(
    date: LocalDate,
    onDateChanged: (LocalDate) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        keyboardController?.hide()
    }

    val datePickerState =
        rememberDatePickerState(
            initialDisplayMode = DisplayMode.Picker,
            initialSelectedDateMillis = date.toMilliSeconds("UTC"),
            selectableDates =
                object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val nowMillis = LocalDate.now().toMilliSeconds("UTC")
                        return utcTimeMillis >= nowMillis
                    }
                },
        )

    MeetingDateContent(
        datePickerState = datePickerState,
        date = date,
    )

    LaunchedEffect(datePickerState.selectedDateMillis) {
        val selectedDate = datePickerState.selectedDateMillis ?: return@LaunchedEffect
        val localDate = selectedDate.millisToLocalDate()
        if (localDate != date) {
            onDateChanged(localDate)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeetingDateContent(
    datePickerState: DatePickerState,
    date: LocalDate,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text =
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(color = OdyTheme.colors.secondary),
                    ) {
                        append(stringResource(id = R.string.meeting_date_question_front))
                    }
                    withStyle(
                        style = SpanStyle(color = OdyTheme.colors.quinary),
                    ) {
                        append(stringResource(id = R.string.meeting_date_question_back))
                    }
                },
            style = OdyTheme.typography.pretendardBold24,
            modifier = Modifier.padding(top = 52.dp, bottom = 32.dp),
        )
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
            title = null,
            headline = {
                Text(
                    text = date.toMessage(),
                    style = OdyTheme.typography.pretendardBold24.copy(color = OdyTheme.colors.quinary),
                    modifier = Modifier.padding(all = 12.dp).padding(start = 12.dp),
                )
            },
            colors = DatePickerDefaults.colors(containerColor = OdyTheme.colors.primary),
            modifier = Modifier.padding(horizontal = 20.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
private fun MeetingDateContentPreview() {
    OdyTheme {
        MeetingDateContent(
            datePickerState = rememberDatePickerState(),
            date = LocalDate.now(),
        )
    }
}
