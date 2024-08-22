package com.ody.mate.repository;

import com.ody.mate.domain.Mate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MateRepository extends JpaRepository<Mate, Long> {

    @Query("""
            select mate
            from Mate mate
            join fetch mate.meeting
            join fetch mate.member
            where mate.id = :id
            """)
    Optional<Mate> findFetchedMateById(Long id);

    List<Mate> findAllByMeetingId(Long meetingId);

    @Query("""
            select mate
            from Mate mate
            join fetch mate.meeting
            where mate.member.id = :memberId
            and mate.meeting.id = :meetingId
            """)
    Optional<Mate> findByMeetingIdAndMemberId(Long meetingId, Long memberId);

    boolean existsByMeetingIdAndMemberId(Long memberId, Long meetingId);

    int countByMeetingId(Long id);
}
