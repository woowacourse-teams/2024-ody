package com.mulberry.ody.fake

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.AuthToken
import com.mulberry.ody.domain.repository.ody.AuthRepository

object FakeAuthRepository : AuthRepository {
    override suspend fun isLoggedIn(): Boolean {
        return true
    }

    override suspend fun login(): ApiResult<AuthToken> {
        return ApiResult.Success(AuthToken("accessToken", "refreshToken"))
    }

    override suspend fun logout(): ApiResult<Unit> {
        return ApiResult.Success(Unit)
    }

    override suspend fun withdrawAccount(): ApiResult<Unit> {
        return ApiResult.Success(Unit)
    }
}
