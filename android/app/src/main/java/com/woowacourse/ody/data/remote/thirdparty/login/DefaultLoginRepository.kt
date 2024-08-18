package com.woowacourse.ody.data.remote.thirdparty.login

import android.content.Context
import com.woowacourse.ody.data.remote.thirdparty.login.kakao.KakaoLoginService

class DefaultLoginRepository(private val context: Context) {
    suspend fun loginWithKakao() {
        KakaoLoginService(context).login()
    }
}
