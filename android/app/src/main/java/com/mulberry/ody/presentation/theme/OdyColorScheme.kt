package com.mulberry.ody.presentation.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

class OdyColorScheme(
    primary: Color,
    primaryVariant: Color,
    secondary: Color,
    secondaryVariant: Color,
    tertiary: Color,
    quarternary: Color,
    quinary: Color,
    senary: Color,
    septenary: Color,
    octonary: Color,
    nonary: Color,
) {
    var primary by mutableStateOf(primary)
        private set
    var primaryVariant by mutableStateOf(primaryVariant)
        private set
    var secondary by mutableStateOf(secondary)
        private set
    var secondaryVariant by mutableStateOf(secondaryVariant)
        private set
    var tertiary by mutableStateOf(tertiary)
        private set
    var quarternary by mutableStateOf(quarternary)
        private set
    var quinary by mutableStateOf(quinary)
        private set
    var senary by mutableStateOf(senary)
        private set
    var septenary by mutableStateOf(septenary)
        private set
    var octonary by mutableStateOf(octonary)
        private set
    var nonary by mutableStateOf(nonary)
        private set
}

val lightOdyColorScheme: OdyColorScheme by lazy {
    OdyColorScheme(
        primary = Cream,
        primaryVariant = Purple300,
        secondary = Purple800,
        secondaryVariant = White,
        tertiary = Black,
        quarternary = Gray500,
        quinary = Gray800,
        senary = Gray350,
        septenary = Cream,
        octonary = White,
        nonary = Gray600,
    )
}

val darkOdyColorScheme: OdyColorScheme by lazy {
    OdyColorScheme(
        primary = Black,
        primaryVariant = Purple800,
        secondary = Purple300,
        secondaryVariant = White,
        tertiary = Cream,
        quarternary = Gray350,
        quinary = Cream,
        senary = Gray500,
        septenary = Gray900,
        octonary = Black,
        nonary = Gray500,
    )
}
