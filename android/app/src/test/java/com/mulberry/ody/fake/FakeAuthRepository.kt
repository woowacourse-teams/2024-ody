package com.mulberry.ody.fake

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.AuthToken
import com.mulberry.ody.domain.repository.ody.AuthRepository

class FakeAuthRepository(
    private val isLoggedIn: Boolean = true,
    private val authToken: AuthToken = AuthToken("", "")
) : AuthRepository {
    override suspend fun isLoggedIn(): Boolean {
        return isLoggedIn
    }

    override suspend fun login(): ApiResult<AuthToken> {
        return ApiResult.Success(authToken)
    }

    override suspend fun logout(): ApiResult<Unit> {
        return ApiResult.Success(Unit)
    }

    override suspend fun withdrawAccount(): ApiResult<Unit> {
        return ApiResult.Success(Unit)
    }
}
