package com.mulberry.ody.di

import com.mulberry.ody.data.auth.repository.KakaoAuthRepository
import com.mulberry.ody.data.repository.DefaultJoinRepository
import com.mulberry.ody.data.repository.DefaultMatesEtaRepository
import com.mulberry.ody.data.repository.DefaultMeetingRepository
import com.mulberry.ody.data.repository.DefaultNotificationLogRepository
import com.mulberry.ody.data.repository.KakaoAddressRepository
import com.mulberry.ody.domain.repository.location.AddressRepository
import com.mulberry.ody.domain.repository.ody.AuthRepository
import com.mulberry.ody.domain.repository.ody.JoinRepository
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
    fun bindEtaRepository(defaultMatesEtaRepository: DefaultMatesEtaRepository): MatesEtaRepository

    @Binds
    @Singleton
    fun bindNotificationLogRepository(defaultNotificationLogRepository: DefaultNotificationLogRepository): NotificationLogRepository

    @Binds
    @Singleton
    fun bindKakaoLoginRepository(kakaoLoginRepository: KakaoAuthRepository): AuthRepository

    @Binds
    @Singleton
    fun bindAddressRepository(kakaoGeoLocationRepository: KakaoAddressRepository): AddressRepository
}
