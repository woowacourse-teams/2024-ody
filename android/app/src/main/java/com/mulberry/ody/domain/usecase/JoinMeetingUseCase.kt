package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.MeetingJoinInfo
import com.mulberry.ody.domain.model.ReserveInfo
import com.mulberry.ody.domain.repository.ody.JoinRepository
import javax.inject.Inject

class JoinMeetingUseCase @Inject constructor(
    private val joinRepository: JoinRepository,
){
    suspend operator fun invoke(meetingJoinInfo: MeetingJoinInfo): ApiResult<ReserveInfo> {
        return joinRepository.joinMeeting(meetingJoinInfo)
    }
}
