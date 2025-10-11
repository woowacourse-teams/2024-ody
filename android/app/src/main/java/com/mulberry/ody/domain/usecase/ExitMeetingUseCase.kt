package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import javax.inject.Inject

class ExitMeetingUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository,
) {
    suspend operator fun invoke(meetingId: Long): ApiResult<Unit> {
        return meetingRepository.exitMeeting(meetingId)
    }
}
