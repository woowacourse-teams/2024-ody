package com.mulberry.ody.presentation.feature.creation.name

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulberry.ody.R
import com.mulberry.ody.presentation.component.OdyTextField
import com.mulberry.ody.presentation.feature.creation.MeetingCreationViewModel
import com.mulberry.ody.presentation.theme.OdyTheme

@Composable
fun MeetingNameScreen(
    name: String,
    onNameChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text =
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(color = OdyTheme.colors.quinary)
                ) {
                    append(stringResource(id = R.string.meeting_name_question_front))
                }
                withStyle(
                    style = SpanStyle(color = OdyTheme.colors.secondary)
                ) {
                    append(stringResource(id = R.string.meeting_name_question_middle))
                }
                withStyle(
                    style = SpanStyle(color = OdyTheme.colors.quinary)
                ) {
                    append(stringResource(id = R.string.meeting_name_question_back))
                }
            },
            style = OdyTheme.typography.pretendardBold24,
            modifier = Modifier.padding(top = 52.dp, bottom = 32.dp),
        )
        OdyTextField(
            value = name,
            onValueChange = onNameChanged,
            placeholder = stringResource(id = R.string.meeting_name_question_hint),
            trailingIcon = {
                Text(
                    text = stringResource(
                        id = R.string.meeting_name_length,
                        name.length,
                        MeetingCreationViewModel.MEETING_NAME_MAX_LENGTH,
                    ),
                    style = OdyTheme.typography.pretendardMedium16.copy(color = OdyTheme.colors.senary),
                )
            },
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun MeetingNameScreenPreview() {
    OdyTheme {
        MeetingNameScreen("약속 이름", {})
    }
}
