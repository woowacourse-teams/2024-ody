package com.ydo.ody.fake

import android.os.Bundle
import com.ydo.ody.presentation.common.analytics.AnalyticsHelper

object FakeAnalyticsHelper : AnalyticsHelper {
    override fun logEvent(
        eventName: String,
        bundle: Bundle,
    ) = Unit
}
