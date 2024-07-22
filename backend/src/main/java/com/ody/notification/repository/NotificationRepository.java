package com.ody.notification.repository;

import com.ody.notification.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
            select noti
            from Notification noti
            left join fetch Mate m on noti.mate = m
            left join Meeting meet on m.meeting = meet
            where meet.id = :meetingId
            """)
    List<Notification> findAllMeetingLogsById(Long meetingId);
}
