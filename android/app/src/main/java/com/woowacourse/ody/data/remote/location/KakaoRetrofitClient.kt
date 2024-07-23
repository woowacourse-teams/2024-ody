package com.woowacourse.ody.data.remote.location

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object KakaoRetrofitClient {
    private const val BASE_URL = "https://dapi.kakao.com/"
    private val moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}
