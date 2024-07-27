package com.ody.route.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.BaseServiceTest;
import com.ody.meeting.domain.Location;
import com.ody.route.domain.DepartureTime;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RouteServiceTest extends BaseServiceTest {

    @Autowired
    RouteService routeService;

    @DisplayName("약속 장소, 출발 장소, 약속 시간을 받아 출발시간을 반환한다")
    @Test
    void calculateDepartureTimeSuccess() {
        Location origin = new Location("서울 강남구 테헤란로 411", "37.505713", "127.050691");
        Location target = new Location("서울 송파구 올림픽로35다길 42", "37.515298", "127.103113");

        LocalDateTime meetingTime = LocalDateTime.now();
        DepartureTime departureTime = routeService.calculateDepartureTime(origin, target, meetingTime);

        LocalDateTime expectedDepartureTime = meetingTime.minusMinutes(26);

        assertThat(departureTime.getValue()).isEqualTo(expectedDepartureTime);
    }

    @DisplayName("도착지와 출발지가 700m 이내일 때 기본값 10분을 반환한다")
    @Test
    void calculateDepartureTimeSuccessWithNoTransitService() {
        Location origin = new Location("서울 강남구 테헤란로 411", "37.505713", "127.050691");
        Location target = new Location("서울 강남구 테헤란로 411", "37.505713", "127.050691");

        LocalDateTime meetingTime = LocalDateTime.now();
        DepartureTime departureTime = routeService.calculateDepartureTime(origin, target, meetingTime);

        LocalDateTime expectedDepartureTime = meetingTime.minusMinutes(10);

        assertThat(departureTime.getValue()).isEqualTo(expectedDepartureTime);
    }
}
