package com.mulberry.ody.data.remote.thirdparty.address.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Meta(
    @Json(name = "is_end")
    val isEnd: Boolean,
    @Json(name = "pageable_count")
    val pageableCount: Int,
    @Json(name = "same_name")
    val sameName: SameName,
    @Json(name = "total_count")
    val totalCount: Int,
)
