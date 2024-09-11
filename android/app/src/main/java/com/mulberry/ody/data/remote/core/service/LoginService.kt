package com.mulberry.ody.data.remote.core.service

import com.mulberry.ody.data.remote.core.entity.login.request.LoginRequest
import com.mulberry.ody.data.remote.core.entity.login.response.LoginResponse
import com.mulberry.ody.domain.apiresult.ApiResult
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("/v1/auth/kakao")
    suspend fun postLoginWithKakao(
        @Body loginRequest: LoginRequest,
    ): ApiResult<LoginResponse>
}
