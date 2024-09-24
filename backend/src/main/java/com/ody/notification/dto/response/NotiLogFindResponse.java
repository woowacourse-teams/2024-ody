package com.ody.notification.dto.response;

import com.ody.notification.domain.Notification;
import com.ody.util.TimeUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record NotiLogFindResponse(

        @Schema(description = "알림 타입", example = "ENTRY")
        String type,

        @Schema(description = "닉네임", example = "조조")
        String nickname,

        @Schema(description = "생성 시각", example = "2024-07-17 08:59:32")
        LocalDateTime createdAt,

        @Schema(description = "참여자 프로필 사진 url", example = "imageUrl")
        String imageUrl
) {

    public static NotiLogFindResponse from(Notification notification) {
        String imageUrl = notification.getMate().getMember().getImageUrl();
        if (notification.getMate().isDeleted()) {
            imageUrl = "";
        }
        return new NotiLogFindResponse(
                notification.getType().toString(),
                notification.getMate().getNickname().getValue(),
                TimeUtil.trimSecondsAndNanos(notification.getSendAt()),
                imageUrl
        );
    }
}
