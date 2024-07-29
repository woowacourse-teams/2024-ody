package com.woowacourse.ody.data.remote.core.entity.meeting.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.woowacourse.ody.domain.model.Mate

@JsonClass(generateAdapter = true)
data class MateResponse(
    @Json(name = "nickname")
    val nickname: String,
) {
    fun toMate(): Mate = Mate(nickname = nickname)
}
