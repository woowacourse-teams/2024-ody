package com.mulberry.ody.data.local.repository

import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.data.remote.core.entity.login.mapper.toAuthToken
import com.mulberry.ody.data.remote.core.service.RefreshTokenService
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.apiresult.map
import com.mulberry.ody.domain.model.AuthToken
import com.mulberry.ody.domain.repository.ody.AuthTokenRepository
import dagger.Lazy
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DefaultAuthTokenRepository
    @Inject
    constructor(
        private val odyDatastore: OdyDatastore,
        private val refreshTokenService: Lazy<RefreshTokenService>,
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

        override suspend fun removeAuthToken() {
            odyDatastore.removeAuthToken()
        }

        override suspend fun setAuthToken(authToken: AuthToken) {
            odyDatastore.setAuthToken(authToken)
        }
    }
