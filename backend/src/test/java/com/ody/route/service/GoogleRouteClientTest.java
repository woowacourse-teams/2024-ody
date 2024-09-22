package com.ody.route.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

import com.ody.common.BaseRouteClientTest;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyServerErrorException;
import com.ody.meeting.domain.Coordinates;
import com.ody.route.domain.RouteTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.util.UriComponentsBuilder;

@RestClientTest(GoogleRouteClient.class)
public class GoogleRouteClientTest extends BaseRouteClientTest {

    private static final String BASE_URI = "https://maps.googleapis.com/maps/api/distancematrix/json?";

    @Value("${google.maps.api-key}")
    private String testApiKey;

    @DisplayName("버스, 지하철을 이용한 소요시간 계산 요청 성공 시, 가장 빠른 소요 시간을 분으로 변환하여 반환한다.")
    @Test
    void calculateRouteTimeWhenStatusOK() {
        Coordinates origin = new Coordinates("37.505419", "127.050817");
        Coordinates target = new Coordinates("37.515253", "127.102895");

        String response = """
                {
                    "rows": [{
                        "elements": [{
                            "status": "OK",
                            "duration": {
                                "value": 600
                            }
                        }]
                    }],
                    "status": "OK"
                }
                """;
        setMockServer(origin, target, response);

        RouteTime result = routeClient.calculateRouteTime(origin, target);

        assertThat(result.getMinutes()).isEqualTo(10);
        mockServer.verify();
    }


    @DisplayName("서비스의 상태가 OK가 아니라면 서버 에러가 발생한다.")
    @Test
    void calculateRouteTimeWhenServiceStatusNotOK() {
        Coordinates origin = new Coordinates("37.505419", "127.050817");
        Coordinates target = new Coordinates("37.515253", "127.102895");

        String response = """
                {
                    "rows": [{
                        "elements": [{
                            "status": "OK",
                            "duration": {
                                "value": 600
                            }
                        }]
                    }],
                    "status": "REQUEST_DENIED"
                }
                """;
        setMockServer(origin, target, response);

        assertThatThrownBy(() -> routeClient.calculateRouteTime(origin, target))
                .isInstanceOf(OdyServerErrorException.class);

        mockServer.verify();
    }

    @DisplayName("응답 요소의 상태가 OK가 아니라면 클라이언트 에러가 발생한다.")
    @Test
    void calculateRouteTimeWhenElementStatusNotOK() {
        Coordinates origin = new Coordinates("37.505419", "127.050817");
        Coordinates target = new Coordinates("37.515253", "127.102895");

        String response = """
                {
                    "rows": [{
                        "elements": [{
                            "status": "NOT_FOUND",
                            "duration": {
                                "value": 600
                            }
                        }]
                    }],
                    "status": "OK"
                }
                """;
        setMockServer(origin, target, response);

        assertThatThrownBy(() -> routeClient.calculateRouteTime(origin, target))
                .isInstanceOf(OdyBadRequestException.class);

        mockServer.verify();
    }

    private String makeUri(Coordinates origin, Coordinates target) {
        return UriComponentsBuilder.fromHttpUrl(BASE_URI)
                .queryParam("destinations", mapCoordinatesToUrl(target))
                .queryParam("origins", mapCoordinatesToUrl(origin))
                .queryParam("mode", "transit")
                .queryParam("transit_mode", "bus%7Csubway")
                .queryParam("key", testApiKey)
                .build()
                .toUriString();
    }

    private String mapCoordinatesToUrl(Coordinates coordinates) {
        return coordinates.getLatitude() + "," + coordinates.getLongitude();
    }

    private void setMockServer(Coordinates origin, Coordinates target, String response) {
        mockServer.expect(requestTo(makeUri(origin, target)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(response, MediaType.APPLICATION_JSON));
    }

    @Override
    protected RouteClient createRouteClient() {
        return new GoogleRouteClient(restClientBuilder, testApiKey);
    }
}
