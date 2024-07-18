package com.woowacourse.ody.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null
    private const val BASE_URL = ""

    fun getRetrofit(): Retrofit {
        val builder = OkHttpClient.Builder()
        val okHttpClient = builder.build()

        return retrofit ?: Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}
