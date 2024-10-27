package com.mulberry.ody.data.local.entity.eta.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mulberry.ody.data.local.entity.eta.MateEtaListTypeConverter
import com.mulberry.ody.domain.model.EtaStatus
import com.mulberry.ody.domain.model.MateEta

class DatabaseMigration3To4 : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.migrateEtaInfo()
        db.migrateEtaReservation()
    }

    private fun SupportSQLiteDatabase.migrateEtaInfo() {
        execSQL(CREATE_ETA_INFO_TEMP_QUERY)

        val cursor = query(SELECT_ALL_ETA_INFO_QUERY)
        if (cursor.moveToFirst()) {
            do {
                val meetingId = cursor.getLong(cursor.getColumnIndexOrThrow("meetingId"))
                val mateId = cursor.getLong(cursor.getColumnIndexOrThrow("mateId"))
                val mateEtasJson = cursor.getString(cursor.getColumnIndexOrThrow("mateEtas"))

                val oldMateEtas: List<OldMateEta> =
                    OldMateEtaListTypeConverter().fromString(mateEtasJson) ?: emptyList()
                val newMateEtas = oldMateEtas.toMateEtas()
                val newMateEtasJson = MateEtaListTypeConverter().fromMateEta(newMateEtas)

                execSQL(INSERT_ETA_INFO_TEMP_QUERY, arrayOf(meetingId, mateId, newMateEtasJson))
            } while (cursor.moveToNext())
        }

        cursor.close()
        execSQL(DROP_ETA_INFO)
        execSQL(RENAME_ETA_INFO_TEMP)
    }

    private fun List<OldMateEta>.toMateEtas(): List<MateEta> {
        return map { oldMateEta ->
            MateEta(
                mateId = oldMateEta.mateId,
                nickname = oldMateEta.nickname,
                etaStatus =
                when (oldMateEta.etaType) {
                    EtaType.ARRIVED -> EtaStatus.Arrived
                    EtaType.ARRIVAL_SOON -> EtaStatus.ArrivalSoon(oldMateEta.durationMinute)
                    EtaType.LATE_WARNING -> EtaStatus.LateWarning(oldMateEta.durationMinute)
                    EtaType.LATE -> EtaStatus.Late(oldMateEta.durationMinute)
                    EtaType.MISSING -> EtaStatus.Missing
                },
            )
        }
    }

    private fun SupportSQLiteDatabase.migrateEtaReservation() {
        execSQL(CREATE_ETA_RESERVATION_QUERY.trimIndent())
    }

    companion object {
        private const val CREATE_ETA_RESERVATION_QUERY =
            """
            CREATE TABLE IF NOT EXISTS `eta_reservation` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `meetingId` INTEGER NOT NULL,
                 `reserveMillis` INTEGER NOT NULL,
                 `isOpen` INTEGER NOT NULL
            )
            """
        private const val CREATE_ETA_INFO_TEMP_QUERY =
            """
            CREATE TABLE IF NOT EXISTS `eta_info_temp` (
                `meetingId` INTEGER PRIMARY KEY NOT NULL, 
                `mateId` INTEGER NOT NULL, 
                `mateEtas` TEXT NOT NULL
            )
            """
        private const val SELECT_ALL_ETA_INFO_QUERY = "SELECT * FROM eta_info"
        private const val INSERT_ETA_INFO_TEMP_QUERY =
            "INSERT INTO eta_info_temp (meetingId, mateId, mateEtas) VALUES (?, ?, ?)"
        private const val DROP_ETA_INFO = "DROP TABLE eta_info"
        private const val RENAME_ETA_INFO_TEMP = "ALTER TABLE eta_info_temp RENAME TO eta_info"
    }
}
