package com.ody.route.service;

import com.ody.meeting.domain.Location;
import com.ody.route.config.RouteProperties;
import com.ody.route.domain.RouteTime;
import com.ody.route.dto.OdsayResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import org.springframework.web.client.RestClient;

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
        return responseToRouteTime(response); //TODO : NPE 가능성 체크
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
            throw new RuntimeException(exception);
        }
    }

    private RouteTime responseToRouteTime(OdsayResponse response) {
        if (response.code().isPresent()) {
            return extractRouteTimeOrThrow(response);
        }
        response.minutes().orElseThrow(RuntimeException::new);
        return new RouteTime(response.minutes().getAsLong());
    }

    private RouteTime extractRouteTimeOrThrow(OdsayResponse response) {
        Optional<String> code = response.code();
        if (code.isPresent() && code.get().equals("-98")) {
            return RouteTime.ZERO;
        }
        throw new RuntimeException();
    }
}
