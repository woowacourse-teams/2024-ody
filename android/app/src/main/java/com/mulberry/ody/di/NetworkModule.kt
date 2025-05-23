package com.mulberry.ody.di

import com.mulberry.ody.BuildConfig
import com.mulberry.ody.data.local.db.OdyDataStore
import com.mulberry.ody.data.remote.core.service.RefreshTokenService
import com.mulberry.ody.data.remote.retrofit.AccessTokenInterceptor
import com.mulberry.ody.data.remote.retrofit.ApiResultCallAdapter
import com.mulberry.ody.data.remote.retrofit.RefreshTokenInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LoginRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaohHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthTokenInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRefreshTokenInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LoggingInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoInterceptor

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private val BASE_URL =
        if (BuildConfig.DEBUG) BuildConfig.BASE_DEV_URL else BuildConfig.BASE_PROD_URL

    @Provides
    @Singleton
    @DefaultRetrofit
    fun provideDefaultRetrofit(
        @AuthHttpClient httpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(ApiResultCallAdapter.Factory())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @KakaoRetrofit
    fun provideKakaoRetrofit(
        @KakaohHttpClient httpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl("https://dapi.kakao.com/")
            .addCallAdapterFactory(ApiResultCallAdapter.Factory())
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build(),
                ),
            ).build()
    }

    @Provides
    @Singleton
    @RefreshRetrofit
    fun provideRefreshRetrofit(
        @RefreshHttpClient httpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(ApiResultCallAdapter.Factory())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @LoginRetrofit
    fun provideLoginRetrofit(
        @DefaultOkHttpClient httpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(ApiResultCallAdapter.Factory())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @AuthHttpClient
    fun provideAuthHttpClient(
        @AuthTokenInterceptor interceptor: Interceptor,
        @LoggingInterceptor httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        val builder =
            OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(httpLoggingInterceptor)
        return builder.build()
    }

    @Provides
    @Singleton
    @RefreshHttpClient
    fun provideRefreshHttpClient(
        @AuthRefreshTokenInterceptor interceptor: Interceptor,
        @LoggingInterceptor httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        val builder =
            OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(httpLoggingInterceptor)
        return builder.build()
    }

    @Provides
    @Singleton
    @KakaohHttpClient
    fun provideKakaoHttpClient(
        @KakaoInterceptor interceptor: Interceptor,
    ): OkHttpClient {
        val builder =
            OkHttpClient.Builder()
                .addInterceptor(interceptor)
        return builder.build()
    }

    @Provides
    @Singleton
    @DefaultOkHttpClient
    fun provideOkHttpClient(
        @LoggingInterceptor httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        val builder =
            OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
        return builder.build()
    }

    @Provides
    @Singleton
    @AuthTokenInterceptor
    fun provideAuthTokenInterceptor(
        odyDataStore: OdyDataStore,
        refreshTokenService: RefreshTokenService,
    ): Interceptor {
        return AccessTokenInterceptor(odyDataStore, refreshTokenService)
    }

    @Provides
    @Singleton
    @AuthRefreshTokenInterceptor
    fun provideRefreshTokenInterceptor(odyDataStore: OdyDataStore): Interceptor {
        return RefreshTokenInterceptor(odyDataStore)
    }

    @Provides
    @Singleton
    @LoggingInterceptor
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logger =
            HttpLoggingInterceptor.Logger { message ->
                Timber.tag("OkHttp").d(message)
            }
        return HttpLoggingInterceptor(logger).setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    @KakaoInterceptor
    fun provideKakaoInterceptor(): Interceptor {
        return Interceptor {
            val newRequest =
                it.request().newBuilder()
                    .addHeader("Authorization", "KakaoAK ${BuildConfig.KAKAO_API_KEY}").build()
            it.proceed(newRequest)
        }
    }
}
