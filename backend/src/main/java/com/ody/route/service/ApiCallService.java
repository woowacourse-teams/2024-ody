package com.ody.route.service;

import com.ody.route.domain.ApiCall;
import com.ody.route.domain.ClientType;
import com.ody.route.dto.ApiCallCountResponse;
import com.ody.route.repository.ApiCallRepository;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
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

    public ApiCallCountResponse countGoogleMapsApiCall() {
        int thisYear = Year.now().getValue();
        Month thisMonth = LocalDate.now().getMonth();
        List<ApiCall> apiCalls = apiCallRepository.findAllByClientTypeAndDateBetween(
                ClientType.GOOGLE_MAPS,
                LocalDate.of(thisYear, thisMonth, 1),
                LocalDate.now()
        );
        int totalCount = apiCalls.stream()
                .mapToInt(ApiCall::getCount)
                .sum();
        return new ApiCallCountResponse(totalCount);
    }
}
