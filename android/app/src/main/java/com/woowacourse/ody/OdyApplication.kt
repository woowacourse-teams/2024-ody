package com.woowacourse.ody

import android.app.Application
import com.woowacourse.ody.BuildConfig.DEBUG
import com.woowacourse.ody.data.local.db.OdyDatastore
import com.woowacourse.ody.data.remote.core.RetrofitClient
import com.woowacourse.ody.data.remote.core.repository.DefaultJoinRepository
import com.woowacourse.ody.data.remote.core.repository.DefaultMeetingRepository
import com.woowacourse.ody.data.remote.core.repository.DefaultNotificationLogRepository
import com.woowacourse.ody.data.remote.core.service.JoinService
import com.woowacourse.ody.data.remote.core.service.MeetingService
import com.woowacourse.ody.data.remote.core.service.NotificationService
import com.woowacourse.ody.data.remote.thirdparty.location.KakaoRetrofitClient
import com.woowacourse.ody.data.remote.thirdparty.location.repository.KakaoGeoLocationRepository
import com.woowacourse.ody.data.remote.thirdparty.location.service.KakaoLocationService
import com.woowacourse.ody.presentation.address.AddressSearchViewModelFactory
import com.woowacourse.ody.presentation.invitecode.InviteCodeViewModelFactory
import com.woowacourse.ody.presentation.join.complete.JoinCompleteViewModelFactory
import com.woowacourse.ody.presentation.room.MeetingRoomViewModelFactory
import com.woowacourse.ody.presentation.splash.SplashViewModelFactory
import timber.log.Timber

class OdyApplication : Application() {
    val odyDatastore by lazy { OdyDatastore(this) }
    private val retrofit = RetrofitClient().retrofit
    private val kakaoRetrofit = KakaoRetrofitClient().retrofit

    private val joinService = retrofit.create(JoinService::class.java)
    private val meetingService = retrofit.create(MeetingService::class.java)
    private val notificationService: NotificationService =
        retrofit.create(NotificationService::class.java)

    private val kakaoLocationService = kakaoRetrofit.create(KakaoLocationService::class.java)

    private val joinRepository by lazy { DefaultJoinRepository(joinService) }
    private val meetingRepository by lazy { DefaultMeetingRepository(meetingService) }
    private val notificationLogRepository by lazy {
        DefaultNotificationLogRepository(
            notificationService,
        )
    }

    private val kakaoGeoLocationRepository by lazy { KakaoGeoLocationRepository(kakaoLocationService) }

    val meetingRoomViewModelFactory =
        MeetingRoomViewModelFactory(
            notificationLogRepository,
            meetingRepository,
        )
    val joinCompleteViewModelFactory =
        JoinCompleteViewModelFactory(
            meetingRepository = meetingRepository,
            joinRepository = joinRepository,
            datastore = odyDatastore,
        )
    val inviteCodeViewModelFactory = InviteCodeViewModelFactory(meetingRepository)
    val splashViewModelFactory = SplashViewModelFactory(meetingRepository)
    val addressSearchViewModelFactory = AddressSearchViewModelFactory(kakaoGeoLocationRepository)

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
