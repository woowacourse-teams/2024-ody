package com.mulberry.ody.data.local.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface MateEtaInfoDao {
    @Upsert
    suspend fun upsert(mateEtaInfo: MateEtaInfoEntity)

    @Query("SELECT * FROM eta_info WHERE meetingId = :meetingId")
    fun getMateEtaInfo(meetingId: Long): Flow<MateEtaInfoEntity?>

    @Query("DELETE FROM eta_info WHERE meetingId = :meetingId")
    suspend fun delete(meetingId: Long)

    @Query("DELETE FROM eta_info")
    suspend fun deleteAll()
}
