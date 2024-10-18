package com.mulberry.ody.data.local.entity.reserve

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "eta_reserve")
@JsonClass(generateAdapter = true)
data class EtaReserveEntity(
    val meetingId: Long,
    val reserveMillis: Long,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 1L
)
