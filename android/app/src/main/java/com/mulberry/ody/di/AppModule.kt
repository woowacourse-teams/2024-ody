package com.mulberry.ody.di

import android.content.Context
import com.mulberry.ody.data.local.service.EtaDashboardAlarm
import com.mulberry.ody.data.local.service.EtaDashboardNotification
import com.mulberry.ody.presentation.common.PermissionHelper
import com.mulberry.ody.presentation.common.gps.GeoLocationHelper
import com.mulberry.ody.presentation.common.gps.LocationHelper
import com.mulberry.ody.presentation.notification.FCMNotification
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFCMNotification(
        @ApplicationContext context: Context,
    ): FCMNotification {
        return FCMNotification(context)
    }

    @Provides
    @Singleton
    fun providePermissionHelper(
        @ApplicationContext context: Context,
    ): PermissionHelper {
        return PermissionHelper(context)
    }

    @Provides
    @Singleton
    fun provideGpsHelper(
        @ApplicationContext context: Context,
    ): LocationHelper {
        return GeoLocationHelper(context)
    }

    @Provides
    @Singleton
    fun provideEtaDashboardAlarm(
        @ApplicationContext context: Context,
    ): EtaDashboardAlarm {
        return EtaDashboardAlarm(context)
    }

    @Provides
    @Singleton
    fun provideEtaDashboardNotification(
        @ApplicationContext context: Context,
    ): EtaDashboardNotification {
        return EtaDashboardNotification(context)
    }
}
