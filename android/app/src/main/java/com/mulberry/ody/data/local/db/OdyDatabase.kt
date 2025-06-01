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

@Database(
    entities = [MateEtaInfoEntity::class],
    version = 5,
)
@TypeConverters(MateEtaListTypeConverter::class)
abstract class OdyDatabase : RoomDatabase() {
    abstract fun mateEtaInfoDao(): MateEtaInfoDao

    companion object {
        private const val DATABASE_NAME = "ody_db"

        private val MIGRATION_4_TO_5 =
            object : Migration(4, 5) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("DROP TABLE IF EXISTS eta_reservation")
                }
            }

        fun create(context: Context): OdyDatabase {
            return Room.databaseBuilder(context, OdyDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_4_TO_5)
                .addTypeConverter(MateEtaListTypeConverter())
                .build()
        }
    }
}
