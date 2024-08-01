package com.woowacourse.ody.data.remote.core.repository

import com.woowacourse.ody.data.remote.core.entity.join.request.toJoinRequest
import com.woowacourse.ody.data.remote.core.entity.meeting.response.toMeeting
import com.woowacourse.ody.data.remote.core.service.JoinService
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.MeetingJoinInfo
import com.woowacourse.ody.domain.repository.ody.JoinRepository

class DefaultJoinRepository(private val service: JoinService) : JoinRepository {
    override suspend fun postMates(meetingJoinInfo: MeetingJoinInfo): Result<Meeting> =
        runCatching { service.postMates(meetingJoinInfo.toJoinRequest()).toMeeting() }
}
