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
        ApiCall apiCall = findOrSaveFirstByDateAndClientType(ClientType.ODSAY);
        return new ApiCallCountResponse(apiCall.getCount());
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
    public void increaseCountByClientType(ClientType clientType) {
        ApiCall apiCall = findOrSaveFirstByDateAndClientType(clientType);
        apiCall.increaseCount();
    }

    public boolean findStateByClientType(ClientType clientType) {
        ApiCall apiCall = findOrSaveFirstByDateAndClientType(clientType);
        return apiCall.isState();
    }

    @Transactional
    public void toggleStateByClientType(ClientType clientType) {
        ApiCall apiCall = findOrSaveFirstByDateAndClientType(clientType);
        apiCall.updateState();
    }

    private ApiCall findOrSaveFirstByDateAndClientType(ClientType clientType) {
        Optional<ApiCall> apiCall = apiCallRepository.findFirstByDateAndClientType(LocalDate.now(), clientType);
        return apiCall.orElseGet(() -> apiCallRepository.save(new ApiCall(clientType)));
    }
}
