package com.ody.eta.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketMessageSender {

    private static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);
    private static final String BLANK_PAYLOAD = "";

    private final TaskScheduler taskScheduler;
    private final SimpMessagingTemplate template;

    public void reserveMessage(String destination, LocalDateTime triggerTime) {
        Instant startTime = triggerTime.toInstant(KST_OFFSET);
        taskScheduler.schedule(() -> template.convertAndSend(destination, BLANK_PAYLOAD), startTime); //TODO payLoad로 비교
        log.info("--- schedule 예약 완료 ! - {}, {}", destination, triggerTime);
    }

    public void sendMessage(String destination) {
        template.convertAndSend(destination, BLANK_PAYLOAD);
    }
}
