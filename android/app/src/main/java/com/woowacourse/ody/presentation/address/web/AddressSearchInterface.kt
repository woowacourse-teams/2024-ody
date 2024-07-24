package com.woowacourse.ody.presentation.address.web

import android.os.Handler
import android.os.Looper
import com.woowacourse.ody.presentation.address.listener.AddressReceiveListener

class AddressSearchInterface(private val addressReceiveListener: AddressReceiveListener) {
    @android.webkit.JavascriptInterface
    fun receive(address: String) {
        Handler(Looper.getMainLooper()).post {
            addressReceiveListener.onReceive(address)
        }
    }
}
