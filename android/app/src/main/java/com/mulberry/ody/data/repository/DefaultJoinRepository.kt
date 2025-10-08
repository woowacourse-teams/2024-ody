package com.mulberry.ody.data.repository

import com.mulberry.ody.data.remote.core.entity.join.mapper.toJoinRequest
import com.mulberry.ody.data.remote.core.entity.join.mapper.toEtaOpenInfo
import com.mulberry.ody.data.remote.core.service.JoinService
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.apiresult.map
import com.mulberry.ody.domain.model.MeetingJoinInfo
import com.mulberry.ody.domain.model.EtaOpenInfo
import com.mulberry.ody.domain.repository.ody.JoinRepository
import javax.inject.Inject

class DefaultJoinRepository
    @Inject
    constructor(private val service: JoinService) : JoinRepository {
        override suspend fun joinMeeting(meetingJoinInfo: MeetingJoinInfo): ApiResult<EtaOpenInfo> {
            return service.postMates(meetingJoinInfo.toJoinRequest()).map { it.toEtaOpenInfo() }
        }
    }
