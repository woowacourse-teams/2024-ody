package com.mulberry.ody.data.local.entity.eta

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.mulberry.ody.domain.model.MateEta
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
class MateEtaListTypeConverter {
    private val json =
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }

    @TypeConverter
    fun fromMateEtaList(value: List<MateEta>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toMateEtaList(value: String): List<MateEta> {
        return json.decodeFromString(value)
    }
}
