package com.woowacourse.ody

import android.app.Application
import androidx.work.WorkManager
import com.kakao.sdk.common.KakaoSdk
import com.woowacourse.ody.BuildConfig.DEBUG
import com.woowacourse.ody.data.DefaultAuthTokenRepository
import com.woowacourse.ody.data.local.db.OdyDatastore
import com.woowacourse.ody.data.local.repository.DefaultMatesEtaRepository
import com.woowacourse.ody.data.remote.core.RetrofitClient
import com.woowacourse.ody.data.remote.core.repository.DefaultFCMTokenRepository
import com.woowacourse.ody.data.remote.core.repository.DefaultJoinRepository
import com.woowacourse.ody.data.remote.core.repository.DefaultMeetingRepository
import com.woowacourse.ody.data.remote.core.repository.DefaultNotificationLogRepository
import com.woowacourse.ody.data.remote.core.service.AuthService
import com.woowacourse.ody.data.remote.core.service.JoinService
import com.woowacourse.ody.data.remote.core.service.MeetingService
import com.woowacourse.ody.data.remote.core.service.NotificationService
import com.woowacourse.ody.data.remote.thirdparty.location.KakaoRetrofitClient
import com.woowacourse.ody.data.remote.thirdparty.location.repository.KakaoGeoLocationRepository
import com.woowacourse.ody.data.remote.thirdparty.location.service.KakaoLocationService
import com.woowacourse.ody.data.remote.thirdparty.login.kakao.KakaoLoginRepository
import com.woowacourse.ody.data.remote.thirdparty.login.kakao.KakaoOAuthLoginService
import com.woowacourse.ody.domain.common.Provider
import com.woowacourse.ody.domain.repository.ody.AuthTokenRepository
import com.woowacourse.ody.domain.repository.ody.FCMTokenRepository
import com.woowacourse.ody.domain.repository.ody.JoinRepository
import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository
import com.woowacourse.ody.presentation.common.PermissionHelper
import com.woowacourse.ody.presentation.common.analytics.AnalyticsHelper
import com.woowacourse.ody.presentation.common.analytics.FirebaseAnalyticsHelper
import com.woowacourse.ody.presentation.notification.NotificationHelper
import retrofit2.Retrofit
import timber.log.Timber

class OdyApplication : Application() {
    private val odyDatastore by lazy {
        OdyDatastore(this)
    }
    private val authService by lazy {
        Provider<AuthService> { retrofit.create(AuthService::class.java) }
    }
    private val authTokenRepository: AuthTokenRepository by lazy {
        DefaultAuthTokenRepository(
            odyDatastore,
            authService,
        )
    }
    private val retrofit: Retrofit = RetrofitClient(authTokenRepository).retrofit
    private val kakaoRetrofit: Retrofit = KakaoRetrofitClient().retrofit

    private val joinService: JoinService = retrofit.create(JoinService::class.java)
    private val meetingService: MeetingService = retrofit.create(MeetingService::class.java)
    private val notificationService: NotificationService =
        retrofit.create(NotificationService::class.java)
    private val kakaoLocationService: KakaoLocationService =
        kakaoRetrofit.create(KakaoLocationService::class.java)
    private val workerManager: WorkManager by lazy { WorkManager.getInstance(this) }
    val analyticsHelper: AnalyticsHelper by lazy { FirebaseAnalyticsHelper(this) }
    val notificationHelper: NotificationHelper by lazy { NotificationHelper(this) }
    val permissionHelper: PermissionHelper by lazy { PermissionHelper(this) }

    val joinRepository: JoinRepository by lazy { DefaultJoinRepository(joinService) }
    val meetingRepository: MeetingRepository by lazy { DefaultMeetingRepository(meetingService) }
    val fcmTokenRepository: FCMTokenRepository by lazy { DefaultFCMTokenRepository(odyDatastore) }
    val matesEtaRepository: MatesEtaRepository by lazy { DefaultMatesEtaRepository(workerManager) }
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

    val kakaoLoginRepository: KakaoLoginRepository by lazy {
        KakaoLoginRepository(
            authService.get(),
            odyDatastore,
            KakaoOAuthLoginService(),
            fcmTokenRepository,
        )
    }

    override fun onCreate() {
        super.onCreate()
        if (DEBUG) {
            Timber.plant(OdyDebugTree)
        }
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }

    companion object {
        private const val ODY_KEY = "ody_key"
    }
}

object OdyDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String =
        "${element.fileName}:${element.lineNumber}#${element.methodName}"
}
