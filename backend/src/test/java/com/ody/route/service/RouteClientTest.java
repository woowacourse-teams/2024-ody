package com.ody.route.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.ody.meeting.domain.Location;
import com.ody.route.config.RouteConfig;
import com.ody.route.domain.RouteTime;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest(RouteClient.class)
@Import(RouteConfig.class)
class RouteClientTest {

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private RouteClient routeClient;

    @Value("${odsay.url}")
    private String baseUrl;

    @Value("${odsay.api-key}")
    private String testApiKey;

    @BeforeEach
    void setUp() {
        mockServer.reset();
    }

    @DisplayName("길찾기 api 요청 성공 시, 올바른 소요시간을 반환한다")
    @Test
    void calculateRouteTimeSuccess() throws IOException {
        Location origin = new Location("서울특별시 강남구 테헤란로 411", "37.505419", "127.050817");
        Location target = new Location("서울특별시 송파구 신천동 7-20", "37.515253", "127.102895");

        String expectedUri = baseUrl
                + "?SX=" + origin.getLongitude()
                + "&SY=" + origin.getLatitude()
                + "&EX=" + target.getLongitude()
                + "&EY=" + target.getLatitude()
                + "&apiKey=" + testApiKey;

        String successResponse = new String(Files.readAllBytes(
                new ClassPathResource("odsay-api-response/successResponse.json").getFile().toPath())
        );

        mockServer.expect(requestTo(expectedUri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(successResponse, MediaType.APPLICATION_JSON));

        RouteTime routeTime = routeClient.calculateRouteTime(origin, target);

        assertThat(routeTime.getMinutes()).isEqualTo(15);
        mockServer.verify();
    }
}
