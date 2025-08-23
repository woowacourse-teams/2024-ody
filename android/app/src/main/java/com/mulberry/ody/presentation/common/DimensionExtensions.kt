package com.mulberry.ody.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun Dp.toPx(): Float {
    val density = LocalDensity.current
    return with(density) { this@toPx.toPx() }
}

@Composable
internal fun Float.toDp(): Dp {
    val density = LocalDensity.current
    return with(density) { this@toDp.toDp() }
}

@Composable
internal fun Int.toDp(): Dp {
    val density = LocalDensity.current
    return with(density) { this@toDp.toDp() }
}

@Composable
internal fun Int.dpToPx(): Float {
    val density = LocalDensity.current
    return with(density) { this@dpToPx.dp.toPx() }
}
