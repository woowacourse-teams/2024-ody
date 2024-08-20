package com.woowacourse.ody.data.retrofit

import com.woowacourse.ody.domain.model.AuthToken
import com.woowacourse.ody.domain.repository.ody.AuthTokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AccessTokenInterceptor(
    private val tokenRepository: AuthTokenRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token =
            fetchAuthToken().getOrElse {
                return chain.proceed(chain.request())
            }
        val newRequest =
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        return chain.proceed(newRequest)
    }

    private fun fetchAuthToken(): Result<AuthToken> =
        runBlocking {
            tokenRepository.fetchAuthToken()
        }
}
