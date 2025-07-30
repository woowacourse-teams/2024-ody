package com.mulberry.ody.presentation.feature.creation.destination

import android.util.Log
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulberry.ody.R
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.presentation.common.modifier.noRippleClickable
import com.mulberry.ody.presentation.component.OdyTextField
import com.mulberry.ody.presentation.theme.OdyTheme

@Composable
fun MeetingDestinationScreen(
    address: Address?,
    showAddressSearch: () -> Unit,
) {
    Log.e("TEST", "MeetingDestinationScreen ${address?.detailAddress}")
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text =
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = OdyTheme.colors.secondary)) {
                    append(stringResource(id = R.string.meeting_destination_question_front))
                }
                withStyle(style = SpanStyle(color = OdyTheme.colors.quinary)) {
                    append(stringResource(id = R.string.meeting_destination_question_back))
                }
            },
            style = OdyTheme.typography.pretendardBold24,
            modifier = Modifier.padding(top = 52.dp, bottom = 32.dp),
        )

        OdyTextField(
            value = address?.detailAddress ?: "",
            onValueChange = {},
            placeholder = stringResource(id = R.string.destination_question_placeholder),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .noRippleClickable(showAddressSearch),
            enabled = false,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun MeetingDestinationScreenPreview() {
    OdyTheme {
        MeetingDestinationScreen(
            address = null,
            showAddressSearch = {},
        )
    }
}
