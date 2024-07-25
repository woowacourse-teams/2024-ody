package com.ody.mate.repository;

import com.ody.mate.domain.Mate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateRepository extends JpaRepository<Mate, Long> {

    List<Mate> findAllByMeetingId(Long meetingId);

    boolean existsByMeetingIdAndNicknameNickname(Long meetingId, String nickname);

    boolean existsByMeetingIdAndMemberId(Long memberId, Long meetingId);
}
