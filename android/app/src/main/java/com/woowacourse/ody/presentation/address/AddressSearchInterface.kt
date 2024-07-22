package com.woowacourse.ody.presentation.address

import android.os.Handler
import android.os.Looper

class AddressSearchInterface(private val addressListener: AddressListener) {
    @android.webkit.JavascriptInterface
    fun receive(address: String) {
        Handler(Looper.getMainLooper()).post {
            addressListener.onReceive(address)
        }
    }
}
