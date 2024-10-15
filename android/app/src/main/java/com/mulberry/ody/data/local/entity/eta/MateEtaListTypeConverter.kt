package com.mulberry.ody.data.local.entity.eta

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.mulberry.ody.domain.model.MateEta
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@ProvidedTypeConverter
class MateEtaListTypeConverter(
    private val moshi: Moshi,
) {
    @TypeConverter
    fun fromString(value: String): List<MateEta>? {
        val listType = Types.newParameterizedType(List::class.java, MateEta::class.java)
        val adapter: JsonAdapter<List<MateEta>> = moshi.adapter(listType)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun fromMateEta(type: List<MateEta>): String {
        val listType = Types.newParameterizedType(List::class.java, MateEta::class.java)
        val adapter: JsonAdapter<List<MateEta>> = moshi.adapter(listType)
        return adapter.toJson(type)
    }
}
