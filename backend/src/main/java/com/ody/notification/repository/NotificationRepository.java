package com.ody.notification.repository;

import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
            select noti
            from Notification noti
            left join fetch Mate m on noti.mate = m
            left join Meeting meet on m.meeting = meet
            where meet.id = :meetingId and noti.sendAt <= now()
            order by noti.sendAt asc
            """)
    List<Notification> findAllMeetingLogs(Long meetingId);

    List<Notification> findAllByTypeAndStatus(NotificationType type, NotificationStatus status);

    @Query("""
            select noti
            from Notification noti
            join fetch Mate mate on noti.mate.id = mate.id and mate.meeting.id = :meetingId
            join fetch Member member on mate.member.id = member.id
            where noti.type = :type
            """)
    List<Notification> findAllMeetingIdAndType(Long meetingId, NotificationType type);

    List<Notification> findAllByMateIdAndStatus(long mateId, NotificationStatus status);
}
