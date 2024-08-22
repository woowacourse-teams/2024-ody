package com.woowacourse.ody.data.remote.core.entity.login.mapper

import com.woowacourse.ody.data.remote.core.entity.login.response.LoginResponse
import com.woowacourse.ody.domain.model.AuthToken

fun LoginResponse.toAuthToken(): AuthToken =
    AuthToken(
        accessToken,
        refreshToken,
    )
