package com.ody.route.service;

import com.ody.common.exception.OdyServerErrorException;
import com.ody.meeting.domain.Coordinates;
import com.ody.route.domain.RouteTime;
import com.ody.route.dto.DistanceMatrixResponse;
import com.ody.route.dto.DistanceMatrixResponse.Element;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class GoogleRouteClient implements RouteClient {

    private final String apiKey;
    private final RestClient restClient;


    public GoogleRouteClient(RestClient.Builder routeRestClientBuilder, String apiKey) {
        this.apiKey = apiKey;
        this.restClient = routeRestClientBuilder.baseUrl("https://maps.googleapis.com").build();
    }

    @Override
    public RouteTime calculateRouteTime(Coordinates origin, Coordinates target) {
        DistanceMatrixResponse response = getDistanceMatrixResponse(origin, target);
        return convertToRouteTime(response);
    }

    private DistanceMatrixResponse getDistanceMatrixResponse(Coordinates origin, Coordinates target) {
        String url = UriComponentsBuilder.fromPath("/maps/api/distancematrix/json")
                .queryParam("destinations", mapCoordinatesToUrl(target))
                .queryParam("origins", mapCoordinatesToUrl(origin))
                .queryParam("mode", "transit")
                .queryParam("transit_mode", "bus|subway")
                .queryParam("key", apiKey)
                .build()
                .toUriString();

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(DistanceMatrixResponse.class);
    }

    private String mapCoordinatesToUrl(Coordinates coordinates) {
        String mappedCoordinates = coordinates.getLatitude() + "," + coordinates.getLongitude();
        return URLEncoder.encode(mappedCoordinates, StandardCharsets.UTF_8);
    }

    private RouteTime convertToRouteTime(DistanceMatrixResponse response) {
        Element element = response.rows().get(0).elements().get(0);
        if ("OK".equals(element.status())) {
            int durationSeconds = element.duration().value();
            long durationMinutes = Duration.ofSeconds(durationSeconds).toMinutes();
            return new RouteTime(durationMinutes);
        } else {
            log.error("Google Distance Matrix API 에러 발생 | status : {} | message", response.errorMessage());
            throw new OdyServerErrorException("소요 시간 계산에 실패했습니다.");
        }
    }
}
