package com.woowacourse.ody.domain.repository.ody

import com.woowacourse.ody.domain.model.MeetingJoinInfo
import com.woowacourse.ody.domain.model.ReserveInfo

interface JoinRepository {
    suspend fun postMates(meetingJoinInfo: MeetingJoinInfo): Result<ReserveInfo>
}
