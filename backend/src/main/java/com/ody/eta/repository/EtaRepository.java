package com.ody.eta.repository;

import com.ody.eta.domain.Eta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EtaRepository extends JpaRepository<Eta, Long> {

    Optional<Eta> findByMateId(Long id);

    @Query("""
                    select e from Eta e
                    join Mate ma on e.mate.id = ma.id
                    join Meeting  me on e.mate.meeting.id = me.id
                    where me.id = :meetingId
            """)
    List<Eta> findAllByMeetingId(Long meetingId);
}
