package com.woowacourse.ody.data.remote.core.service

import com.woowacourse.ody.data.remote.core.entity.login.response.LoginResponse
import com.woowacourse.ody.domain.apiresult.ApiResult
import retrofit2.http.POST

interface RefreshTokenService {
    @POST("/v1/auth/refresh")
    suspend fun refreshAccessToken(): ApiResult<LoginResponse>
}
