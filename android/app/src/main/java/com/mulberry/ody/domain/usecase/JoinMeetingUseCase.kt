package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.EtaOpenInfo
import com.mulberry.ody.domain.model.MeetingJoinInfo
import com.mulberry.ody.domain.repository.ody.JoinRepository
import javax.inject.Inject

class JoinMeetingUseCase
    @Inject
    constructor(
        private val joinRepository: JoinRepository,
    ) {
        suspend operator fun invoke(meetingJoinInfo: MeetingJoinInfo): ApiResult<EtaOpenInfo> {
            return joinRepository.joinMeeting(meetingJoinInfo)
        }
    }
