package com.mulberry.ody.presentation.common.modifier

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import com.mulberry.ody.presentation.common.NoRippleInteractionSource

fun Modifier.noRippleClickable(onClick: () -> Unit) =
    clickable(
        interactionSource = NoRippleInteractionSource,
        indication = null,
        onClick = onClick,
    )
