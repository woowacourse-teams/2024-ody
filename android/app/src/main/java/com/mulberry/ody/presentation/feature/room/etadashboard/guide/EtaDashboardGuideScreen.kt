package com.mulberry.ody.presentation.feature.room.etadashboard.guide

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun EtaDashboardGuideScreen(onDismiss: () -> Unit) {
    var showFirstGuide by remember { mutableStateOf(true) }

    if (showFirstGuide) {
        EtaDashboardFirstGuideScreen(onClick = { showFirstGuide = false })
    } else {
        EtaDashboardSecondGuideScreen(onClick = onDismiss)
    }
}
