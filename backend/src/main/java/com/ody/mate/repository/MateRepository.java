package com.ody.mate.repository;

import com.ody.mate.domain.Mate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MateRepository extends JpaRepository<Mate, Long> {

    List<Mate> findAllByMeetingId(Long meetingId);

    @Query("""
            select mate
            from Mate mate
            join fetch mate.meeting
            where mate.member.id = :memberId
            and mate.meeting.id = :meetingId
            """)
    Optional<Mate> findByMeetingIdAndMemberId(Long meetingId, Long memberId);

    boolean existsByMeetingIdAndNickname_Value(Long meetingId, String value);

    boolean existsByMeetingIdAndMemberId(Long memberId, Long meetingId);
}
