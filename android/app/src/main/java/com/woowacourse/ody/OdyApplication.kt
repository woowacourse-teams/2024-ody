package com.woowacourse.ody

import android.app.Application
import com.woowacourse.ody.BuildConfig.DEBUG
import com.woowacourse.ody.data.local.db.OdyDatastore
import timber.log.Timber

class OdyApplication : Application() {
    val odyDatastore by lazy { OdyDatastore(this) }

    override fun onCreate() {
        super.onCreate()
        if (DEBUG) {
            Timber.plant(OdyDebugTree)
        }
    }
}

object OdyDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String =
        "${element.fileName}:${element.lineNumber}#${element.methodName}"
}
