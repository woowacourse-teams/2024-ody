package com.mulberry.ody.data.local.entity.eta

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mulberry.ody.domain.model.MateEta
import com.squareup.moshi.JsonClass

@Entity(tableName = "eta_info")
@JsonClass(generateAdapter = true)
data class MateEtaInfoEntity(
    @PrimaryKey
    val meetingId: Long,
    val mateId: Long,
    val mateEtas: List<MateEta>,
)
