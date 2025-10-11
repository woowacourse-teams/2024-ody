package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.DetailMeeting
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import javax.inject.Inject

class GetDetailMeetingUseCase
    @Inject
    constructor(
        private val meetingRepository: MeetingRepository,
    ) {
        suspend operator fun invoke(meetingId: Long): ApiResult<DetailMeeting> {
            return meetingRepository.fetchMeeting(meetingId)
        }
    }
