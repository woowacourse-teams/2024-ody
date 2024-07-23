package com.woowacourse.ody.data.remote.location

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.woowacourse.ody.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object KakaoRetrofitClient {
    private const val BASE_URL = "https://dapi.kakao.com/"
    private val interceptor: Interceptor by lazy {
        Interceptor {
            val newRequest =
                it.request().newBuilder()
                    .addHeader("Authorization", "KakaoAK ${BuildConfig.KAKAO_API_KEY}").build()
            it.proceed(newRequest)
        }
    }
    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder().addInterceptor(interceptor)
        builder.build()
    }
    private val moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}
