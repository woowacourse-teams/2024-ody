package com.mulberry.ody.domain.repository.ody

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.AuthToken

interface AuthRepository {
    suspend fun isLoggedIn(): Boolean

    suspend fun login(): ApiResult<AuthToken>

    suspend fun logout(): ApiResult<Unit>

    suspend fun withdrawAccount(): ApiResult<Unit>
}
