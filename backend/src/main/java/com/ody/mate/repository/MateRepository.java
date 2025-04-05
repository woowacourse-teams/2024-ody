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

    @Query("""
            select mate
            from Mate mate
            join fetch mate.member
            where mate.meeting.id = :meetingId
            and mate.meeting.overdue = false
            """)
    List<Mate> findAllByOverdueFalseMeetingId(Long meetingId);

    @Query("""
            select mate
            from Mate mate
            join fetch mate.meeting
            join fetch mate.member
            where mate.meeting.id = :meetingId
            and mate.member.id = :memberId
            """)
    Optional<Mate> findByMeetingIdAndMemberId(Long meetingId, Long memberId);

    @Query("""
            select mate
            from Mate mate
            join fetch mate.meeting
            join fetch mate.member
            where mate.member.id = :memberId
            """)
    List<Mate> findFetchedAllByMemberId(long memberId);

    @Query("""
            select mate
            from Mate mate
            join fetch mate.member
            join fetch mate.meeting
            where mate.meeting.id = :meetingId
            """)
    List<Mate> findFetchedAllByMeetingId(long meetingId);

    boolean existsByMeetingIdAndMemberId(Long meetingId, Long memberId);

    int countByMeetingId(Long meetingId);
}
