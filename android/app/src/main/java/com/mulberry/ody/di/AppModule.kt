package com.mulberry.ody.di

import android.content.Context
import androidx.work.WorkManager
import com.mulberry.ody.presentation.common.PermissionHelper
import com.mulberry.ody.presentation.common.gps.GpsHelper
import com.mulberry.ody.presentation.notification.NotificationHelper
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
    fun provideWorkManager(
        @ApplicationContext context: Context,
    ): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideNotificationHelper(
        @ApplicationContext context: Context,
    ): NotificationHelper {
        return NotificationHelper(context)
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
    ): GpsHelper {
        return GpsHelper(context)
    }
}
