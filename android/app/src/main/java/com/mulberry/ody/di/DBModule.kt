package com.mulberry.ody.di

import android.app.Application
import com.mulberry.ody.data.local.db.OdyDatastore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {
    @Provides
    @Singleton
    fun provideDataStore(application: Application): OdyDatastore {
        return OdyDatastore(application)
    }
}
