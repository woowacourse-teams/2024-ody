package com.mulberry.ody.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mulberry.ody.data.local.entity.reserve.EtaReservationEntity

@Dao
interface EtaReservationDao {
    @Query("SELECT * FROM eta_reservation")
    suspend fun fetchAll(): List<EtaReservationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(etaReservationEntity: EtaReservationEntity): Long

    @Query("DELETE FROM eta_reservation WHERE meetingId = :meetingId")
    suspend fun delete(meetingId: Long)

    @Query("DELETE FROM eta_reservation")
    suspend fun deleteAll()
}
