package com.mulberry.ody.presentation.common.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseAnalyticsHelper(context: Context) : AnalyticsHelper {
    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun logEvent(
        eventName: String,
        bundle: Bundle,
    ) {
        firebaseAnalytics.logEvent(eventName, bundle)
    }
}
