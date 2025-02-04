package com.ody.meeting.repository;

import com.ody.meeting.domain.Meeting;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query("""
            select meeting
            from Meeting meeting
            right join Mate mate on meeting.id = mate.meeting.id
            where mate.member.id = :memberId
            and meeting.overdue = false
            """
    )
    List<Meeting> findAllByMemberId(Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("update Meeting m set m.overdue = true where m.overdue = false and m.date < CURRENT_DATE")
    void updateAllByNotOverdueMeetings();

    @Query("select m from Meeting m where m.overdue = true and CAST(m.updatedAt AS LOCALDATE) = CURRENT_DATE")
    List<Meeting> findAllByUpdatedTodayAndOverdue();

    @Query("""
            select m
            from Meeting m
            where m.date > :startDate and m.date < :endDate
            or (m.date = :startDate and m.time >= :includeStartTime)
            or (m.date = :endDate and m.time < :excludeEndTime)
            """
    )
    List<Meeting> findAllWithInDateTimeRange( // StartDateTime >= TargetDateTime > EndDateTime
            LocalDate startDate,
            LocalTime includeStartTime,
            LocalDate endDate,
            LocalTime excludeEndTime
    );

    Optional<Meeting> findByIdAndOverdueFalse(Long id);

    Optional<Meeting> findByInviteCode(String inviteCode);

    boolean existsByInviteCode(String inviteCode);
}
