package com.woowacourse.ody.data.remote.service

import retrofit2.http.POST

interface MemberService {
    @POST(MEMBER_PATH)
    suspend fun postMember()

    companion object {
        private const val MEMBER_PATH = "/members"
    }
}
