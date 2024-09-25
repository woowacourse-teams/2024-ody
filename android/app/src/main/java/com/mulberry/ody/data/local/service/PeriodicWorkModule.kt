package com.mulberry.ody.data.local.service

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PeriodicWorkModule {
    @Provides
    @Singleton
    fun providePeriodicWork(): PeriodicWork {
        return TestPeriodicWork()
    }
}
