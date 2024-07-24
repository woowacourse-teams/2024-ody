package com.ody.mate.repository;

import com.ody.mate.domain.Mate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MateRepository extends JpaRepository<Mate, Long> {

    List<Mate> findAllByMeetingId(Long meetingId);
}
