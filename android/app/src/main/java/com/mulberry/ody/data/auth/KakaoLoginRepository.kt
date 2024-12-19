package com.mulberry.ody.data.auth

import android.content.Context
import com.mulberry.ody.data.auth.source.LocalAuthDataSource
import com.mulberry.ody.data.auth.source.RemoteAuthDataSource
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.apiresult.getOrNull
import com.mulberry.ody.domain.apiresult.suspendOnSuccess
import com.mulberry.ody.domain.model.AuthToken
import com.mulberry.ody.domain.repository.ody.LoginRepository
import javax.inject.Inject

class KakaoLoginRepository
    @Inject
    constructor(
        private val localAuthDataSource: LocalAuthDataSource,
        private val remoteAuthDataSource: RemoteAuthDataSource,
    ) : LoginRepository {
        override suspend fun checkIfLoggedIn(): Boolean {
            if (!remoteAuthDataSource.checkIfLoggedIn()) {
                return false
            }
            if (localAuthDataSource.fetchAuthToken().getOrNull() == null) {
                return false
            }
            val authToken = remoteAuthDataSource.postAuthToken().getOrNull() ?: return false
            localAuthDataSource.postAuthToken(authToken)
            return true
        }

        override suspend fun login(context: Context): ApiResult<AuthToken> {
            val fcmToken = localAuthDataSource.fetchFCMToken().getOrNull() ?: return ApiResult.Unexpected(Exception())
            return remoteAuthDataSource.login(fcmToken, context).suspendOnSuccess {
                localAuthDataSource.postAuthToken(it)
            }
        }

        override suspend fun logout(): ApiResult<Unit> {
            localAuthDataSource.removeAuthToken()
            return remoteAuthDataSource.logout()
        }

        override suspend fun withdrawAccount(): ApiResult<Unit> {
            return remoteAuthDataSource.withdraw().suspendOnSuccess {
                localAuthDataSource.removeAuthToken()
            }
        }
    }
