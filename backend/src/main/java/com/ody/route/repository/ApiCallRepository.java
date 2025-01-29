package com.ody.route.repository;

import com.ody.route.domain.ApiCall;
import com.ody.route.domain.ClientType;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ApiCallRepository extends JpaRepository<ApiCall, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ApiCall> findByDateAndClientType(LocalDate date, ClientType clientType);

    Optional<ApiCall> findFirstByDateBetweenAndClientType(LocalDate start, LocalDate end, ClientType clientType);

    List<ApiCall> findAllByDateBetweenAndClientType(LocalDate start, LocalDate end, ClientType clientType);
}
