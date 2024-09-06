package com.ydo.ody.data

import com.ydo.ody.data.local.db.OdyDatastore
import com.ydo.ody.data.remote.core.entity.login.mapper.toAuthToken
import com.ydo.ody.data.remote.core.service.RefreshTokenService
import com.ydo.ody.domain.apiresult.ApiResult
import com.ydo.ody.domain.apiresult.map
import com.ydo.ody.domain.common.Provider
import com.ydo.ody.domain.model.AuthToken
import com.ydo.ody.domain.repository.ody.AuthTokenRepository
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
