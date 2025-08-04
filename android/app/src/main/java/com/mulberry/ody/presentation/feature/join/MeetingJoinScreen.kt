package com.mulberry.ody.presentation.feature.join

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulberry.ody.R
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.presentation.common.modifier.noRippleClickable
import com.mulberry.ody.presentation.component.BaseActionHandler
import com.mulberry.ody.presentation.component.OdyActionButton
import com.mulberry.ody.presentation.component.OdyTextField
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.feature.address.OnReceiveAddress
import com.mulberry.ody.presentation.feature.join.model.MeetingJoinNavigateAction
import com.mulberry.ody.presentation.theme.OdyTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun MeetingJoinScreen(
    onBack: () -> Unit,
    inviteCode: String,
    navigate: (MeetingJoinNavigateAction) -> Unit,
    showAddressSearch: (OnReceiveAddress) -> Unit,
    viewModel: MeetingJoinViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackbar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
    val context = LocalContext.current

    val departureAddress by viewModel.departureAddress.collectAsStateWithLifecycle()
    val isJoinValid by viewModel.isJoinValid.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = OdyTheme.colors.primary,
        topBar = {
            OdyTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            tint = Color.Unspecified,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        MeetingJoinContent(
            onNext = { viewModel.joinMeeting(inviteCode) },
            nextButtonEnabled = isJoinValid,
            address = departureAddress,
            showAddressSearch = { showAddressSearch { viewModel.updateMeetingDeparture(it) } },
            getDefaultLocation = { viewModel.getCurrentLocation() },
            modifier = Modifier.padding(innerPadding),
        )
    }

    LaunchedEffect(Unit) {
        viewModel.invalidDepartureEvent.collectLatest {
            showSnackbar(context.getString(R.string.address_question_information))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.currentLocationError.collectLatest {
            showSnackbar(context.getString(R.string.current_location_error_guide))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.navigateAction.collectLatest {
            navigate(it)
        }
    }
    BaseActionHandler(viewModel = viewModel, snackbarHostState = snackbarHostState)
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
