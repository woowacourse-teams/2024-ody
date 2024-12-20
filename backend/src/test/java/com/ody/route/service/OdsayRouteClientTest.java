package com.ody.route.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.ody.common.BaseRouteClientTest;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyServerErrorException;
import com.ody.meeting.domain.Coordinates;
import com.ody.route.config.RouteClientProperty;
import com.ody.route.domain.RouteTime;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

@RestClientTest(OdsayRouteClient.class)
class OdsayRouteClientTest extends BaseRouteClientTest {

    @DisplayName("길찾기 api 요청 성공 시, 올바른 소요시간을 반환한다")
    @Test
    void calculateRouteTimeSuccess() throws IOException {
        Coordinates origin = new Coordinates("37.505419", "127.050817");
        Coordinates target = new Coordinates("37.515253", "127.102895");

        setMockServer(origin, target, "odsay-api-response/successResponse.json");

        RouteTime routeTime = routeClient.calculateRouteTime(origin, target);

        assertThat(routeTime.getMinutes()).isEqualTo(15);
        mockServer.verify();
    }

    @DisplayName("도착지와 출발지가 700m 이내일 때, 소요시간 -1분을 반환한다")
    @Test
    void calculateRouteTimeWithDistanceWithIn700m() throws IOException {
        Coordinates origin = new Coordinates("37.505419", "127.050817");
        Coordinates target = new Coordinates("37.505419", "127.050817");

        setMockServer(origin, target, "odsay-api-response/error-98Response.json");

        RouteTime routeTime = routeClient.calculateRouteTime(origin, target);

        assertThat(routeTime.getMinutes()).isEqualTo(-1L);
        mockServer.verify();
    }

    @DisplayName("잘못된 api-key 요청 시, 서버 에러가 발생한다")
    @Test
    void calculateRouteTimeExceptionWithInvalidApiKey() throws IOException {
        Coordinates origin = new Coordinates("37.505419", "127.050817");
        Coordinates target = new Coordinates("37.515253", "127.102895");

        setMockServer(origin, target, "odsay-api-response/error500Response.json");

        assertThatThrownBy(() -> routeClient.calculateRouteTime(origin, target))
                .isInstanceOf(OdyServerErrorException.class);

        mockServer.verify();
    }

    @DisplayName("도착지 정류장이 없으면, 클라이언트 에러가 발생한다")
    @Test
    void calculateRouteTimeExceptionWithInvalidParameterValue() throws IOException {
        Coordinates origin = new Coordinates("37.505419", "127.050817");
        Coordinates target = new Coordinates("35.548756", "139.780203"); // 도쿄 국제공항

        setMockServer(origin, target, "odsay-api-response/error4Response.json");

        assertThatThrownBy(() -> routeClient.calculateRouteTime(origin, target))
                .isInstanceOf(OdyBadRequestException.class);

        mockServer.verify();
    }

    private void setMockServer(Coordinates origin, Coordinates target, String responseClassPath) throws IOException {
        URI requestUri = makeUri(origin, target);
        String response = makeResponseByPath(responseClassPath);

        mockServer.expect(requestTo(requestUri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
    }

    private URI makeUri(Coordinates origin, Coordinates target) {
        String uri = property.baseUrl()
                + "?SX=" + origin.getLongitude()
                + "&SY=" + origin.getLatitude()
                + "&EX=" + target.getLongitude()
                + "&EY=" + target.getLatitude()
                + "&apiKey=" + property.apiKey();

        try {
            return new URI(uri);
        } catch (URISyntaxException exception) {
            throw new OdyServerErrorException(exception.getMessage());
        }
    }

    private String makeResponseByPath(String path) throws IOException {
        return new String(Files.readAllBytes(
                new ClassPathResource(path).getFile().toPath())
        );
    }

    @Override
    protected RouteClientProperty getProperty() {
        return properties.getProperty("odsay");
    }

    @Override
    protected RouteClient createRouteClient() {
        return new OdsayRouteClient(property, restClientBuilder);
    }
}
