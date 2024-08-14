package com.woowacourse.ody.fake

import android.os.Bundle
import com.woowacourse.ody.presentation.common.analytics.AnalyticsHelper

object FakeAnalyticsHelper : AnalyticsHelper {
    override fun logEvent(
        eventName: String,
        bundle: Bundle,
    ) {}
}
