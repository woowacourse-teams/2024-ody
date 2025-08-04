package com.mulberry.ody.presentation.feature.join

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.mulberry.ody.presentation.component.OdyActionButton
import com.mulberry.ody.presentation.component.OdyTextField
import com.mulberry.ody.presentation.theme.OdyTheme

@Composable
fun MeetingJoinScreen() {
    Scaffold { innerPadding ->
        MeetingJoinContent(
            onNext = {},
            nextButtonEnabled = true,
            address = null,
            showAddressSearch = {

            },
            getDefaultLocation = {

            },
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun MeetingJoinContent(
    onNext: () -> Unit,
    nextButtonEnabled: Boolean,
    address: Address?,
    showAddressSearch: () -> Unit,
    getDefaultLocation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text =
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = OdyTheme.colors.secondary)) {
                    append(stringResource(id = R.string.join_departure_question_front))
                }
                withStyle(style = SpanStyle(color = OdyTheme.colors.quinary)) {
                    append(stringResource(id = R.string.join_departure_question_back))
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
            modifier =
            Modifier
                .padding(horizontal = 40.dp)
                .noRippleClickable(showAddressSearch),
            enabled = false,
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_current_location),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.noRippleClickable(getDefaultLocation),
                )
            },
        )

        Spacer(modifier = Modifier.weight(1f))

        OdyActionButton(
            onClick = onNext,
            enabled = nextButtonEnabled,
            modifier = Modifier.imePadding(),
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun MeetingJoinContentPreview() {
    OdyTheme {
        MeetingJoinContent(
            onNext = { /*TODO*/ },
            nextButtonEnabled = true,
            address = null,
            showAddressSearch = { /*TODO*/ },
            getDefaultLocation = { /*TODO*/ },
        )
    }
}
