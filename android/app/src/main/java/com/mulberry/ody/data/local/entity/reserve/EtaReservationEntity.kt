package com.mulberry.ody.data.local.entity.reserve

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "eta_reservation")
@JsonClass(generateAdapter = true)
data class EtaReservationEntity(
    val meetingId: Long,
    val reserveMillis: Long,
    val isOpen: Boolean,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
