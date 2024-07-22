package com.woowacourse.ody.presentation.address

class AddressSearchInterface(private val onReceive: (String) -> Unit) {
    @android.webkit.JavascriptInterface
    fun receive(address: String) {
        onReceive(address)
    }
}
