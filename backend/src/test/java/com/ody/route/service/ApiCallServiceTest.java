package com.ody.route.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.BaseServiceTest;
import com.ody.route.domain.ClientType;
import com.ody.route.dto.ApiCallCountResponse;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ApiCallServiceTest extends BaseServiceTest {

    @Autowired
    private ApiCallService apiCallService;

    @DisplayName("오늘 Odsay API 호출 횟수를 조회한다.")
    @Test
    void countOdsayApiCallSuccess() {
        apiCallService.increaseCountByClientType(ClientType.ODSAY);

        ApiCallCountResponse actual = apiCallService.countApiCall(ClientType.ODSAY);
        ApiCallCountResponse expected = new ApiCallCountResponse(1);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("오늘 Odsay API 호출 횟수가 없으면 0을 반환한다.")
    @Test
    void countOdsayApiCallFailure() {
        ApiCallCountResponse actual = apiCallService.countApiCall(ClientType.ODSAY);
        ApiCallCountResponse expected = new ApiCallCountResponse(0);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("이번달 Google API 호출 횟수를 조회한다.")
    @Test
    void countGoogleApiCallSuccess() {
        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);

        fixtureGenerator.generateApiCall(ClientType.GOOGLE, 1, lastMonth);
        apiCallService.increaseCountByClientType(ClientType.GOOGLE);
        apiCallService.increaseCountByClientType(ClientType.GOOGLE);
        apiCallService.increaseCountByClientType(ClientType.GOOGLE);

        ApiCallCountResponse actual = apiCallService.countApiCall(ClientType.GOOGLE);
        ApiCallCountResponse expected = new ApiCallCountResponse(3);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("이번달 Odsay API 호출 횟수가 없으면 0을 반환한다.")
    @Test
    void countGoogleApiCallFailure() {
        ApiCallCountResponse actual = apiCallService.countApiCall(ClientType.GOOGLE);
        ApiCallCountResponse expected = new ApiCallCountResponse(0);

        assertThat(actual).isEqualTo(expected);
    }
}
