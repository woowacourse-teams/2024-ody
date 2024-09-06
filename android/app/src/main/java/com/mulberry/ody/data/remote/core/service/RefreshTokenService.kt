package com.mulberry.ody.data.remote.core.service

import com.mulberry.ody.data.remote.core.entity.login.response.LoginResponse
import com.mulberry.ody.domain.apiresult.ApiResult
import retrofit2.http.POST

interface RefreshTokenService {
    @POST("/v1/auth/refresh")
    suspend fun postRefreshToken(): ApiResult<LoginResponse>
}
