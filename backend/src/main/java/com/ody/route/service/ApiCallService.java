package com.ody.route.service;

import com.ody.common.exception.OdyServerErrorException;
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
        ApiCall apiCall = findOrSaveTodayApiCallByClientType(clientType);
        apiCall.increaseCount();
    }

    public ApiCallEnabledResponse getApiCallEnabled(ClientType clientType) {
        boolean enabled = getEnabledByClientType(clientType);
        return new ApiCallEnabledResponse(enabled);
    }

    public boolean getEnabledByClientType(ClientType clientType) {
        ApiCall apiCall = findOrSaveTodayApiCallByClientType(clientType);
        return apiCall.getEnabled();
    }

    public ApiCall findOrSaveTodayApiCallByClientType(ClientType clientType) {
        LocalDate now = LocalDate.now();
        return apiCallRepository.findByDateAndClientType(now, clientType)
                .orElseGet(() -> {
                    log.error("date : {}, clientType : {} apiCall을 찾을 수 없습니다.", now, clientType);
                    return save(new ApiCall(clientType, 0, now));
                });
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
}
