package com.ody.auth.dto.request;

import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

public record AuthRequest(

        @Schema(description = "디바이스 토큰", example = "devicetokendevicetoken")
        String deviceToken,

        @Schema(description = "회원 번호", example = "12345")
        String providerId,

        @Schema(description = "닉네임", example = "몽키건우")
        String nickname,

        @Schema(description = "프로필 사진 url", example = "imageurl")
        String imageUrl
) {

    public DeviceToken toDeviceToken() {
        return new DeviceToken(deviceToken);
    }

    public Member toMember() {
        return new Member(providerId, nickname, imageUrl, toDeviceToken());
    }
}
