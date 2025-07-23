package com.mulberry.ody.presentation.feature.creation.date

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.collectWhenStarted
import com.mulberry.ody.presentation.component.OdyTextField
import com.mulberry.ody.presentation.feature.creation.MeetingCreationViewModel
import com.mulberry.ody.presentation.theme.OdyTheme
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun MeetingDateScreen(
    showSnackBar: (String) -> Unit,
    viewModel: MeetingCreationViewModel,
) {
    val context = LocalContext.current

    MeetingDateContent()

    LaunchedEffect(Unit) {
        viewModel.invalidMeetingDateEvent.collect {
            showSnackBar(context.getString(R.string.meeting_date_date_guide))
        }
    }

    // TODO: 오늘 날짜 디폴트로 선택, 과거 날짜 선택 불가능
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeetingDateContent() {
    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis > System.currentTimeMillis()
            }
        }
    )
    val selectedDate = datePickerState.selectedDateMillis

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text =
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(color = OdyTheme.colors.secondary)
                ) {
                    append(stringResource(id = R.string.meeting_date_question_front))
                }
                withStyle(
                    style = SpanStyle(color = OdyTheme.colors.quinary)
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
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun MeetingDateContentPreview() {
    OdyTheme {
        MeetingDateContent()
    }
}
