package com.woowacourse.ody

import android.app.Application
import com.woowacourse.ody.BuildConfig.DEBUG
import com.woowacourse.ody.data.local.db.OdyDatastore
import com.woowacourse.ody.data.remote.core.RetrofitClient
import com.woowacourse.ody.data.remote.core.repository.DefaultFCMTokenRepository
import com.woowacourse.ody.data.remote.core.repository.DefaultInviteCodeRepository
import com.woowacourse.ody.data.remote.core.repository.DefaultJoinRepository
import com.woowacourse.ody.data.remote.core.repository.DefaultMeetingRepository
import com.woowacourse.ody.data.remote.core.repository.DefaultNotificationLogRepository
import com.woowacourse.ody.data.remote.core.service.JoinService
import com.woowacourse.ody.data.remote.core.service.MeetingService
import com.woowacourse.ody.data.remote.core.service.NotificationService
import com.woowacourse.ody.data.remote.thirdparty.location.KakaoRetrofitClient
import com.woowacourse.ody.data.remote.thirdparty.location.repository.KakaoGeoLocationRepository
import com.woowacourse.ody.data.remote.thirdparty.location.service.KakaoLocationService
import com.woowacourse.ody.domain.repository.ody.FCMTokenRepository
import com.woowacourse.ody.domain.repository.ody.InviteCodeRepository
import com.woowacourse.ody.domain.repository.ody.JoinRepository
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository
import retrofit2.Retrofit
import timber.log.Timber

class OdyApplication : Application() {
    private val odyDatastore by lazy { OdyDatastore(this) }
    private val retrofit: Retrofit = RetrofitClient().retrofit
    private val kakaoRetrofit: Retrofit = KakaoRetrofitClient().retrofit

    private val joinService: JoinService = retrofit.create(JoinService::class.java)
    private val meetingService: MeetingService = retrofit.create(MeetingService::class.java)
    private val notificationService: NotificationService =
        retrofit.create(NotificationService::class.java)

    private val kakaoLocationService: KakaoLocationService =
        kakaoRetrofit.create(KakaoLocationService::class.java)

    val joinRepository: JoinRepository by lazy { DefaultJoinRepository(joinService) }
    val meetingRepository: MeetingRepository by lazy {
        DefaultMeetingRepository(
            meetingService,
        )
    }
    val notificationLogRepository: NotificationLogRepository by lazy {
        DefaultNotificationLogRepository(
            notificationService,
        )
    }

    val kakaoGeoLocationRepository: KakaoGeoLocationRepository by lazy {
        KakaoGeoLocationRepository(
            kakaoLocationService,
        )
    }

    val inviteCodeRepository: InviteCodeRepository by lazy {
        DefaultInviteCodeRepository(odyDatastore)
    }

    val fcmTokenRepository: FCMTokenRepository by lazy {
        DefaultFCMTokenRepository(odyDatastore)
    }

    override fun onCreate() {
        super.onCreate()
        if (DEBUG) {
            Timber.plant(OdyDebugTree)
        }
    }
}

object OdyDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String =
        "${element.fileName}:${element.lineNumber}#${element.methodName}"
}
