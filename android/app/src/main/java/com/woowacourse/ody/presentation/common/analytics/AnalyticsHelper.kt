package com.woowacourse.ody.presentation.common.analytics

import android.os.Bundle

interface AnalyticsHelper {
    fun logEvent(eventName: String, bundle: Bundle)
}
