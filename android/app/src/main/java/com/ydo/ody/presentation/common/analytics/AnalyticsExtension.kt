package com.ydo.ody.presentation.common.analytics

import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics

fun AnalyticsHelper.logNetworkErrorEvent(
    name: String?,
    message: String?,
) {
    val bundle =
        bundleOf(
            "name" to name,
            "message" to message,
        )
    this.logEvent("network_error", bundle)
}

fun AnalyticsHelper.logButtonClicked(
    eventName: String,
    location: String?,
) {
    val bundle =
        bundleOf(
            FirebaseAnalytics.Param.CONTENT_TYPE to "button",
            FirebaseAnalytics.Param.METHOD to "click",
            FirebaseAnalytics.Param.LOCATION to location,
        )
    this.logEvent(eventName, bundle)
}
