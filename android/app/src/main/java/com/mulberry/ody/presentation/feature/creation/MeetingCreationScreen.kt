package com.mulberry.ody.presentation.feature.creation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulberry.ody.R
import com.mulberry.ody.presentation.component.OdyActionButton
import com.mulberry.ody.presentation.component.OdyIndicator
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.theme.OdyTheme

@Composable
fun MeetingCreationSubScreen(index: Int) {
    Text(
        text = "MeetingCreationSubScreen $index",
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
fun MeetingCreationScreen(onBack: () -> Unit) {
    val pages: List<(@Composable () -> Unit)> = listOf(
        { MeetingCreationSubScreen(1) },
        { MeetingCreationSubScreen(2) },
        { MeetingCreationSubScreen(3) },
        { MeetingCreationSubScreen(4) },
    )
    val pagerState = rememberPagerState(pageCount = { 4 })
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
    ) { innerPadding ->
        MeetingCreationContent(
            pages = pages,
            pagerState = pagerState,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun MeetingCreationContent(
    pages: List<(@Composable () -> Unit)>,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val currentPage = pagerState.currentPage
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        OdyIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(top = 50.dp)
        )
        Box(modifier = Modifier.weight(1f)) {
            pages[currentPage]()
        }
        OdyActionButton(onClick = { /*TODO*/ })
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
        )
    }
}
