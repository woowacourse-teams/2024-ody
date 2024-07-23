package com.ody.notification.controller;

import com.ody.common.annotaion.AuthMember;
import com.ody.member.domain.Member;
import com.ody.notification.dto.response.NotiLogFindResponse;
import com.ody.notification.dto.response.NotificationSaveResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerSwagger {

    @Override
    @GetMapping("/meetings/{meetingId}/noti-log")
    public ResponseEntity<NotiLogFindResponse> findAllByMeetingId(
            @AuthMember Member member,
            @PathVariable Long meetingId
    ) {
        return ResponseEntity.ok(
                new NotiLogFindResponse(List.of(
                        new NotificationSaveResponse(
                                "ENTRY",
                                "조조",
                                LocalDateTime.parse("2024-07-17 08:59:32"))
                        ,
                        new NotificationSaveResponse(
                                "DEPARTURE_REMINDER",
                                "조조",
                                LocalDateTime.parse("2024-07-17 09:00:01")
                        )
                ))
        );
    }
}
