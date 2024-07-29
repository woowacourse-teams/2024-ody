package com.woowacourse.ody.data.remote.core

import com.google.firebase.messaging.FirebaseMessaging
import com.woowacourse.ody.BuildConfig
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

object RetrofitClient {
    private const val BASE_URL = BuildConfig.BASE_URL
    private val token: String by lazy {
        runBlocking {
            FirebaseMessaging.getInstance().token.await()
        }
    }
    private val interceptor: Interceptor by lazy {
        Interceptor {
            val newRequest = it.request().newBuilder().addHeader("Authorization", "Bearer device-token=$token").build()
            it.proceed(newRequest)
        }
    }

    private val logging by lazy {
        val logger =
            HttpLoggingInterceptor.Logger { message ->
                Timber.tag("OkHttp").d(message)
            }
        HttpLoggingInterceptor(logger).setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(logging)
        builder.build()
    }
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}
