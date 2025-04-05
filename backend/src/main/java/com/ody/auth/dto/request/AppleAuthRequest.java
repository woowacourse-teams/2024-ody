package com.ody.auth.dto.request;

import com.ody.member.domain.Member;
import com.ody.member.domain.ProviderType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AppleAuthRequest extends SocialAuthRequest {

    @Schema(description = "애플 인증 코드", example = "authorizationcodeauthorizationcode")
    @NotBlank
    private final String authorizationCode;

    public AppleAuthRequest(
            String deviceToken,
            String providerId,
            String nickname,
            String imageUrl,
            String authorizationCode
    ) {
        super(deviceToken, providerId, nickname, imageUrl);
        this.authorizationCode = authorizationCode;
    }

    public Member toMember() {
        return toMember(ProviderType.APPLE);
    }
}
