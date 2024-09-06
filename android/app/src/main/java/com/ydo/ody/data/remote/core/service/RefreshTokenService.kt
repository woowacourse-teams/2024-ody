package com.ydo.ody.data.remote.core.service

import com.ydo.ody.data.remote.core.entity.login.response.LoginResponse
import com.ydo.ody.domain.apiresult.ApiResult
import retrofit2.http.POST

interface RefreshTokenService {
    @POST("/v1/auth/refresh")
    suspend fun postRefreshToken(): ApiResult<LoginResponse>
}
