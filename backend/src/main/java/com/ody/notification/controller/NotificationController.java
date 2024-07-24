package com.ody.notification.controller;

import com.ody.notification.domain.Notification;
import com.ody.notification.dto.response.NotiLogFindResponse;
import com.ody.notification.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerSwagger {

    private final NotificationService notificationService;

    @Override
    @GetMapping("/meetings/{meetingId}/noti-log")
    public ResponseEntity<NotiLogFindResponse> findAllMeetingLogs( //TODO method 위치 논의
            @RequestHeader(HttpHeaders.AUTHORIZATION) String fcmToken,
            @PathVariable Long meetingId
    ) {

        List<Notification> notifications = notificationService.findAllMeetingLogs(meetingId);
        NotiLogFindResponse response = NotiLogFindResponse.toResponse(notifications);
        return ResponseEntity.ok(response);
    }
}
