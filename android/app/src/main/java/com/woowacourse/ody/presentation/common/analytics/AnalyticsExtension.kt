package com.woowacourse.ody.presentation.common.analytics

import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics

fun FirebaseAnalytics.logNetworkErrorEvent(
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

fun FirebaseAnalytics.logButtonClicked(
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
