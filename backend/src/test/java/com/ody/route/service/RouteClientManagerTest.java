package com.ody.route.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.BaseServiceTest;
import com.ody.route.domain.ApiCall;
import com.ody.route.domain.ClientType;
import com.ody.route.repository.ApiCallRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RouteClientManagerTest extends BaseServiceTest {

    @Autowired
    private RouteClientManager routeclientManager;

    @Autowired
    private ApiCallRepository apiCallRepository;

    @BeforeEach
    void cleanup() {
        apiCallRepository.deleteAll();
    }

    @DisplayName("오늘 일자의 apiCall Enabled를 가진 다음 날짜 apiCall이 초기화된다.")
    @Test
    void initializeClientApiCalls() {
        LocalDate now = LocalDate.now();
        System.out.println(now);
        LocalDate tommorow = now.plusDays(1);
        ApiCall odsayApiCall = new ApiCall(ClientType.ODSAY, 1, now, true);
        ApiCall googleApiCall = new ApiCall(ClientType.GOOGLE, 1, now, false);
        apiCallRepository.save(odsayApiCall);
        apiCallRepository.save(googleApiCall);

        routeclientManager.initializeClientApiCalls();
        Optional<ApiCall> nextDayOdsay = apiCallRepository.findFirstByDateBetweenAndClientType(tommorow, tommorow, ClientType.ODSAY);
        Optional<ApiCall> nextDayGoogle = apiCallRepository.findFirstByDateBetweenAndClientType(tommorow, tommorow, ClientType.GOOGLE);

        assertAll(
                () -> assertThat(nextDayOdsay.get().getEnabled()).isTrue(),
                () -> assertThat(nextDayGoogle.get().getEnabled()).isFalse()
        );
    }
}
