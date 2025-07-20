package com.mulberry.ody.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulberry.ody.presentation.theme.OdyTheme

@Composable
fun OdyIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(pagerState.pageCount) { page ->
            val isCurrentPage = pagerState.currentPage == page
            val color by animateColorAsState(
                targetValue = if (isCurrentPage) OdyTheme.colors.secondary else OdyTheme.colors.senary,
                animationSpec = tween(durationMillis = 300),
                label = "indicatorColor",
            )
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(size = 10.dp),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun OdyIndicatorPreview() {
    OdyTheme {
        OdyIndicator(
            pagerState = rememberPagerState(
                initialPage = 1,
                pageCount = { 5 },
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 58.dp)
        )
    }
}
