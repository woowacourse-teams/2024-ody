package com.woowacourse.ody.data.remote

import com.google.firebase.messaging.FirebaseMessaging
import com.woowacourse.ody.BuildConfig
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    private val BASE_URL = BuildConfig.ServerUrl
    private val token: String by lazy {
        runBlocking {
            FirebaseMessaging.getInstance().token.await()
        }
    }
    private val interceptor: Interceptor by lazy {
        Interceptor {
            val newRequest = it.request().newBuilder().addHeader("Authorization", "token=$token").build()
            it.proceed(newRequest)
        }
    }
    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder().addInterceptor(interceptor)
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
