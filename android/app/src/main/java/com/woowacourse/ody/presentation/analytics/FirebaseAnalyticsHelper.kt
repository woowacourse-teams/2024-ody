package com.woowacourse.ody.presentation.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseAnalyticsHelper(context: Context) : AnalyticsHelper {
    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun logEvent(
        eventName: String,
        parameters: Map<String, String>,
    ) {
        val params =
            Bundle().apply {
                parameters.forEach {
                    putString(it.key, it.value)
                }
            }
        firebaseAnalytics.logEvent(eventName, params)
    }
}
