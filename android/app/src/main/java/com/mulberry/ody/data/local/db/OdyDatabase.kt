package com.mulberry.ody.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.data.local.entity.eta.MateEtaListTypeConverter
import com.mulberry.ody.data.local.entity.eta.migration.OdyDatabaseMigration
import com.mulberry.ody.data.local.entity.reserve.EtaReservationEntity

@Database(
    entities = [MateEtaInfoEntity::class, EtaReservationEntity::class],
    version = 4,
)
@TypeConverters(MateEtaListTypeConverter::class)
abstract class OdyDatabase : RoomDatabase() {
    abstract fun mateEtaInfoDao(): MateEtaInfoDao

    abstract fun etaReservationDao(): EtaReservationDao

    companion object {
        private const val DATABASE_NAME = "ody_db"

        fun create(context: Context): OdyDatabase {
            return Room.databaseBuilder(context, OdyDatabase::class.java, DATABASE_NAME)
                .addMigrations(OdyDatabaseMigration(4, 5))
                .addTypeConverter(MateEtaListTypeConverter())
                .build()
        }
    }
}
