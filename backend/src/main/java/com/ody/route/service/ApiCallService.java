package com.ody.route.service;

import com.ody.common.exception.OdyServerErrorException;
import com.ody.route.domain.ApiCall;
import com.ody.route.domain.ClientType;
import com.ody.route.dto.ApiCallCountResponse;
import com.ody.route.dto.ApiCallEnabledResponse;
import com.ody.route.repository.ApiCallRepository;
import jakarta.persistence.EntityManager;
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

    private final EntityManager entityManager;
    private final ApiCallRepository apiCallRepository;

    @Transactional
    public ApiCall save(ApiCall apiCall) {
        return apiCallRepository.save(apiCall);
    }

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
        entityManager.flush();
        entityManager.clear();
        ApiCall apiCall = findTodayApiCallByClientType(clientType);
        apiCall.increaseCount();
    }

    public ApiCallEnabledResponse getApiCallEnabled(ClientType clientType) {
        boolean enabled = getEnabledByClientType(clientType);
        return new ApiCallEnabledResponse(enabled);
    }

    public boolean getEnabledByClientType(ClientType clientType) {
        ApiCall apiCall = findApiCallForToggleByClientType(clientType);
        return apiCall.getEnabled();
    }

    @Transactional
    public void toggleApiCallEnabled(ClientType clientType) {
        ApiCall apiCall = findApiCallForToggleByClientType(clientType);
        apiCall.updateEnabled();
    }

    private ApiCall findApiCallForToggleByClientType(ClientType clientType) {
        LocalDate end = LocalDate.now();
        LocalDate start = clientType.determineResetDate(end);
        Optional<ApiCall> apiCall = apiCallRepository.findFirstByDateBetweenAndClientType(start, end, clientType);
        return apiCall
                .orElseThrow(() -> new OdyServerErrorException(clientType + "의 apiCall이 존재하지 않습니다."));
    }

    private ApiCall findTodayApiCallByClientType(ClientType clientType) {
        return apiCallRepository.findByDateAndClientType(LocalDate.now(), clientType)
                .orElseThrow(() -> new OdyServerErrorException(clientType + "의 apiCall이 존재하지 않습니다."));
    }
}
