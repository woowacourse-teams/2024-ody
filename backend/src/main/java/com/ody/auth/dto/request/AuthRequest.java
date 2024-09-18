package com.ody.auth.dto.request;

import com.ody.mate.domain.Nickname;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AuthRequest(

        @Schema(description = "디바이스 토큰", example = "devicetokendevicetoken")
        @NotNull
        String deviceToken,

        @Schema(description = "회원 번호", example = "12345")
        @NotNull
        String providerId,

        @Schema(description = "닉네임", example = "몽키건우")
        @NotNull
        String nickname,

        @Schema(description = "프로필 사진 url", example = "imageurl")
        @NotNull
        String imageUrl
) {

    public DeviceToken toDeviceToken() {
        return new DeviceToken(deviceToken);
    }

    public Nickname toNickname() {
        return new Nickname(nickname);
    }

    public Member toMember() {
        return new Member(providerId, toNickname(), imageUrl, toDeviceToken());
    }
}
