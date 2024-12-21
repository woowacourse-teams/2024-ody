package com.mulberry.ody.data.local.repository

import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.domain.model.AuthToken
import com.mulberry.ody.domain.repository.ody.AuthTokenRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DefaultAuthTokenRepository
@Inject
constructor(
    private val odyDatastore: OdyDatastore,
) : AuthTokenRepository {
    override suspend fun fetchAuthToken(): Result<AuthToken> {
        return odyDatastore.getAuthToken().first()
    }

    override suspend fun removeAuthToken() {
        odyDatastore.removeAuthToken()
    }

    override suspend fun setAuthToken(authToken: AuthToken) {
        odyDatastore.setAuthToken(authToken)
    }
}
