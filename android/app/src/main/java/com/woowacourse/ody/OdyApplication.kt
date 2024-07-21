package com.woowacourse.ody

import android.app.Application
import com.google.android.datatransport.BuildConfig
import timber.log.Timber

class OdyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
