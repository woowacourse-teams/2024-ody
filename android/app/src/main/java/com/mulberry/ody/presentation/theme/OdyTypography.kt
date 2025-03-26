package com.mulberry.ody.presentation.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mulberry.ody.R

private val OdyFontFamily =
    FontFamily(
        Font(R.font.pretendard_bold, FontWeight.Bold),
        Font(R.font.pretendard_medium, FontWeight.Medium),
        Font(R.font.pretendard_regular, FontWeight.Light),
    )

object OdyTypography {
    val pretendardBold28 =
        TextStyle(
            fontFamily = OdyFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 33.4.sp,
        )

    val pretendardBold24 =
        TextStyle(
            fontFamily = OdyFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 28.6.sp,
        )

    val pretendardBold20 =
        TextStyle(
            fontFamily = OdyFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 23.9.sp,
        )

    val pretendardBold16 =
        TextStyle(
            fontFamily = OdyFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 19.1.sp,
        )

    val pretendardMedium20 =
        TextStyle(
            fontFamily = OdyFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            lineHeight = 23.9.sp,
        )

    val pretendardMedium18 =
        TextStyle(
            fontFamily = OdyFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            lineHeight = 21.5.sp,
        )

    val pretendardMedium16 =
        TextStyle(
            fontFamily = OdyFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 19.1.sp,
        )

    val pretendardMedium14 =
        TextStyle(
            fontFamily = OdyFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 16.7.sp,
        )

    val pretendardRegular16 =
        TextStyle(
            fontFamily = OdyFontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 16.sp,
            lineHeight = 19.1.sp,
        )

    val pretendardRegular14 =
        TextStyle(
            fontFamily = OdyFontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 14.sp,
            lineHeight = 16.7.sp,
        )

    val pretendardRegular12 =
        TextStyle(
            fontFamily = OdyFontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 12.sp,
            lineHeight = 14.3.sp,
        )
}
