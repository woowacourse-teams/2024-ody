package com.mulberry.ody.di

import android.content.Context
import androidx.room.Room
import com.mulberry.ody.data.local.db.EtaReserveDao
import com.mulberry.ody.data.local.db.MateEtaInfoDao
import com.mulberry.ody.data.local.db.OdyDatabase
import com.mulberry.ody.data.local.db.OdyDatabase.Companion.MIGRATION_3_4
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
    fun provideMoshi(): Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

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
    ): OdyDatabase {
        return Room.databaseBuilder(context, OdyDatabase::class.java, "ody_db")
            .addMigrations(MIGRATION_3_4)
            .addTypeConverter(MateEtaListTypeConverter(moshi))
            .build()
    }

    @Provides
    fun provideMateEtaInfoDao(appDatabase: OdyDatabase): MateEtaInfoDao =
        appDatabase.mateEtaInfoDao()

    @Provides
    fun provideEtaReserveDao(appDatabase: OdyDatabase): EtaReserveDao = appDatabase.etaReserveDao()
}
