package com.woowacourse.ody.presentation.analytics

interface AnalyticsHelper {
    fun logEvent(
        eventName: String,
        parameters: Map<String, String>,
    )
}
