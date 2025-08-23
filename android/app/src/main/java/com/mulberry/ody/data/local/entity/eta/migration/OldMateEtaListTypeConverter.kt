package com.mulberry.ody.data.local.entity.eta.migration

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class OldMateEtaListTypeConverter(
    private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build(),
) {
    fun fromJson(value: String): List<OldMateEta>? {
        val listType = Types.newParameterizedType(List::class.java, OldMateEta::class.java)
        val adapter: JsonAdapter<List<OldMateEta>> = moshi.adapter(listType)
        return adapter.fromJson(value)
    }
}
