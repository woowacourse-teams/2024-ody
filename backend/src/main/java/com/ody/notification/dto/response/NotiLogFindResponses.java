package com.ody.notification.dto.response;

import com.ody.notification.domain.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;

public record NotiLogFindResponses(

        @Schema(
                description = "로그 목록",
                example = "[{\"type\": \"ENTRY\",\n\"nickname\": \"조조\",\n\"createdAt\": \"2024-07-17 08:59:32\"}]"
        )
        List<NotiLogFindResponse> notiLog
) {

    public static NotiLogFindResponses from(List<Notification> notifications) {
        return notifications.stream()
                .map(NotiLogFindResponse::new)
                .collect(Collectors.collectingAndThen(Collectors.toList(), NotiLogFindResponses::new));
    }
}
