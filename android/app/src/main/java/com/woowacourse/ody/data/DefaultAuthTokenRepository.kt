package com.woowacourse.ody.data

import com.woowacourse.ody.data.local.db.OdyDatastore
import com.woowacourse.ody.data.remote.core.entity.login.mapper.toAuthToken
import com.woowacourse.ody.data.remote.core.service.RefreshTokenService
import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.domain.apiresult.map
import com.woowacourse.ody.domain.common.Provider
import com.woowacourse.ody.domain.model.AuthToken
import com.woowacourse.ody.domain.repository.ody.AuthTokenRepository
import kotlinx.coroutines.flow.first

class DefaultAuthTokenRepository(
    private val odyDatastore: OdyDatastore,
    private val refreshTokenService: Provider<RefreshTokenService>,
) : AuthTokenRepository {
    override suspend fun fetchAuthToken(): Result<AuthToken> {
        return odyDatastore.getAuthToken().first()
    }

    override suspend fun refreshAuthToken(): ApiResult<AuthToken> {
        return refreshTokenService.get().postRefreshToken().map {
            val authToken = it.toAuthToken()
            odyDatastore.setAuthToken(authToken)
            authToken
        }
    }
}
