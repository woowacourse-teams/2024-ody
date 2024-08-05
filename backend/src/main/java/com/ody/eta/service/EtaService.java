package com.ody.eta.service;

import com.ody.eta.domain.Eta;
import com.ody.eta.repository.EtaRepository;
import com.ody.mate.domain.Mate;
import com.ody.route.domain.RouteTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EtaService {

    private final EtaRepository etaRepository;

    public Eta saveFirstEtaOfMate(Mate mate, RouteTime routeTime) {
        return etaRepository.save(new Eta(mate, routeTime.getMinutes()));
    }
}
