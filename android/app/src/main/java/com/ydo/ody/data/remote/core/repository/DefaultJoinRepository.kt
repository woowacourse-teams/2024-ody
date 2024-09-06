package com.ydo.ody.data.remote.core.repository

import com.ydo.ody.data.remote.core.entity.join.mapper.toJoinRequest
import com.ydo.ody.data.remote.core.entity.join.mapper.toReserveInfo
import com.ydo.ody.data.remote.core.service.JoinService
import com.ydo.ody.domain.apiresult.ApiResult
import com.ydo.ody.domain.apiresult.map
import com.ydo.ody.domain.model.MeetingJoinInfo
import com.ydo.ody.domain.model.ReserveInfo
import com.ydo.ody.domain.repository.ody.JoinRepository

class DefaultJoinRepository(private val service: JoinService) : JoinRepository {
    override suspend fun postMates(meetingJoinInfo: MeetingJoinInfo): ApiResult<ReserveInfo> {
        return service.postMates(meetingJoinInfo.toJoinRequest()).map { it.toReserveInfo() }
    }
}
