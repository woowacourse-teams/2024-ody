package com.ody.route.service;

import com.ody.route.domain.ApiCall;
import com.ody.route.domain.ClientType;
import com.ody.route.dto.ApiCallCountResponse;
import com.ody.route.repository.ApiCallRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiCallService {

    private final ApiCallRepository apiCallRepository;

    public ApiCallCountResponse countOdsayApiCall() {
        Optional<ApiCall> apiCall = apiCallRepository.findFirstByDateAndClientType(
                LocalDate.now(),
                ClientType.ODSAY
        );
        return apiCall.map(call -> new ApiCallCountResponse(call.getCount()))
                .orElseGet(() -> new ApiCallCountResponse(0));
    }

    public ApiCallCountResponse countGoogleApiCall() {
        LocalDate now = LocalDate.now();
        List<ApiCall> apiCalls = apiCallRepository.findAllByClientTypeAndDateBetween(
                ClientType.GOOGLE,
                now.withDayOfMonth(1),
                now
        );
        int totalCount = apiCalls.stream()
                .mapToInt(ApiCall::getCount)
                .sum();
        return new ApiCallCountResponse(totalCount);
    }

    @Transactional
    public ApiCall increaseCountByRouteClient(RouteClient routeClient) {
        ClientType clientType = routeClient.getClientType();
        return apiCallRepository.findFirstByDateAndClientType(LocalDate.now(), clientType)
                .map(this::updateCount)
                .orElseGet(() -> saveInitialCount(clientType));
    }

    private ApiCall updateCount(ApiCall apiCall) {
        apiCall.increaseCount();
        return apiCall;
    }

    private ApiCall saveInitialCount(ClientType clientType) {
        return apiCallRepository.save(new ApiCall(clientType));
    }
}
