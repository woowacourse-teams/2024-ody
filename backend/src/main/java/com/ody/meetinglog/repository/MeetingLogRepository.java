package com.ody.meetinglog.repository;

import com.ody.meetinglog.domain.MeetingLog;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MeetingLogRepository extends JpaRepository<MeetingLog, Long> {

    @Query("""
            select meetingLog
             from MeetingLog meetingLog
             where meetingLog.mate.meeting.id = :meetingId
                         and meetingLog.showAt <= :time
            """)
    List<MeetingLog> findByShowAtBeforeOrEqualTo(long meetingId, LocalDateTime time);
}
