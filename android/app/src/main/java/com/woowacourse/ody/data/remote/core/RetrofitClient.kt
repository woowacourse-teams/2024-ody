package com.woowacourse.ody.data.remote.core

import com.woowacourse.ody.BuildConfig
import com.woowacourse.ody.data.retrofit.AccessTokenInterceptor
import com.woowacourse.ody.data.retrofit.ApiResultCallAdapter
import com.woowacourse.ody.data.retrofit.RefreshTokenInterceptor
import com.woowacourse.ody.domain.repository.ody.AuthTokenRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

class RetrofitClient(
    private val authTokenRepository: AuthTokenRepository,
    private val baseUrl: String = BuildConfig.BASE_URL,
) {
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addCallAdapterFactory(ApiResultCallAdapter.Factory())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        val builder =
            OkHttpClient.Builder()
                .addInterceptor(accessTokenInterceptor)
                .addInterceptor(refreshTokenInterceptor)
                .addInterceptor(loggingInterceptor)
        builder.build()
    }

    private val accessTokenInterceptor: Interceptor by lazy {
        AccessTokenInterceptor(authTokenRepository)
    }

    private val refreshTokenInterceptor: Interceptor by lazy {
        RefreshTokenInterceptor(authTokenRepository)
    }

    private val loggingInterceptor by lazy {
        val logger =
            HttpLoggingInterceptor.Logger { message ->
                Timber.tag("OkHttp").d(message)
            }
        HttpLoggingInterceptor(logger).setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}
