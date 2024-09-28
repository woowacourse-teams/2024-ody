package com.mulberry.ody.di

import android.content.Context
import androidx.room.Room
import com.mulberry.ody.data.local.db.OdyDatabase
import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.data.local.entity.eta.MateEtaListTypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {
    @Provides
    @Singleton
    fun provideMoshi() = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): OdyDatastore {
        return OdyDatastore(context)
    }

    @Provides
    @Singleton
    fun provideOdyDatabase(
        @ApplicationContext context: Context,
        moshi: Moshi,
    ) = Room.databaseBuilder(
        context,
        OdyDatabase::class.java,
        "ody_db",
    ).fallbackToDestructiveMigration()
        .addTypeConverter(MateEtaListTypeConverter(moshi))
        .build()

    @Provides
    fun providesMateEtaInfoDao(appDatabase: OdyDatabase) = appDatabase.mateEtaInfoDao()
}
