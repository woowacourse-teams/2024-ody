package com.mulberry.ody.data.repository

import com.mulberry.ody.data.local.db.MateEtaInfoDao
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.data.remote.core.entity.meeting.mapper.toDetailMeeting
import com.mulberry.ody.data.remote.core.entity.meeting.mapper.toMateEtaInfo
import com.mulberry.ody.data.remote.core.entity.meeting.mapper.toMeetingRequest
import com.mulberry.ody.data.remote.core.entity.meeting.mapper.toMeetings
import com.mulberry.ody.data.remote.core.entity.meeting.mapper.toNudgeRequest
import com.mulberry.ody.data.remote.core.entity.meeting.request.MatesEtaRequest
import com.mulberry.ody.data.remote.core.service.MeetingService
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.apiresult.map
import com.mulberry.ody.domain.model.DetailMeeting
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.model.Meeting
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.model.Nudge
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import javax.inject.Inject

class DefaultMeetingRepository
    @Inject
    constructor(
        private val service: MeetingService,
        private val mateEtaInfoDao: MateEtaInfoDao,
    ) : MeetingRepository {
        override suspend fun fetchInviteCodeValidity(inviteCode: String): ApiResult<Unit> {
            return service.fetchInviteCodeValidity(inviteCode)
        }

        override suspend fun fetchMeeting(meetingId: Long): ApiResult<DetailMeeting> {
            return service.fetchMeeting(meetingId).map { it.toDetailMeeting() }
        }

        override suspend fun postNudge(nudge: Nudge): ApiResult<Unit> = service.postNudge(nudge.toNudgeRequest())

        override suspend fun postMeeting(meetingCreationInfo: MeetingCreationInfo): ApiResult<String> {
            return service.postMeeting(meetingCreationInfo.toMeetingRequest()).map { it.inviteCode }
        }

        override suspend fun patchMatesEta(
            meetingId: Long,
            isMissing: Boolean,
            currentLatitude: String,
            currentLongitude: String,
        ): ApiResult<MateEtaInfo> {
            return service.patchMatesEta(
                meetingId,
                MatesEtaRequest(isMissing, currentLatitude, currentLongitude),
            ).map { it.toMateEtaInfo() }
        }

        override suspend fun upsertMateEta(
            meetingId: Long,
            mateEtaInfo: MateEtaInfo,
        ): ApiResult<Unit> {
            val mateEtaInfoEntity =
                MateEtaInfoEntity(meetingId, mateEtaInfo.userId, mateEtaInfo.mateEtas)
            mateEtaInfoDao.upsert(mateEtaInfoEntity)
            return ApiResult.Success(Unit)
        }

        override suspend fun fetchMeetings(): ApiResult<List<Meeting>> = service.fetchMeetings().map { it.toMeetings() }

        override suspend fun exitMeeting(meetingId: Long): ApiResult<Unit> {
            return service.exitMeeting(meetingId)
        }
    }
