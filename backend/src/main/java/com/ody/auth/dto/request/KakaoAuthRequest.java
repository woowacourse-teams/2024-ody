package com.ody.auth.dto.request;

import com.ody.member.domain.Member;
import com.ody.member.domain.ProviderType;

public class KakaoAuthRequest extends SocialAuthRequest {

    public KakaoAuthRequest(String deviceToken, String providerId, String nickname, String imageUrl) {
        super(deviceToken, providerId, nickname, imageUrl);
    }

    public Member toMember() {
        return toMember(ProviderType.KAKAO);
    }
}
