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
import com.mulberry.ody.data.local.entity.eta.OldMateEtaListTypeConverter
import com.mulberry.ody.data.local.entity.reserve.EtaReservationEntity
import com.mulberry.ody.domain.model.EtaStatus
import com.mulberry.ody.domain.model.EtaType2
import com.mulberry.ody.domain.model.MateEta
import com.mulberry.ody.domain.model.MateEta2

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
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `eta_info_temp` (
                            `meetingId` INTEGER PRIMARY KEY NOT NULL, 
                            `mateId` INTEGER NOT NULL, 
                            `mateEtas` TEXT NOT NULL
                        )
                        """.trimIndent(),
                    )

                    val cursor = db.query("SELECT * FROM eta_info")
                    if (cursor.moveToFirst()) {
                        do {
                            val meetingId = cursor.getLong(cursor.getColumnIndexOrThrow("meetingId"))
                            val mateId = cursor.getLong(cursor.getColumnIndexOrThrow("mateId"))
                            val mateEtasJson = cursor.getString(cursor.getColumnIndexOrThrow("mateEtas"))

                            val oldMateEtas: List<MateEta2> = OldMateEtaListTypeConverter().fromString(mateEtasJson) ?: emptyList()
                            val newMateEtas =
                                oldMateEtas.map { oldMateEta ->
                                    MateEta(
                                        mateId = oldMateEta.mateId,
                                        nickname = oldMateEta.nickname,
                                        etaStatus =
                                            when (oldMateEta.etaType) {
                                                EtaType2.ARRIVED -> EtaStatus.Arrived
                                                EtaType2.ARRIVAL_SOON -> EtaStatus.ArrivalSoon(oldMateEta.durationMinute)
                                                EtaType2.LATE_WARNING -> EtaStatus.LateWarning(oldMateEta.durationMinute)
                                                EtaType2.LATE -> EtaStatus.Late(oldMateEta.durationMinute)
                                                EtaType2.MISSING -> EtaStatus.Missing
                                            },
                                    )
                                }

                            val newMateEtasJson = MateEtaListTypeConverter().fromMateEta(newMateEtas)

                            db.execSQL(
                                "INSERT INTO eta_info_temp (meetingId, mateId, mateEtas) VALUES (?, ?, ?)",
                                arrayOf(meetingId, mateId, newMateEtasJson),
                            )
                        } while (cursor.moveToNext())
                    }

                    cursor.close()
                    db.execSQL("DROP TABLE eta_info")
                    db.execSQL("ALTER TABLE eta_info_temp RENAME TO eta_info")
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
