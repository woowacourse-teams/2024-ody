package com.mulberry.ody.data.retrofit

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.AuthToken
import com.mulberry.ody.domain.repository.ody.AuthTokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AccessTokenInterceptor
    @Inject
    constructor(
        private val tokenRepository: AuthTokenRepository,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val token =
                fetchAuthToken().getOrElse {
                    return chain.proceed(chain.request())
                }
            val request =
                chain.request().newBuilder()
                    .header("Authorization", "Bearer access-token=${token.accessToken}")
                    .build()
            val response = chain.proceed(request)

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
                tokenRepository.refreshAuthToken()
            }
    }
