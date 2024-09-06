package com.ydo.ody.data.remote.core.entity.login.mapper

import com.ydo.ody.data.remote.core.entity.login.response.LoginResponse
import com.ydo.ody.domain.model.AuthToken

fun LoginResponse.toAuthToken(): AuthToken =
    AuthToken(
        accessToken,
        refreshToken,
    )
