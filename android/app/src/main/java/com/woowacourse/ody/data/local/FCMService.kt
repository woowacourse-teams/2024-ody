package com.woowacourse.ody.data.local

import com.google.firebase.messaging.FirebaseMessagingService
import com.woowacourse.ody.data.remote.RetrofitClient
import com.woowacourse.ody.data.remote.service.MemberService
import kotlinx.coroutines.runBlocking

class FCMService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        val retrofit = RetrofitClient.retrofit
        val memberService = retrofit.create(MemberService::class.java)
        runBlocking {
            memberService.postMember()
        }
    }
}
