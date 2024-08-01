package com.woowacourse.ody.domain.repository.ody

import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.MeetingJoinInfo

interface JoinRepository {
    suspend fun postMates(meetingJoinInfo: MeetingJoinInfo): Result<Meeting>
}
