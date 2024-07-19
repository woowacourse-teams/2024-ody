package com.ody.notification.service;

import com.ody.notification.dto.request.FcmSendRequest;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FcmEventScheduler {

    private static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);

    private final TaskScheduler taskScheduler;
    private final FcmPushSender fcmPushSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void addDepartureNotification(FcmSendRequest fcmSendRequest) {
        taskScheduler.schedule(
                () -> fcmPushSender.sendPushNotification(fcmSendRequest), fcmSendRequest.sendAt().toInstant(KST_OFFSET)
        );
    }
}
