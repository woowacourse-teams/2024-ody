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
        ApiCall todayGoogleApiCall = new ApiCall(ClientType.GOOGLE, 3, now);
        apiCallRepository.save(yesterdayOdsayApiCall);
        apiCallRepository.save(todayOdsayApiCall);
        apiCallRepository.save(todayGoogleApiCall);

        // when
        Optional<ApiCall> actual = apiCallRepository.findFirstByDateAndClientType(now, ClientType.ODSAY);

        // then
        assertThat(actual).isPresent()
                .get().extracting(ApiCall::getCount).isEqualTo(2);
    }

    @DisplayName("특정 ClientType과 날짜 기간 내에서 가장 빠른 데이터를 조회한다.")
    @Test
    void findFirstByDateBetweenAndClientType() {
        ClientType clientType = ClientType.GOOGLE;
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        ApiCall secondApiCall = new ApiCall(clientType, 1, firstDay.plusDays(5));
        ApiCall thirdApiCall = new ApiCall(clientType, 2, firstDay.plusDays(10));
        ApiCall firstApiCall = new ApiCall(clientType, 3, firstDay.plusDays(3));
        apiCallRepository.save(secondApiCall);
        apiCallRepository.save(thirdApiCall);
        apiCallRepository.save(firstApiCall);

        Optional<ApiCall> actual = apiCallRepository.findFirstByDateBetweenAndClientType(firstDay, now, clientType);

        assertThat(actual).isPresent()
                .get().extracting(ApiCall::getDate).isEqualTo(firstApiCall.getDate());
    }

    @DisplayName("특정 ClientType과 날짜 기간이 일치하는 모든 데이터를 조회한다")
    @Test
    void findAllByDateBetweenAndClientType() {
        // given
        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);
        ApiCall yesterdayGoogleApiCall = new ApiCall(ClientType.GOOGLE, 1, yesterday);
        ApiCall todayGoogleApiCall = new ApiCall(ClientType.GOOGLE, 3, now);
        ApiCall todayOdsayApiCall = new ApiCall(ClientType.ODSAY, 2, now);
        apiCallRepository.save(yesterdayGoogleApiCall);
        apiCallRepository.save(todayGoogleApiCall);
        apiCallRepository.save(todayOdsayApiCall);

        // when
        List<ApiCall> actual = apiCallRepository.findAllByDateBetweenAndClientType(yesterday, now, ClientType.GOOGLE);

        // then
        assertAll(
                () -> assertThat(actual).extracting(ApiCall::getCount).containsExactly(1, 3),
                () -> assertThat(actual).extracting(ApiCall::getDate).containsExactly(yesterday, now)
        );
    }
}
