package com.mulberry.ody.di

import android.content.Context
import com.mulberry.ody.data.remote.thirdparty.image.FirebaseImageStorage
import com.mulberry.ody.domain.repository.image.ImageStorage
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.FirebaseAnalyticsHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideAnalyticsHelper(
        @ApplicationContext context: Context,
    ): AnalyticsHelper {
        return FirebaseAnalyticsHelper(context)
    }

    @Provides
    @Singleton
    fun provideImageStorage(): ImageStorage {
        return FirebaseImageStorage()
    }
}
