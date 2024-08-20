package com.woowacourse.ody.data.retrofit

import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.domain.model.AuthToken
import com.woowacourse.ody.domain.repository.ody.AuthTokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class RefreshTokenInterceptor(
    private val tokenRepository: AuthTokenRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token =
            fetchAuthToken().getOrElse {
                return chain.proceed(chain.request())
            }
        val request = chain.request()
        val isRefreshTokenRequest = request.url.encodedPath.contains("refresh")
        val tokenString =
            if (isRefreshTokenRequest) {
                "access-token=${token.accessToken} refresh-token=${token.refreshToken}"
            } else {
                "access-token=${token.accessToken}"
            }

        val accessTokenRequest =
            request.newBuilder()
                .header("Authorization", "Bearer $tokenString")
                .build()
        val response = chain.proceed(accessTokenRequest)

        if (response.code == 401) {
            val newToken = refreshAuthToken()
            val newRequest =
                if (newToken is ApiResult.Success) {
                    chain.request().newBuilder()
                        .header("Authorization", "Bearer access-token=${newToken.data.accessToken}")
                        .build()
                } else {
                    chain.request()
                }
            return chain.proceed(newRequest)
        }
        return response
    }

    private fun fetchAuthToken(): Result<AuthToken> =
        runBlocking {
            tokenRepository.fetchAuthToken()
        }

    private fun refreshAuthToken(): ApiResult<AuthToken> =
        runBlocking(Dispatchers.IO) {
            println(tokenRepository)
            tokenRepository.refreshAuthToken()
        }
}
