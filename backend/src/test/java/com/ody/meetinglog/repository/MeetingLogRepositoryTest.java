package com.ody.meetinglog.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.ody.common.BaseRepositoryTest;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.meetinglog.domain.MeetingLog;
import com.ody.meetinglog.domain.MeetingLogType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MeetingLogRepositoryTest extends BaseRepositoryTest {

    @DisplayName("특정 시간 이전의 약속 로그를 가져온다")
    @Test
    void findByShowAtBeforeOrEqualTo() {
        LocalDateTime now = LocalDateTime.now();
        Meeting meeting = fixtureGenerator.generateMeeting();
        Mate mate = fixtureGenerator.generateMate(meeting);
        fixtureGenerator.generateMeetingLog(mate, MeetingLogType.ENTRY_LOG, now);
        fixtureGenerator.generateMeetingLog(mate, MeetingLogType.DEPARTURE_REMINDER, now.plusHours(1L));

        List<MeetingLog> meetingLogs = meetingLogRepository.findByShowAtBeforeOrEqualTo(meeting.getId(), now);

        assertAll(
                () -> assertThat(meetingLogs).hasSize(1),
                () -> assertThat(meetingLogs.get(0).getType()).isEqualTo(MeetingLogType.ENTRY_LOG)
        );
    }
}
