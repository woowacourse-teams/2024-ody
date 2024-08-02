package com.ody.mate.repository;

import com.ody.mate.domain.Mate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateRepository extends JpaRepository<Mate, Long> {

    List<Mate> findAllByMeetingId(Long meetingId);

    boolean existsByMeetingIdAndNickname_Value(Long meetingId, String value);

    boolean existsByMeetingIdAndMemberId(Long memberId, Long meetingId);
}
