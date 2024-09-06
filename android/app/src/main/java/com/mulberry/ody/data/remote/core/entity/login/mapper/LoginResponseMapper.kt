package com.mulberry.ody.data.remote.core.entity.login.mapper

import com.mulberry.ody.data.remote.core.entity.login.response.LoginResponse
import com.mulberry.ody.domain.model.AuthToken

fun LoginResponse.toAuthToken(): AuthToken =
    AuthToken(
        accessToken,
        refreshToken,
    )
