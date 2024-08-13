package com.woowacourse.ody.data.remote.core.repository

import com.woowacourse.ody.data.remote.core.entity.join.mapper.toJoinRequest
import com.woowacourse.ody.data.remote.core.entity.join.mapper.toReserveInfo
import com.woowacourse.ody.data.remote.core.service.JoinService
import com.woowacourse.ody.domain.model.MeetingJoinInfo
import com.woowacourse.ody.domain.model.ReserveInfo
import com.woowacourse.ody.domain.repository.ody.JoinRepository

class DefaultJoinRepository(private val service: JoinService) : JoinRepository {
    override suspend fun postMates(meetingJoinInfo: MeetingJoinInfo): Result<ReserveInfo> {
        return runCatching {
            service.postMates(meetingJoinInfo.toJoinRequest()).toReserveInfo()
        }
    }
}
