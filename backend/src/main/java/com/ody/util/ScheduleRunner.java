package com.ody.util;

import com.ody.common.exception.OdyServerErrorException;
import com.ody.notification.service.event.PushEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleRunner {

    private final TaskScheduler taskScheduler;

    @Transactional
    public void runWithTransaction(Runnable runnable, LocalDateTime scheduleTime) {
        try {
            Instant startTime = InstantConverter.kstToInstant(scheduleTime);
            taskScheduler.schedule(() -> {
                runnable.run();
                log.info("스케쥴링 로직 실행 성공");
            }, startTime);
        }catch (Exception e){
            log.error("스케쥴링 실패");
            throw new OdyServerErrorException("스케쥴링에 실패하였습니다");
        }
    }
}
