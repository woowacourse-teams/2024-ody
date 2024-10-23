package com.mulberry.ody.data.local.entity.eta

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.mulberry.ody.domain.model.EtaStatus
import com.mulberry.ody.domain.model.MateEta

@ProvidedTypeConverter
class MateEtaListTypeConverter {
    @TypeConverter
    fun fromString(value: String): List<MateEta>? {
        return value.split(";").map { fromJson(it) }
    }

    @TypeConverter
    fun fromMateEta(type: List<MateEta>): String {
        return type.joinToString(";") { toJson(it) }
    }

    private fun toJson(mateEta: MateEta): String {
        val etaStatusString =
            when (mateEta.etaStatus) {
                is EtaStatus.Arrived -> """{type:"Arrived"}"""
                is EtaStatus.ArrivalSoon -> """{"type":"ArrivalSoon"/"durationMinutes":${mateEta.etaStatus.durationMinutes}}"""
                is EtaStatus.LateWarning -> """{"type":"LateWarning"/"durationMinutes":${mateEta.etaStatus.durationMinutes}}"""
                is EtaStatus.Late -> """{"type":"Late"/"durationMinutes":${mateEta.etaStatus.durationMinutes}}"""
                is EtaStatus.Missing -> """{"type":"Missing"}"""
            }

        return """{"mateId":${mateEta.mateId},"nickname":"${mateEta.nickname}","etaStatus":$etaStatusString}"""
    }

    private fun fromJson(json: String): MateEta {
        val cleanedJson = json.trim().removePrefix("{").removeSuffix("}")
        val jsonObject =
            cleanedJson.split(",").map { it.trim() }
                .associate {
                    val (key, value) =
                        it.split(":", limit = 2)
                            .map { it.trim().removeSurrounding("\"") }
                    key to value
                }

        val mateId = jsonObject["mateId"]!!.toLong()
        val nickname = jsonObject["nickname"]!!
        val etaStatusJson = jsonObject["etaStatus"]!!.trim()
        val etaStatusMap =
            etaStatusJson.removePrefix("{").removeSuffix("}").split("/")
                .map { it.trim() }
                .associate {
                    val (key, value) = it.split(":", limit = 2).map { it.trim() }
                    key.removeSurrounding("\"") to value.trim().removeSurrounding("\"")
                }

        val etaStatusType =
            etaStatusMap["type"] ?: throw IllegalArgumentException("Missing etaStatus type")
        val etaStatus =
            when (etaStatusType) {
                "Arrived" -> EtaStatus.Arrived
                "ArrivalSoon" -> {
                    val durationMinutes = etaStatusMap["durationMinutes"]?.toInt()
                    EtaStatus.ArrivalSoon(durationMinutes ?: throw IllegalArgumentException("Missing durationMinutes"))
                }

                "LateWarning" -> {
                    val durationMinutes = etaStatusMap["durationMinutes"]?.toInt()
                    EtaStatus.LateWarning(durationMinutes ?: throw IllegalArgumentException("Missing durationMinutes"))
                }

                "Late" -> {
                    val durationMinutes = etaStatusMap["durationMinutes"]?.toInt()
                    EtaStatus.Late(durationMinutes ?: throw IllegalArgumentException("Missing durationMinutes"))
                }

                "Missing" -> EtaStatus.Missing
                else -> throw IllegalArgumentException("Unknown etaStatus type: $etaStatusType")
            }

        return MateEta(mateId, nickname, etaStatus)
    }
}
