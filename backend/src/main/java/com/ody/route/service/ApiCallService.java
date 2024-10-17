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
        List<ApiCall> apiCalls = apiCallRepository.findAllByClientTypeAndDateBetween(clientType, start, end);

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
        Optional<ApiCall> apiCall = apiCallRepository.findFirstByClientTypeAndDate(clientType, LocalDate.now());
        return apiCall.orElseGet(() -> apiCallRepository.save(new ApiCall(clientType)));
    }

    public ApiCallEnabledResponse getApiCallEnabled(ClientType clientType) {
        boolean enabled = getEnabledByClientType(clientType);
        return new ApiCallEnabledResponse(enabled);
    }

    public boolean getEnabledByClientType(ClientType clientType) {
        ApiCall apiCall = findOrSaveFirstByClientTypeAndDateBetween(clientType);
        return apiCall.getEnabled();
    }

    @Transactional
    public void toggleApiCallEnabled(ClientType clientType) {
        ApiCall apiCall = findOrSaveFirstByClientTypeAndDateBetween(clientType);
        apiCall.updateEnabled();
    }

    private ApiCall findOrSaveFirstByClientTypeAndDateBetween(ClientType clientType) {
        LocalDate end = LocalDate.now();
        LocalDate start = clientType.determineResetDate(end);
        Optional<ApiCall> apiCall = apiCallRepository.findFirstByClientTypeAndDateBetween(clientType, start, end);
        return apiCall.orElseGet(() -> apiCallRepository.save(new ApiCall(clientType)));
    }
}
