package com.mulberry.ody.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.data.local.entity.eta.MateEtaListTypeConverter
import com.mulberry.ody.data.local.entity.reserve.EtaReserveEntity

@Database(
    entities = [MateEtaInfoEntity::class, EtaReserveEntity::class],
    version = 4,
)
@TypeConverters(MateEtaListTypeConverter::class)
abstract class OdyDatabase : RoomDatabase() {
    abstract fun mateEtaInfoDao(): MateEtaInfoDao

    abstract fun etaReserveDao(): EtaReserveDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `eta_reserve` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `meetingId` INTEGER NOT NULL,
                `reserveMillis` INTEGER NOT NULL,
                `isOpen` INTEGER NOT NULL
            )
            """.trimIndent()
                )
            }
        }
    }
}
