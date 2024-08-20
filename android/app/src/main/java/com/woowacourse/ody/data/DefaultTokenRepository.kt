package com.woowacourse.ody.data

import com.woowacourse.ody.data.local.db.OdyDatastore
import com.woowacourse.ody.data.remote.core.entity.login.mapper.toAuthToken
import com.woowacourse.ody.data.remote.core.service.AuthService
import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.domain.apiresult.map
import com.woowacourse.ody.domain.common.Provider
import com.woowacourse.ody.domain.model.AuthToken
import com.woowacourse.ody.domain.repository.ody.AuthTokenRepository
import kotlinx.coroutines.flow.first

class DefaultAuthTokenRepository(
    private val odyDatastore: OdyDatastore,
    private val authService: Provider<AuthService>,
) : AuthTokenRepository {
    override suspend fun fetchAuthToken(): Result<AuthToken> {
        return odyDatastore.getAuthToken().first()
    }

    override suspend fun refreshAuthToken(): ApiResult<AuthToken> {
        return authService.get().refreshAccessToken().map {
            val authToken = it.toAuthToken()
            odyDatastore.setAuthToken(authToken)
            authToken
        }
    }
}
