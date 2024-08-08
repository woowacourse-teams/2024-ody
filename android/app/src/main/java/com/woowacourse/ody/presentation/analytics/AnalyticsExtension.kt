package com.woowacourse.ody.presentation.analytics

fun AnalyticsHelper.logEvent(
    eventName: String,
    eventMessage: String,
)  {
    this.logEvent(eventName, mapOf("message" to eventMessage))
}
