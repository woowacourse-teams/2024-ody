package com.mulberry.ody.di

import android.app.Application
import com.mulberry.ody.data.remote.thirdparty.image.FirebaseImageStorage
import com.mulberry.ody.domain.repository.image.ImageStorage
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.FirebaseAnalyticsHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideAnalyticsHelper(application: Application): AnalyticsHelper {
        return FirebaseAnalyticsHelper(application)
    }

    @Provides
    @Singleton
    fun provideImageStorage(): ImageStorage {
        return FirebaseImageStorage()
    }
}
