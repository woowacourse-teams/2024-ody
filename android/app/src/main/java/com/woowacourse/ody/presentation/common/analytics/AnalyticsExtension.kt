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
