package com.ydo.ody

import android.app.Application
import androidx.work.WorkManager
import com.kakao.sdk.common.KakaoSdk
import com.ydo.ody.BuildConfig.DEBUG
import com.ydo.ody.data.DefaultAuthTokenRepository
import com.ydo.ody.data.local.db.OdyDatastore
import com.ydo.ody.data.local.repository.DefaultMatesEtaRepository
import com.ydo.ody.data.remote.core.RetrofitClient
import com.ydo.ody.data.remote.core.repository.DefaultFCMTokenRepository
import com.ydo.ody.data.remote.core.repository.DefaultJoinRepository
import com.ydo.ody.data.remote.core.repository.DefaultMeetingRepository
import com.ydo.ody.data.remote.core.repository.DefaultNotificationLogRepository
import com.ydo.ody.data.remote.core.service.JoinService
import com.ydo.ody.data.remote.core.service.LoginService
import com.ydo.ody.data.remote.core.service.MeetingService
import com.ydo.ody.data.remote.core.service.NotificationService
import com.ydo.ody.data.remote.core.service.RefreshTokenService
import com.ydo.ody.data.remote.thirdparty.image.FirebaseImageStorage
import com.ydo.ody.data.remote.thirdparty.location.KakaoRetrofitClient
import com.ydo.ody.data.remote.thirdparty.location.repository.KakaoGeoLocationRepository
import com.ydo.ody.data.remote.thirdparty.location.service.KakaoLocationService
import com.ydo.ody.data.remote.thirdparty.login.kakao.KakaoLoginRepository
import com.ydo.ody.data.remote.thirdparty.login.kakao.KakaoOAuthLoginService
import com.ydo.ody.domain.common.Provider
import com.ydo.ody.domain.repository.image.ImageStorage
import com.ydo.ody.domain.repository.ody.AuthTokenRepository
import com.ydo.ody.domain.repository.ody.FCMTokenRepository
import com.ydo.ody.domain.repository.ody.JoinRepository
import com.ydo.ody.domain.repository.ody.MatesEtaRepository
import com.ydo.ody.domain.repository.ody.MeetingRepository
import com.ydo.ody.domain.repository.ody.NotificationLogRepository
import com.ydo.ody.presentation.common.PermissionHelper
import com.ydo.ody.presentation.common.analytics.AnalyticsHelper
import com.ydo.ody.presentation.common.analytics.FirebaseAnalyticsHelper
import com.ydo.ody.presentation.notification.NotificationHelper
import retrofit2.Retrofit
import timber.log.Timber

class OdyApplication : Application() {
    private val odyDatastore by lazy {
        OdyDatastore(this)
    }

    private val refreshTokenService by lazy {
        Provider {
            refreshRetrofit.create(RefreshTokenService::class.java)
        }
    }

    val authTokenRepository: AuthTokenRepository by lazy {
        DefaultAuthTokenRepository(
            odyDatastore,
            refreshTokenService,
        )
    }
    private val retrofit: Retrofit = RetrofitClient(authTokenRepository).retrofit
    private val loginRetrofit: Retrofit = RetrofitClient(authTokenRepository).loginRetrofit
    private val refreshRetrofit: Retrofit = RetrofitClient(authTokenRepository).refreshRetrofit
    private val kakaoRetrofit: Retrofit = KakaoRetrofitClient().retrofit

    private val joinService: JoinService = retrofit.create(JoinService::class.java)
    private val meetingService: MeetingService = retrofit.create(MeetingService::class.java)
    private val loginService: LoginService = loginRetrofit.create(LoginService::class.java)
    private val notificationService: NotificationService =
        retrofit.create(NotificationService::class.java)
    private val kakaoLocationService: KakaoLocationService =
        kakaoRetrofit.create(KakaoLocationService::class.java)
    private val workerManager: WorkManager by lazy { WorkManager.getInstance(this) }
    val analyticsHelper: AnalyticsHelper by lazy { FirebaseAnalyticsHelper(this) }
    val notificationHelper: NotificationHelper by lazy { NotificationHelper(this) }
    val permissionHelper: PermissionHelper by lazy { PermissionHelper(this) }
    val imageStorage: ImageStorage = FirebaseImageStorage()

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
            loginService,
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
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
    }
}

object OdyDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String =
        "${element.fileName}:${element.lineNumber}#${element.methodName}"
}
