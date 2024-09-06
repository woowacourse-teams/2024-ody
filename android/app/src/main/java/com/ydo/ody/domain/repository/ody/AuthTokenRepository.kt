package com.ydo.ody.domain.repository.ody

import com.ydo.ody.domain.apiresult.ApiResult
import com.ydo.ody.domain.model.AuthToken

interface AuthTokenRepository {
    suspend fun fetchAuthToken(): Result<AuthToken>

    suspend fun refreshAuthToken(): ApiResult<AuthToken>
}
