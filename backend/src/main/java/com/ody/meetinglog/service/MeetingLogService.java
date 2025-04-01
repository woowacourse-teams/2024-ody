package com.ody.meetinglog.service;

import com.ody.meetinglog.domain.MeetingLog;
import com.ody.meetinglog.repository.MeetingLogRepository;
import com.ody.notification.dto.response.NotiLogFindResponses;
import com.ody.util.TimeUtil;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingLogService {

    private final MeetingLogRepository meetingLogRepository;

    @Transactional
    public MeetingLog save(MeetingLog meetingLog) {
        return meetingLogRepository.save(meetingLog);
    }

    public NotiLogFindResponses findAllByMeetingId(long meetingId) {
        LocalDateTime now = TimeUtil.nowWithTrim();
        List<MeetingLog> meetingLogs = meetingLogRepository.findMeetingLogsBeforeThanEqual(meetingId, now);
        return NotiLogFindResponses.from(meetingLogs);
    }
}
