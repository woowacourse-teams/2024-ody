package com.mulberry.ody.di

import com.mulberry.ody.data.remote.core.service.AuthService
import com.mulberry.ody.data.remote.core.service.JoinService
import com.mulberry.ody.data.remote.core.service.LoginService
import com.mulberry.ody.data.remote.core.service.MeetingService
import com.mulberry.ody.data.remote.core.service.NotificationService
import com.mulberry.ody.data.remote.core.service.RefreshTokenService
import com.mulberry.ody.data.remote.thirdparty.address.KakaoAddressService
import com.mulberry.ody.data.remote.thirdparty.login.kakao.KakaoOAuthLoginService
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideKakaoAddressService(
        @KakaoRetrofit retrofit: Retrofit,
    ): KakaoAddressService {
        return retrofit.create(KakaoAddressService::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginService(
        @LoginRetrofit retrofit: Retrofit,
    ): LoginService {
        return retrofit.create(LoginService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthService(
        @DefaultRetrofit retrofit: Retrofit,
    ): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideRefreshTokenService(
        @RefreshRetrofit retrofit: Retrofit,
    ): RefreshTokenService {
        return Lazy { retrofit.create(RefreshTokenService::class.java) }.get()
    }

    @Provides
    @Singleton
    fun provideJoinService(
        @DefaultRetrofit retrofit: Retrofit,
    ): JoinService {
        return retrofit.create(JoinService::class.java)
    }

    @Provides
    @Singleton
    fun provideMeetingService(
        @DefaultRetrofit retrofit: Retrofit,
    ): MeetingService {
        return retrofit.create(MeetingService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationService(
        @DefaultRetrofit retrofit: Retrofit,
    ): NotificationService {
        return retrofit.create(NotificationService::class.java)
    }

    @Provides
    @Singleton
    fun provideKakaoOAuthLoginService(): KakaoOAuthLoginService {
        return KakaoOAuthLoginService()
    }
}
