package com.ody.meetinglog.service;

import com.ody.meetinglog.domain.MeetingLog;
import com.ody.meetinglog.repository.MeetingLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingLogService {

    private final MeetingLogRepository meetingLogRepository;

    public MeetingLog save(MeetingLog meetingLog) {
        return meetingLogRepository.save(meetingLog);
    }

    public List<MeetingLog> findAllByMeetingId(long meetingId) {
        return meetingLogRepository.findByMeetingId(meetingId);
    }
}
