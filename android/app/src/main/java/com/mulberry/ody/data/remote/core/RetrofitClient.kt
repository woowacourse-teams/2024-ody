package com.mulberry.ody.data.remote.core

import com.mulberry.ody.BuildConfig
import com.mulberry.ody.data.retrofit.AccessTokenInterceptor
import com.mulberry.ody.data.retrofit.ApiResultCallAdapter
import com.mulberry.ody.data.retrofit.RefreshTokenInterceptor
import com.mulberry.ody.domain.repository.ody.AuthTokenRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

class RetrofitClient(private val authTokenRepository: AuthTokenRepository) {
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(authHttpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(ApiResultCallAdapter.Factory())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    val loginRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(ApiResultCallAdapter.Factory())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    val refreshRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(refreshHttpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(ApiResultCallAdapter.Factory())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        val builder =
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
        builder.build()
    }

    private val authHttpClient: OkHttpClient by lazy {
        val builder =
            OkHttpClient.Builder()
                .addInterceptor(authTokenInterceptor)
                .addInterceptor(loggingInterceptor)
        builder.build()
    }

    private val refreshHttpClient: OkHttpClient by lazy {
        val builder =
            OkHttpClient.Builder()
                .addInterceptor(refreshTokenInterceptor)
                .addInterceptor(loggingInterceptor)
        builder.build()
    }

    private val authTokenInterceptor: Interceptor by lazy {
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

    companion object {
        private val BASE_URL = if (BuildConfig.DEBUG) BuildConfig.BASE_DEV_URL else BuildConfig.BASE_PROD_URL
    }
}
