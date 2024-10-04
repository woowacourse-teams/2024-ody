package com.ody.route.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.BaseRepositoryTest;
import com.ody.route.domain.ApiCall;
import com.ody.route.domain.ClientType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ApiCallRepositoryTest extends BaseRepositoryTest {

    @DisplayName("특정 날짜와 ClientType이 일치하는 데이터를 조회한다")
    @Test
    void findFirstByDateAndClientType() {
        // given
        LocalDate now = LocalDate.now();
        ApiCall yesterdayOdsayApiCall = new ApiCall(ClientType.ODSAY, 1, now.minusDays(1));
        ApiCall todayOdsayApiCall = new ApiCall(ClientType.ODSAY, 2, now);
        ApiCall todayGoogleMapsApiCall = new ApiCall(ClientType.GOOGLE_MAPS, 3, now);
        apiCallRepository.save(yesterdayOdsayApiCall);
        apiCallRepository.save(todayOdsayApiCall);
        apiCallRepository.save(todayGoogleMapsApiCall);

        // when
        Optional<ApiCall> actual = apiCallRepository.findFirstByDateAndClientType(now, ClientType.ODSAY);

        // then
        assertThat(actual).isPresent()
                .get().extracting(ApiCall::getCount).isEqualTo(2);
    }

    @DisplayName("특정 ClientType과 날짜 기간이 일치하는 모든 데이터를 조회한다")
    @Test
    void findAllByClientTypeAndDateBetween() {
        // given
        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);
        ApiCall yesterdayGoogleMapsApiCall = new ApiCall(ClientType.GOOGLE_MAPS, 1, yesterday);
        ApiCall todayGoogleMapsApiCall = new ApiCall(ClientType.GOOGLE_MAPS, 3, now);
        ApiCall todayOdsayApiCall = new ApiCall(ClientType.ODSAY, 2, now);
        apiCallRepository.save(yesterdayGoogleMapsApiCall);
        apiCallRepository.save(todayGoogleMapsApiCall);
        apiCallRepository.save(todayOdsayApiCall);

        // when
        List<ApiCall> actual = apiCallRepository.findAllByClientTypeAndDateBetween(
                ClientType.GOOGLE_MAPS,
                yesterday,
                now
        );

        // then
        assertAll(
                () -> assertThat(actual).extracting(ApiCall::getCount).contains(1, 3),
                () -> assertThat(actual).extracting(ApiCall::getDate).contains(yesterday, now)
        );
    }
}
