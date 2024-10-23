package com.mulberry.ody.data.local.entity.eta

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.mulberry.ody.domain.model.MateEta2
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@ProvidedTypeConverter
class OldMateEtaListTypeConverter(
    private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build(),
) {
    @TypeConverter
    fun fromString(value: String): List<MateEta2>? {
        val listType = Types.newParameterizedType(List::class.java, MateEta2::class.java)
        val adapter: JsonAdapter<List<MateEta2>> = moshi.adapter(listType)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun fromMateEta(type: List<MateEta2>): String {
        val listType = Types.newParameterizedType(List::class.java, MateEta2::class.java)
        val adapter: JsonAdapter<List<MateEta2>> = moshi.adapter(listType)
        return adapter.toJson(type)
    }
}
