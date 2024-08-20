package com.woowacourse.ody.data.remote.thirdparty.login.kakao

import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.model.User
import com.woowacourse.ody.data.remote.thirdparty.login.entity.UserProfile
import com.woowacourse.ody.domain.model.AuthToken

fun OAuthToken.toAuthToken(): AuthToken =
    AuthToken(
        accessToken,
        refreshToken,
    )

fun User.toUserProfile(): UserProfile =
    UserProfile(
        id.toString(),
        kakaoAccount?.profile?.nickname.orEmpty(),
        kakaoAccount?.profile?.thumbnailImageUrl.orEmpty(),
    )
