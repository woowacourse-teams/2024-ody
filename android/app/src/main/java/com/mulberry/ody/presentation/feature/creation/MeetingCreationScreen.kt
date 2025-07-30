package com.mulberry.ody.presentation.feature.creation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulberry.ody.R
import com.mulberry.ody.presentation.component.BaseActionHandler
import com.mulberry.ody.presentation.component.OdyActionButton
import com.mulberry.ody.presentation.component.OdyIndicator
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.feature.creation.date.MeetingDateScreen
import com.mulberry.ody.presentation.feature.creation.destination.MeetingDestinationScreen
import com.mulberry.ody.presentation.feature.creation.model.MeetingCreationNavigateAction
import com.mulberry.ody.presentation.feature.creation.name.MeetingNameScreen
import com.mulberry.ody.presentation.feature.creation.time.MeetingTimeScreen
import com.mulberry.ody.presentation.theme.OdyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun MeetingCreationScreen(
    showAddressSearch: (OnReceiveAddress) -> Unit,
    onBack: () -> Unit,
    navigate: (MeetingCreationNavigateAction) -> Unit,
    viewModel: MeetingCreationViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackbar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
    val context = LocalContext.current

    val meetingCreationUiModel by viewModel.meetingCreationUiModel.collectAsStateWithLifecycle()
    val isCreationValid by viewModel.isCreationValid.collectAsStateWithLifecycle()

    val pages: List<(@Composable () -> Unit)> =
        listOf(
            {
                MeetingNameScreen(
                    name = meetingCreationUiModel.name,
                    onNameChanged = { viewModel.updateMeetingName(it) },
                )
            },
            {
                MeetingDateScreen(
                    date = meetingCreationUiModel.date,
                    onDateChanged = { viewModel.updateMeetingDate(it) },
                )
            },
            {
                MeetingTimeScreen(
                    time = meetingCreationUiModel.time,
                    onTimeChanged = { viewModel.updateMeetingTime(it) },
                )
            },
            {
                MeetingDestinationScreen(
                    address = meetingCreationUiModel.destination,
                    showAddressSearch = { showAddressSearch { viewModel.updateMeetingDestination(it) } },
                    getDefaultLocation = { viewModel.getDefaultLocation() },
                )
            },
        )
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Scaffold(
        containerColor = OdyTheme.colors.primary,
        topBar = {
            OdyTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { scope.moveToPreviousPage(pagerState, onBack) }) {
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
        modifier = Modifier.navigationBarsPadding(),
    ) { innerPadding ->
        MeetingCreationContent(
            pages = pages,
            pagerState = pagerState,
            onNext = { scope.moveToNextPage(pagerState, onNext = { viewModel.createMeeting() }) },
            nextButtonEnabled = isCreationValid,
            modifier = Modifier.padding(innerPadding),
        )
    }

    LaunchedEffect(Unit) {
        viewModel.invalidDestinationEvent.collectLatest {
            showSnackbar(context.getString(R.string.address_question_information))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.currentLocationError.collectLatest {
            showSnackbar(context.getString(R.string.default_location_error_guide))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.navigateAction.collectLatest {
            navigate(it)
        }
    }
    BaseActionHandler(viewModel = viewModel, snackbarHostState = snackbarHostState)
}

private fun CoroutineScope.moveToPreviousPage(
    pagerState: PagerState,
    onBack: () -> Unit,
) {
    if (pagerState.currentPage == 0) {
        onBack()
    } else {
        launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        }
    }
}

private fun CoroutineScope.moveToNextPage(
    pagerState: PagerState,
    onNext: () -> Unit,
) {
    if (pagerState.currentPage == pagerState.pageCount - 1) {
        onNext()
    } else {
        launch {
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }
}

@Composable
private fun MeetingCreationContent(
    pages: List<(@Composable () -> Unit)>,
    pagerState: PagerState,
    onNext: () -> Unit,
    nextButtonEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        OdyIndicator(
            pagerState = pagerState,
            modifier =
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(top = 50.dp),
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = false,
        ) { currentPage ->
            Box(modifier = Modifier.fillMaxSize()) {
                pages[currentPage]()
            }
        }
        OdyActionButton(
            onClick = onNext,
            enabled = nextButtonEnabled,
            modifier = Modifier.imePadding(),
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun MeetingCreationContentPreview() {
    OdyTheme {
        val page = @Composable {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "MeetingCreationSubScreen",
                )
            }
        }

        MeetingCreationContent(
            pages = listOf(page, page),
            pagerState = rememberPagerState { 2 },
            onNext = {},
            nextButtonEnabled = true,
        )
    }
}
