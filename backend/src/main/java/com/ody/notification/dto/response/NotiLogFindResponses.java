package com.ody.notification.dto.response;

import com.ody.notification.domain.Notification;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;

public record NotiLogFindResponses(

        @ArraySchema(schema = @Schema(description = "로그 목록", implementation = NotiLogFindResponse.class))
        List<NotiLogFindResponse> notiLog
) {

    public static NotiLogFindResponses from(List<Notification> notifications) {
        return notifications.stream()
                .map(NotiLogFindResponse::new)
                .collect(Collectors.collectingAndThen(Collectors.toList(), NotiLogFindResponses::new));
    }
}
