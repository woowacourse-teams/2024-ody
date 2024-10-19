package com.ody.route.service;

import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyServerErrorException;
import com.ody.meeting.domain.Coordinates;
import com.ody.route.domain.ClientType;
import com.ody.route.domain.RouteTime;
import com.ody.route.dto.DistanceMatrixElementStatus;
import com.ody.route.dto.DistanceMatrixResponse;
import com.ody.route.dto.DistanceMatrixResponse.DistanceMatrixElement;
import com.ody.route.dto.DistanceMatrixStatus;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class GoogleRouteClient implements RouteClient {

    private final String apiKey;
    private final RestClient restClient;
    @Value("${sleep.time}")
    private String sleepTime;

    public GoogleRouteClient(
            RestClient.Builder routeRestClientBuilder,
            String apiKey
    ) {
        this.apiKey = apiKey;
        this.restClient = routeRestClientBuilder.baseUrl("https://maps.googleapis.com").build();
    }

    @Override
    public RouteTime calculateRouteTime(Coordinates origin, Coordinates target) {
        if ("0".equals(sleepTime)) {
            log.info("~~~ 구글 실제 호출 ~~");
            DistanceMatrixResponse response = getDistanceMatrixResponse(origin, target);
            validateGoogleServerStatus(response);
            return convertToRouteTime(response);
        }
        try {
            log.info("-- 구글 가짜 호출 --");
            Thread.sleep(Long.parseLong(this.sleepTime)); // sleepTime 만큼 슬립
            log.info("-- 구글 가짜 호출 --");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new RouteTime(30);
    }

    private DistanceMatrixResponse getDistanceMatrixResponse(Coordinates origin, Coordinates target) {
        String url = UriComponentsBuilder.fromPath("/maps/api/distancematrix/json")
                .queryParam("destinations", mapCoordinatesToUrl(target))
                .queryParam("origins", mapCoordinatesToUrl(origin))
                .queryParam("mode", "transit")
                .queryParam("transit_mode", "bus|subway")
                .queryParam("key", apiKey)
                .build(false)
                .toUriString();

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(DistanceMatrixResponse.class);
    }

    private String mapCoordinatesToUrl(Coordinates coordinates) {
        return coordinates.getLatitude() + "," + coordinates.getLongitude();
    }

    private void validateGoogleServerStatus(DistanceMatrixResponse response) {
        if (response.status() == DistanceMatrixStatus.INVALID_REQUEST) {
            log.error("Google Distance Matrix API 클라이언트 에러 |  {}", response.errorMessage());
            throw new OdyBadRequestException("잘못된 요청으로 소요시간 계산에 실패했습니다.");
        }
        if (response.status() != DistanceMatrixStatus.OK) {
            log.error(
                    "Google Distance Matrix API 서비스 에러 | {} : {} | 에러 메시지 : {}",
                    response.status(),
                    response.status().getDescription(),
                    response.errorMessage()
            );
            throw new OdyServerErrorException("Google Maps API 서비스 응답 오류");
        }
    }

    private RouteTime convertToRouteTime(DistanceMatrixResponse response) {
        DistanceMatrixElement element = response.rows().get(0).elements().get(0);
        validateResponseElementStatus(element);
        int durationSeconds = element.duration().value();
        long durationMinutes = Duration.ofSeconds(durationSeconds).toMinutes();
        return new RouteTime(durationMinutes);
    }

    private void validateResponseElementStatus(DistanceMatrixElement element) {
        if (element.status() != DistanceMatrixElementStatus.OK) {
            log.error(
                    "Google Distance Matrix API 응답 요소 에러 | {} : {} ",
                    element.status(),
                    element.status().getDescription()
            );
            throw new OdyBadRequestException("Google Maps API 응답 element 에러");
        }
    }

    @Override
    public ClientType getClientType() {
        return ClientType.GOOGLE;
    }
}
