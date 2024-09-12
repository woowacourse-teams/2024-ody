package com.ody.notification.dto.response;

import com.ody.notification.domain.Notification;
import com.ody.util.TimeUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EntityNotFoundException;
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
        try {
            return NotiLogFindResponse.create(notification);
        } catch (EntityNotFoundException exception) {
            return NotiLogFindResponse.createWithEmptyImageUrl(notification);
        }
    }

    private static NotiLogFindResponse create(Notification notification) {
        return new NotiLogFindResponse(
                notification,
                notification.getMate().getNickname(),
                notification.getMate().getMember().getImageUrl()
        );
    }

    private static NotiLogFindResponse createWithEmptyImageUrl(Notification notification) {
        return new NotiLogFindResponse(notification, "알 수 없음", "");
    }

    private NotiLogFindResponse(Notification notification, String nickname, String imageUrl) {
        this(
                notification.getType().toString(),
                nickname,
                TimeUtil.trimSecondsAndNanos(notification.getSendAt()),
                imageUrl
        );
    }
}
