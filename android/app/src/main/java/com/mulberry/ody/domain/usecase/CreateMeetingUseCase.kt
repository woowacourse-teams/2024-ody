package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import javax.inject.Inject

class CreateMeetingUseCase
    @Inject
    constructor(
        private val meetingRepository: MeetingRepository,
    ) {
        suspend operator fun invoke(meetingCreationInfo: MeetingCreationInfo): ApiResult<String> {
            return meetingRepository.postMeeting(meetingCreationInfo)
        }
    }
