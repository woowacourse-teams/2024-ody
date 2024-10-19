package com.ody.route.service;

import com.ody.route.domain.ApiCall;
import com.ody.route.domain.ClientType;
import com.ody.route.dto.ApiCallCountResponse;
import com.ody.route.dto.ApiCallEnabledResponse;
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

    public ApiCallCountResponse countApiCall(ClientType clientType) {
        LocalDate end = LocalDate.now();
        LocalDate start = clientType.determineResetDate(end);
        List<ApiCall> apiCalls = apiCallRepository.findAllByDateBetweenAndClientType(start, end, clientType);

        int totalCount = apiCalls.stream()
                .mapToInt(ApiCall::getCount)
                .sum();
        return new ApiCallCountResponse(totalCount);
    }

    @Transactional
    public void increaseCountByClientType(ClientType clientType) {
        ApiCall apiCall = findOrSaveFirstByClientTypeAndDate(clientType);
        apiCall.increaseCount();
    }

    private ApiCall findOrSaveFirstByClientTypeAndDate(ClientType clientType) {
        Optional<ApiCall> apiCall = apiCallRepository.findFirstByDateAndClientType(LocalDate.now(), clientType);
        return apiCall.orElseGet(() -> apiCallRepository.save(new ApiCall(clientType)));
    }

    public ApiCallEnabledResponse getApiCallEnabled(ClientType clientType) {
        boolean enabled = getEnabledByClientType(clientType);
        return new ApiCallEnabledResponse(enabled);
    }

    public boolean getEnabledByClientType(ClientType clientType) {
        ApiCall apiCall = findOrSaveApiCallForToggleByClientType(clientType);
        return apiCall.getEnabled();
    }

    @Transactional
    public void toggleApiCallEnabled(ClientType clientType) {
        ApiCall apiCall = findOrSaveApiCallForToggleByClientType(clientType);
        apiCall.updateEnabled();
    }

    private ApiCall findOrSaveApiCallForToggleByClientType(ClientType clientType) {
        LocalDate end = LocalDate.now();
        LocalDate start = clientType.determineResetDate(end);
        Optional<ApiCall> apiCall = apiCallRepository.findFirstByDateBetweenAndClientType(start, end, clientType);
        return apiCall.orElseGet(() -> apiCallRepository.save(new ApiCall(clientType)));
    }
}
