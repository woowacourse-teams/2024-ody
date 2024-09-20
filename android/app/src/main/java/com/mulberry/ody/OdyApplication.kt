package com.mulberry.ody

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.mulberry.ody.BuildConfig.DEBUG
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class OdyApplication : Application() {
//    private val odyDatastore by lazy {
//        OdyDatastore(this)
//    }

//    private val refreshTokenService: Lazy<RefreshTokenService> by lazy {
//        Lazy {
//            refreshRetrofit.create(RefreshTokenService::class.java)
//        }
//    }

//    val authTokenRepository: AuthTokenRepository by lazy {
//        DefaultAuthTokenRepository(
//            refreshTokenService.get(),
//            odyDatastore,
//        )
//    }

//    private val retrofit: Retrofit = RetrofitClient(authTokenRepository).retrofit
//    private val loginRetrofit: Retrofit = RetrofitClient(authTokenRepository).loginRetrofit
//    private val refreshRetrofit: Retrofit = RetrofitClient(authTokenRepository).refreshRetrofit
//    private val kakaoRetrofit: Retrofit = KakaoRetrofitClient().retrofit

//    private val joinService: JoinService = retrofit.create(JoinService::class.java)
//    private val meetingService: MeetingService = retrofit.create(MeetingService::class.java)
//    private val loginService: LoginService = loginRetrofit.create(LoginService::class.java)
//    private val logoutService: LogoutService = retrofit.create(LogoutService::class.java)
//    private val notificationService: NotificationService = retrofit.create(NotificationService::class.java)
//    private val kakaoLocationService: KakaoLocationService =
//        kakaoRetrofit.create(KakaoLocationService::class.java)

//    private val workerManager: WorkManager by lazy { WorkManager.getInstance(this) }
//    val analyticsHelper: AnalyticsHelper by lazy { FirebaseAnalyticsHelper(this) }
//    val notificationHelper: NotificationHelper by lazy { NotificationHelper(this) }
//    val permissionHelper: PermissionHelper by lazy { PermissionHelper(this) }
//    val imageStorage: ImageStorage = FirebaseImageStorage()

//    val joinRepository: JoinRepository by lazy { DefaultJoinRepository(joinService) }
//    val meetingRepository: MeetingRepository by lazy { DefaultMeetingRepository(meetingService) }
//    val fcmTokenRepository: FCMTokenRepository by lazy { DefaultFCMTokenRepository(odyDatastore) }
//    val matesEtaRepository: MatesEtaRepository by lazy { DefaultMatesEtaRepository(workerManager) }
//    val notificationLogRepository: NotificationLogRepository by lazy {
//        DefaultNotificationLogRepository(
//            notificationService,
//        )
//    }

//    val kakaoGeoLocationRepository: KakaoGeoLocationRepository by lazy {
//        KakaoGeoLocationRepository(
//            kakaoLocationService,
//        )
//    }

//    val kakaoLoginRepository: KakaoLoginRepository by lazy {
//        KakaoLoginRepository(
//            loginService,
//            logoutService,
//            odyDatastore,
//            KakaoOAuthLoginService(),
//            fcmTokenRepository,
//        )
//    }

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
