package com.mulberry.ody.presentation.common.modifier

import androidx.compose.foundation.clickable
import com.mulberry.ody.presentation.common.NoRippleInteractionSource
import androidx.compose.ui.Modifier

fun Modifier.noRippleClickable(onClick: () -> Unit) =
    clickable(
        interactionSource = NoRippleInteractionSource,
        indication = null,
        onClick = onClick,
    )
