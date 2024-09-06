package com.ydo.ody.data.remote.thirdparty.location.entity.response

import com.squareup.moshi.Json

data class Meta(
    @Json(name = "is_end") val isEnd: Boolean,
    @Json(name = "pageable_count") val pageableCount: Int,
    @Json(name = "total_count") val totalCount: Int,
)
