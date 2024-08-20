package com.woowacourse.ody.data.remote.core.service

import com.woowacourse.ody.data.remote.core.entity.login.request.LoginRequest
import com.woowacourse.ody.data.remote.core.entity.login.response.LoginResponse
import com.woowacourse.ody.domain.apiresult.ApiResult
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/v1/auth/kakao")
    suspend fun loginWithKakao(
        @Body loginRequest: LoginRequest,
    ): ApiResult<LoginResponse>

    @POST("/v1/auth/refresh")
    suspend fun refreshAccessToken(): ApiResult<LoginResponse>
}
