package com.ydo.ody.domain.repository.ody

import com.ydo.ody.domain.apiresult.ApiResult
import com.ydo.ody.domain.model.MeetingJoinInfo
import com.ydo.ody.domain.model.ReserveInfo

interface JoinRepository {
    suspend fun postMates(meetingJoinInfo: MeetingJoinInfo): ApiResult<ReserveInfo>
}
