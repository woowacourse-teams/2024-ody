package com.mulberry.ody.presentation.component

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulberry.ody.presentation.theme.OdyTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun OdyNumberPicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    visibleItemCount: Int = 3,
) {
    val items = range.map { it.toString().padStart(2, '0') }
    val visibleItemsMiddle = visibleItemCount / 2
    val totalItemsCount = Int.MAX_VALUE
    val middleIndex = totalItemsCount / 2
    val initialIndex = items.indexOf(value.toString().padStart(2, '0')).coerceAtLeast(0)
    val startIndex = middleIndex - (middleIndex % items.size) - visibleItemsMiddle + initialIndex

    fun getItem(index: Int): String = items[index % items.size]

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)
    val flingBehavior = rememberSnapFlingBehavior(listState)

    var itemHeightPx by remember { mutableIntStateOf(0) }
    val itemHeightDp = with(LocalDensity.current) { itemHeightPx.toDp() }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> getItem(index + visibleItemsMiddle) }
            .distinctUntilChanged()
            .collect { selected ->
                onValueChange(selected.toInt())
            }
    }

    Box(
        modifier = modifier
            .width(60.dp)
            .height(itemHeightDp * visibleItemCount + 32.dp)
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            items(totalItemsCount) { index ->
                val isCenter by remember { derivedStateOf { index == listState.firstVisibleItemIndex + visibleItemsMiddle } }
                if (isCenter) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(
                        color = OdyTheme.colors.primaryVariant,
                        thickness = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = getItem(index),
                    textAlign = TextAlign.Center,
                    style = OdyTheme.typography.pretendardBold28,
                    color = if (isCenter) OdyTheme.colors.tertiary else OdyTheme.colors.senary,
                    modifier = Modifier
                        .onSizeChanged {
                            if (!isCenter) {
                                itemHeightPx = it.height
                            }
                        }
                        .fillMaxWidth()
                )
                if (isCenter) {
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(
                        color = OdyTheme.colors.primaryVariant,
                        thickness = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun OdyNumberPickerPreview() {
    OdyTheme {
        OdyNumberPicker(
            value = 0,
            range = 0..24,
            onValueChange = {},
        )
    }
}
