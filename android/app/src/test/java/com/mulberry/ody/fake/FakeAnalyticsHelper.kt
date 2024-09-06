package com.mulberry.ody.fake

import android.os.Bundle
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper

object FakeAnalyticsHelper : AnalyticsHelper {
    override fun logEvent(
        eventName: String,
        bundle: Bundle,
    ) = Unit
}
