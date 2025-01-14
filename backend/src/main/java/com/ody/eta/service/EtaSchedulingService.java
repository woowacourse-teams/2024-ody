package com.ody.eta.service;

import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.notification.domain.message.GroupMessage;
import com.ody.notification.service.FcmEventPublisher;
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
public class EtaSchedulingService {

    private static final long ETA_NOTICE_TIME_DEFER = 30L;

    private final TaskScheduler taskScheduler;
    private final FcmEventPublisher fcmEventPublisher;
    private final MeetingRepository meetingRepository;

    public void scheduleEtaSchedulingNotice(Meeting meeting) {
        LocalDateTime etaSchedulingNoticeTime = meeting.getMeetingTime().minusMinutes(ETA_NOTICE_TIME_DEFER);
        Instant startTime = InstantConverter.kstToInstant(etaSchedulingNoticeTime);
        taskScheduler.schedule(() -> sendNotice(meeting.getId()), startTime);
    }

    public void sendNotice(long meetingId) {
        meetingRepository.findById(meetingId)
                .ifPresent(meeting -> {
                    GroupMessage noticeMessage = GroupMessage.createEtaSchedulingNotice(meeting);
                    NoticeEvent noticeEvent = new NoticeEvent(this, noticeMessage);

                    fcmEventPublisher.publish(noticeEvent); //FCM 알림 전송
                    //redis 저장
                });
    }
}
