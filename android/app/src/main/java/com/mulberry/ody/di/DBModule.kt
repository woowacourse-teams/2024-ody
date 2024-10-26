package com.mulberry.ody.di

import android.content.Context
import com.mulberry.ody.data.local.db.EtaReservationDao
import com.mulberry.ody.data.local.db.MateEtaInfoDao
import com.mulberry.ody.data.local.db.OdyDatabase
import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.data.local.entity.eta.migration.OldMateEtaListTypeConverter
import com.squareup.moshi.Moshi
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
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): OdyDatastore {
        return OdyDatastore(context)
    }

    @Provides
    @Singleton
    fun provideOdyDatabase(
        @ApplicationContext context: Context,
    ): OdyDatabase {
        return OdyDatabase.create(context)
    }

    @Provides
    fun provideMateEtaInfoDao(appDatabase: OdyDatabase): MateEtaInfoDao = appDatabase.mateEtaInfoDao()

    @Provides
    fun provideEtaReserveDao(appDatabase: OdyDatabase): EtaReservationDao = appDatabase.etaReservationDao()

    @Provides
    fun provideOldMateEtaListTypeConverter(moshi: Moshi): OldMateEtaListTypeConverter {
        return OldMateEtaListTypeConverter(moshi)
    }
}
