package com.mulberry.ody.data.local.entity.eta.migration

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

class OldMateEtaListTypeConverter
    @Inject
    constructor(
        private val moshi: Moshi,
    ) {
        fun fromString(value: String): List<OldMateEta>? {
            val listType = Types.newParameterizedType(List::class.java, OldMateEta::class.java)
            val adapter: JsonAdapter<List<OldMateEta>> = moshi.adapter(listType)
            return adapter.fromJson(value)
        }
    }
