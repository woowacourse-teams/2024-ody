package com.ody.notification.service;

import com.ody.notification.domain.message.AbstractMessage;
import com.ody.notification.domain.notice.Notice;
import com.ody.notification.service.event.NoticeEvent;
import com.ody.util.InstantConverter;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final FcmEventPublisher fcmEventPublisher;
    private final TaskScheduler taskScheduler;

    public <T extends AbstractMessage> void send(Notice notice, T t) {
        NoticeEvent<T> noticeEvent = new NoticeEvent<>(this, notice, t);
        fcmEventPublisher.publish(noticeEvent);
    }

    public <T extends AbstractMessage> void schedule(Notice notice, T t, LocalDateTime noticeTime) {
        Instant startTime = InstantConverter.kstToInstant(noticeTime);
        NoticeEvent<T> noticeEvent = new NoticeEvent<>(this, notice, t);
        taskScheduler.schedule(() -> fcmEventPublisher.publish(noticeEvent), startTime);
    }
}
