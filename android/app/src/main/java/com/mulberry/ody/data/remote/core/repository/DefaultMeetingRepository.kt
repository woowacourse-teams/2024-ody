package com.mulberry.ody.data.remote.core.repository

import com.mulberry.ody.data.local.db.MateEtaInfoDao
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.data.remote.core.entity.meeting.mapper.toMateEtaInfo
import com.mulberry.ody.data.remote.core.entity.meeting.mapper.toMeeting
import com.mulberry.ody.data.remote.core.entity.meeting.mapper.toMeetingCatalogs
import com.mulberry.ody.data.remote.core.entity.meeting.mapper.toMeetingRequest
import com.mulberry.ody.data.remote.core.entity.meeting.mapper.toNudgeRequest
import com.mulberry.ody.data.remote.core.entity.meeting.request.MatesEtaRequest
import com.mulberry.ody.data.remote.core.service.MeetingService
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.apiresult.map
import com.mulberry.ody.domain.model.Mate
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.model.Meeting
import com.mulberry.ody.domain.model.MeetingCatalog
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.model.Nudge
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import java.time.LocalDate
import java.time.LocalTime
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

        override suspend fun fetchMeeting(meetingId: Long): ApiResult<Meeting> {
            return ApiResult.Success(
                Meeting(
                    id = 1,
                    name = "약속 이름",
                    date = LocalDate.of(2025,2,1),
                    time = LocalTime.of(11,30),
                    destinationAddress = "서울특별시 강남구 테헤란로 411 (성담빌딩)",
                    departureAddress="서울특별시 송파구 올림픽로 35다길 (한국루터회관)",
                    departureTime = LocalTime.of(10,0),
                    routeTime=70,
                    mates = listOf(Mate("올리브1",""), Mate("올리브2","")),
                    inviteCode= "12345"

                )
            )
            //return service.fetchMeeting(meetingId).map { it.toMeeting() }
        }

        override suspend fun postNudge(nudge: Nudge): ApiResult<Unit> = service.postNudge(nudge.toNudgeRequest())

        override suspend fun postMeeting(meetingCreationInfo: MeetingCreationInfo): ApiResult<String> =
            service.postMeeting(meetingCreationInfo.toMeetingRequest()).map { it.inviteCode }

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

        override suspend fun fetchMeetingCatalogs(): ApiResult<List<MeetingCatalog>> =
            service.fetchMeetingCatalogs().map { it.toMeetingCatalogs() }

        override suspend fun exitMeeting(meetingId: Long): ApiResult<Unit> {
            return service.exitMeeting(meetingId)
        }
    }
