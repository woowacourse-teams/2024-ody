package com.ody.notification.service;

import com.ody.notification.dto.request.FcmSendRequest;
import java.time.Instant;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

// TODO : Notification saveAndSendDepartureReminder()메서드 로직의 복잡성을 제거하기 위해 일단 scheduler 코드 미사용 중.
// 데모데이 끝나고 이 상황에서 발생할 수 있는 문제점을 확인해봐야할 듯.
@Component
@RequiredArgsConstructor
public class FcmEventScheduler {

    private static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);

    private final TaskScheduler taskScheduler;
    private final FcmPushSender fcmPushSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void addDepartureNotification(FcmSendRequest fcmSendRequest) {
        Instant startTime = fcmSendRequest.sendAt().toInstant(KST_OFFSET);
        taskScheduler.schedule(() -> fcmPushSender.sendPushNotification(fcmSendRequest), startTime);
    }
}
