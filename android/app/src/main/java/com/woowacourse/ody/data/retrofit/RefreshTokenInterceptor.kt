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
        val response = chain.proceed(chain.request())
        if (response.code == 401) {
            val newToken = refreshAuthToken()
            val newRequest =
                if (newToken is ApiResult.Success) {
                    println(newToken)
                    chain.request().newBuilder()
                        .header("Authorization", "Bearer ${newToken.data}")
                        .build()
                } else {
                    chain.request()
                }
            return chain.proceed(newRequest)
        }
        return response
    }

    private fun refreshAuthToken(): ApiResult<AuthToken> =
        runBlocking(Dispatchers.IO) {
            println(tokenRepository)
            tokenRepository.refreshAuthToken()
        }
}
