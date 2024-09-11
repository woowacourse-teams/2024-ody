package com.mulberry.ody.domain.repository.ody

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.AuthToken

interface AuthTokenRepository {
    suspend fun fetchAuthToken(): Result<AuthToken>

    suspend fun refreshAuthToken(): ApiResult<AuthToken>
}
