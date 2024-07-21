package com.woowacourse.ody.data.remote

import retrofit2.http.POST

interface MemberService {
    @POST(MEMBER_URL)
    suspend fun postMember()

    companion object {
        private const val MEMBER_URL = "/members"
    }
}
