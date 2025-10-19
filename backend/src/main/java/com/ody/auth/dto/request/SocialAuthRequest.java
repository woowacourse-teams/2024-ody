package com.ody.auth.dto.request;

import com.ody.mate.domain.Nickname;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.member.domain.ProviderType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SocialAuthRequest {

    private static final String DEFAULT_IMAGE_URL = "https://img1.kakaocdn.net/thumb/R110x110.q70/?fname=https://t1.kakaocdn.net/account_images/default_profile.jpeg";

    @Schema(description = "디바이스 토큰", example = "devicetokendevicetoken")
    @NotBlank
    private final String deviceToken;

    @Schema(description = "회원 번호", example = "12345")
    @NotBlank
    private final String providerId;

    @Schema(description = "닉네임", example = "몽키건우")
    @NotNull
    private final String nickname;

    @Schema(description = "프로필 사진 url", example = "imageurl", nullable = true)
    private final String imageUrl;

    protected Member toMember(ProviderType providerType) {
        AuthProvider authProvider = new AuthProvider(providerType, providerId);
        String finalImageUrl = Optional.ofNullable(imageUrl)
                .filter(s -> !s.isBlank())
                .orElse(DEFAULT_IMAGE_URL);
        return new Member(authProvider, new Nickname(nickname), finalImageUrl, new DeviceToken(deviceToken));
    }
}
