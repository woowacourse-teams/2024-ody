package com.ody.meetinglog.service;

import com.ody.meetinglog.domain.MeetingLog;
import com.ody.meetinglog.repository.MeetingLogRepository;
import com.ody.notification.dto.response.NotiLogFindResponses;
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
        List<MeetingLog> meetingLogs = meetingLogRepository.findByMeetingId(meetingId);
        return NotiLogFindResponses.from(meetingLogs);
    }
}
