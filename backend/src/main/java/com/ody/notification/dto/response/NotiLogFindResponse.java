package com.ody.notification.dto.response;

import com.ody.notification.domain.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record NotiLogFindResponse(

        @Schema(description = "알림 타입", example = "ENTRY")
        String type,

        @Schema(description = "닉네임", example = "조조")
        String nickname,

        @Schema(description = "생성 시각", example = "2024-07-17 08:59:32")
        LocalDateTime createdAt
) {

    public NotiLogFindResponse(Notification notification) {
        this(
                notification.getType().toString(),
                notification.getMate().getNickname().getNickname(),
                notification.getSendAt()
        );
    }
}
