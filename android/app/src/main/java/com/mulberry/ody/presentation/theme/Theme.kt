package com.mulberry.ody.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

object OdyTheme {
    val colors: OdyColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalColorScheme.current

    val typography: OdyTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}

val LocalColorScheme = staticCompositionLocalOf { lightOdyColorScheme }
val LocalTypography = staticCompositionLocalOf { OdyTypography }

@Composable
fun OdyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    typography: OdyTypography = OdyTheme.typography,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            darkTheme -> darkOdyColorScheme
            else -> lightOdyColorScheme
        }

    CompositionLocalProvider(
        LocalColorScheme provides colorScheme,
        LocalTypography provides typography,
    ) {
        MaterialTheme(
            content = content,
        )
    }
}
