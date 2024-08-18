package com.woowacourse.ody.data.remote.thirdparty.login.kakao

import com.kakao.sdk.auth.model.OAuthToken
import com.woowacourse.ody.data.remote.thirdparty.login.AuthToken

fun OAuthToken.toAuthToken(): AuthToken =
    AuthToken(
        accessToken,
        refreshToken,
    )
