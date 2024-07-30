package com.woowacourse.ody.data.remote.core.entity.meeting.response

import com.woowacourse.ody.domain.model.Mate

fun MateResponse.toMate(): Mate = Mate(nickname = nickname)
