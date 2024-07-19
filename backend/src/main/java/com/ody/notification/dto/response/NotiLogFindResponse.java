package com.ody.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record NotiLogFindResponse(

        @Schema(
                description = "로그 목록",
                example = "[{\"type\": \"ENTRY\",\n\"nickname\": \"조조\",\n\"createdAt\": \"2024-07-17 08:59:32\"}]"
        )
        List<NotificationSaveResponse> notiLog
) {

}
