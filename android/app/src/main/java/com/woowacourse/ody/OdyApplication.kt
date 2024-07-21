package com.woowacourse.ody

import android.app.Application
import timber.log.Timber

class OdyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(OdyDebugTree)
        }
    }
}

object OdyDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String =
        "${element.fileName}:${element.lineNumber}#${element.methodName}"
}
