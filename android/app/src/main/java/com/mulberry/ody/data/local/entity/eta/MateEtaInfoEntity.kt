package com.mulberry.ody.data.local.entity.eta

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mulberry.ody.domain.model.MateEta
import kotlinx.serialization.Serializable

@Entity(tableName = "eta_info")
@Serializable
data class MateEtaInfoEntity(
    @PrimaryKey
    val meetingId: Long,
    val mateId: Long,
    val mateEtas: List<MateEta>,
)
