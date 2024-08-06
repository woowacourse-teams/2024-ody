package com.ody.meeting.repository;

import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query("""
            select meeting
            from Meeting meeting
            right join Mate mate on meeting.id = mate.meeting.id
            where mate.member = :member
            """
    )
    List<Meeting> findAllByMember(Member member);
}
