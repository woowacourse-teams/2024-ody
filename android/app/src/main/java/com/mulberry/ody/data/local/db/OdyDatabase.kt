package com.mulberry.ody.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.data.local.entity.eta.MateEtaListTypeConverter
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
        private val MIGRATION_3_TO_4 =
            object : Migration(3, 4) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `eta_reservation` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `meetingId` INTEGER NOT NULL,
                            `reserveMillis` INTEGER NOT NULL,
                            `isOpen` INTEGER NOT NULL
                        )
                        """.trimIndent(),
                    )
                }
            }

        fun create(context: Context): OdyDatabase {
            return Room.databaseBuilder(context, OdyDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_3_TO_4)
                .addTypeConverter(MateEtaListTypeConverter())
                .build()
        }
    }
}
