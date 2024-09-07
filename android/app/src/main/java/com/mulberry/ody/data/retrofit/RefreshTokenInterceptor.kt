package com.mulberry.ody.data.retrofit

import com.mulberry.ody.domain.model.AuthToken
import com.mulberry.ody.domain.repository.ody.AuthTokenRepository
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
        val request =
            chain.request().newBuilder()
                .header("Authorization", "Bearer access-token=${token.accessToken} refresh-token=${token.refreshToken}")
                .build()
        return chain.proceed(request)
    }

    private fun fetchAuthToken(): Result<AuthToken> =
        runBlocking {
            tokenRepository.fetchAuthToken()
        }
}
