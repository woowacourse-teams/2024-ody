package com.mulberry.ody.data.remote.core.repository

import com.mulberry.ody.data.remote.core.entity.join.mapper.toJoinRequest
import com.mulberry.ody.data.remote.core.entity.join.mapper.toReserveInfo
import com.mulberry.ody.data.remote.core.service.JoinService
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.apiresult.map
import com.mulberry.ody.domain.model.MeetingJoinInfo
import com.mulberry.ody.domain.model.ReserveInfo
import com.mulberry.ody.domain.repository.ody.JoinRepository
import javax.inject.Inject

class DefaultJoinRepository
    @Inject
    constructor(private val service: JoinService) : JoinRepository {
        override suspend fun postMates(meetingJoinInfo: MeetingJoinInfo): ApiResult<ReserveInfo> {
            return service.postMates(meetingJoinInfo.toJoinRequest()).map { it.toReserveInfo() }
        }
    }
