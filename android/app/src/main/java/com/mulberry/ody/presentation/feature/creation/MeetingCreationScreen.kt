package com.mulberry.ody.presentation.feature.creation

import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mulberry.ody.R
import com.mulberry.ody.presentation.component.OdyActionButton
import com.mulberry.ody.presentation.component.OdyIndicator
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.feature.creation.date.MeetingDateScreen
import com.mulberry.ody.presentation.feature.creation.destination.MeetingDestinationScreen
import com.mulberry.ody.presentation.feature.creation.model.rememberSaveableMeetingCreationUiModel
import com.mulberry.ody.presentation.feature.creation.name.MeetingNameScreen
import com.mulberry.ody.presentation.feature.creation.time.MeetingTimeScreen
import com.mulberry.ody.presentation.theme.OdyTheme
import kotlinx.coroutines.launch

@Composable
fun MeetingCreationScreen(
    onBack: () -> Unit,
    viewModel: MeetingCreationViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackbar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    val uiModel = rememberSaveableMeetingCreationUiModel()
    val pages: List<(@Composable () -> Unit)> = listOf(
        {
            MeetingNameScreen(
                name = uiModel.name,
                onNameChanged = { uiModel.updateName(it) }
            )
        },
        {
            MeetingDateScreen(
                date = uiModel.date,
                onDateChanged = { uiModel.updateDate(it) },
                showSnackBar = showSnackbar,
                viewModel = viewModel,
            )
        },
        {
            MeetingTimeScreen(
                time = uiModel.time,
                onTimeChanged = { uiModel.updateTime(it) },
                showSnackBar = showSnackbar,
                viewModel = viewModel,
            )
        },
        {
            MeetingDestinationScreen(
                destination = uiModel.destination,
                onDestinationChanged = { uiModel.updateDestination(it) },
                showSnackBar = showSnackbar,
                viewModel = viewModel,
            )
        },
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })

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
        modifier = Modifier.navigationBarsPadding()
    ) { innerPadding ->
        MeetingCreationContent(
            pages = pages,
            pagerState = pagerState,
            onNext = {
                if (pagerState.currentPage == pagerState.pageCount - 1) {
                    val info = uiModel.convertMeetingCreationInfo() ?: return@MeetingCreationContent
                    viewModel.createMeeting(info)
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            nextButtonEnabled = uiModel.isValid,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun MeetingCreationContent(
    pages: List<(@Composable () -> Unit)>,
    pagerState: PagerState,
    onNext: () -> Unit,
    nextButtonEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val currentPage = pagerState.currentPage
    Column {
        OdyIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(top = 50.dp)
        )
        HorizontalPager(
            state = pagerState,
            modifier = modifier
                .weight(1f)
                .pointerInput(nextButtonEnabled) {
                    detectDragGestures { change, dragAmount ->
                        if (!nextButtonEnabled && dragAmount.x < 0) {
                            change.consume()
                        }
                    }
                },
        ) {
            Box(modifier = Modifier.weight(1f)) {
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
                contentAlignment = Alignment.Center
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
