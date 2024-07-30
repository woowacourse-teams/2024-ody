package com.ody.route.service;

import com.ody.common.exception.OdyServerErrorException;
import com.ody.meeting.domain.Location;
import com.ody.route.config.RouteProperties;
import com.ody.route.domain.RouteTime;
import com.ody.route.dto.OdsayResponse;
import com.ody.route.mapper.OdsayResponseMapper;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

@Slf4j
public class RouteClient {

    private final RouteProperties routeProperties;
    private final RestClient restClient;

    public RouteClient(
            RouteProperties routeProperties,
            RestClient.Builder routeRestClientBuilder
    ) {
        this.routeProperties = routeProperties;
        this.restClient = routeRestClientBuilder.build();
    }

    public RouteTime calculateRouteTime(Location origin, Location target) {
        OdsayResponse response = restClient.get()
                .uri(makeURI(origin, target))
                .retrieve()
                .body(OdsayResponse.class);
        return new RouteTime(OdsayResponseMapper.mapMinutes(response));
    }

    private URI makeURI(Location origin, Location target) {
        StringBuilder uri = new StringBuilder();
        uri.append(routeProperties.getUrl())
                .append("?SX=").append(origin.getLongitude())
                .append("&SY=").append(origin.getLatitude())
                .append("&EX=").append(target.getLongitude())
                .append("&EY=").append(target.getLatitude())
                .append("&apiKey=").append(routeProperties.getApiKey());
        try {
            return new URI(uri.toString());
        } catch (URISyntaxException exception) {
            throw new OdyServerErrorException(exception.getMessage());
        }
    }
}
