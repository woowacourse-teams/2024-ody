package com.ody.meeting.repository;

import com.ody.meeting.domain.Meeting;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query("""
            select meet
            from Meeting meet
            right join Mate mate on mate.meeting.id = meet.id
            where mate.member.id = :memberId
            """
    )
    List<Meeting> findAllMeetingsByMemberId(Long memberId);
}
