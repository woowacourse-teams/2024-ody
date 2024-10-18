package com.mulberry.ody.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mulberry.ody.data.local.entity.reserve.EtaReserveEntity

@Dao
interface EtaReserveDao {
    @Query("SELECT * FROM eta_reserve")
    suspend fun fetchAll(): List<EtaReserveEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(etaReserveEntity: EtaReserveEntity): Long

    @Query("DELETE FROM eta_reserve WHERE id = :id")
    suspend fun delete(id: Long)
}
