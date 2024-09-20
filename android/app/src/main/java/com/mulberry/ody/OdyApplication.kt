package com.mulberry.ody

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.mulberry.ody.BuildConfig.DEBUG
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class OdyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (DEBUG) {
            Timber.plant(OdyDebugTree)
        }
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
    }
}

object OdyDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String =
        "${element.fileName}:${element.lineNumber}#${element.methodName}"
}
