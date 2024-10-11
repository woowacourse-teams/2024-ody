package com.ody.route.repository;

import com.ody.route.domain.ApiCall;
import com.ody.route.domain.ClientType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiCallRepository extends JpaRepository<ApiCall, Long> {

    Optional<ApiCall> findFirstByDateAndClientType(LocalDate date, ClientType clientType);

    List<ApiCall> findAllByClientTypeAndDateBetween(ClientType clientType, LocalDate start, LocalDate end);
}
