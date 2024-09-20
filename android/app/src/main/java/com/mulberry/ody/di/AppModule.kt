package com.mulberry.ody.di

import android.app.Application
import androidx.work.WorkManager
import com.mulberry.ody.presentation.common.PermissionHelper
import com.mulberry.ody.presentation.notification.NotificationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideWorkerManager(application: Application): WorkManager {
        return WorkManager.getInstance(application)
    }

    @Provides
    @Singleton
    fun provideNotificationHelper(application: Application): NotificationHelper {
        return NotificationHelper(application)
    }

    @Provides
    @Singleton
    fun providePermissionHelper(application: Application): PermissionHelper {
        return PermissionHelper(application)
    }
}
