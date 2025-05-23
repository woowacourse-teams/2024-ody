package com.mulberry.ody.di

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.data.local.service.EtaDashboard
import com.mulberry.ody.data.local.service.EtaDashboardNotification
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
        odyDatastore: OdyDatastore,
        notificationManager: NotificationManager,
    ): FCMNotification {
        return FCMNotification(context, odyDatastore, notificationManager)
    }

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
    ): NotificationManager {
        return context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
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
    fun provideEtaDashboardNotification(
        @ApplicationContext context: Context,
        notificationManager: NotificationManager,
    ): EtaDashboardNotification {
        return EtaDashboardNotification(context, notificationManager)
    }

    @Provides
    @Singleton
    fun provideEtaDashboard(
        @ApplicationContext context: Context,
        odyDatastore: OdyDatastore,
    ): EtaDashboard {
        return EtaDashboard(context, odyDatastore)
    }
}
