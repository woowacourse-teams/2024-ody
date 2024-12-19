package com.mulberry.ody.di

import com.mulberry.ody.data.local.repository.DefaultAuthTokenRepository
import com.mulberry.ody.data.local.repository.DefaultMatesEtaRepository
import com.mulberry.ody.data.remote.core.repository.DefaultFCMTokenRepository
import com.mulberry.ody.data.remote.core.repository.DefaultJoinRepository
import com.mulberry.ody.data.remote.core.repository.DefaultMeetingRepository
import com.mulberry.ody.data.remote.core.repository.DefaultNotificationLogRepository
import com.mulberry.ody.data.remote.thirdparty.address.KakaoAddressRepository
import com.mulberry.ody.data.auth.KakaoLoginRepository
import com.mulberry.ody.domain.repository.location.AddressRepository
import com.mulberry.ody.domain.repository.ody.AuthTokenRepository
import com.mulberry.ody.domain.repository.ody.FCMTokenRepository
import com.mulberry.ody.domain.repository.ody.JoinRepository
import com.mulberry.ody.domain.repository.ody.LoginRepository
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.domain.repository.ody.NotificationLogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindJoinRepository(defaultJoinRepository: DefaultJoinRepository): JoinRepository

    @Binds
    @Singleton
    fun bindMeetingRepository(defaultMeetingRepository: DefaultMeetingRepository): MeetingRepository

    @Binds
    @Singleton
    fun bindFCMTokenRepository(defaultFCMTokenRepository: DefaultFCMTokenRepository): FCMTokenRepository

    @Binds
    @Singleton
    fun bindEtaRepository(defaultMatesEtaRepository: DefaultMatesEtaRepository): MatesEtaRepository

    @Binds
    @Singleton
    fun bindNotificationLogRepository(defaultNotificationLogRepository: DefaultNotificationLogRepository): NotificationLogRepository

    @Binds
    @Singleton
    fun bindKakaoLoginRepository(kakaoLoginRepository: KakaoLoginRepository): LoginRepository

    @Binds
    @Singleton
    fun bindAddressRepository(kakaoGeoLocationRepository: KakaoAddressRepository): AddressRepository

    @Binds
    @Singleton
    fun bindAuthTokenRepository(defaultAuthTokenRepository: DefaultAuthTokenRepository): AuthTokenRepository
}
