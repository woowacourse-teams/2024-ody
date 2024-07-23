package com.ody.route.service;

import com.ody.meeting.domain.Location;
import com.ody.route.config.RouteProperties;
import com.ody.route.domain.Duration;
import java.net.URI;
import java.net.URISyntaxException;
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

    public Duration calculateDuration(Location origin, Location target) {
        return restClient.get()
                .uri(makeURI(origin, target))
                .retrieve()
                .body(Duration.class);
    }

    private URI makeURI(Location origin, Location target) {
        String originLongitude = "?SX=";
        String originLatitude = "&SY=";
        String targetLongitude = "&EX=";
        String targetLatitude = "&EY=";
        String apiKey = "&apiKey=";

        try {
            return new URI(routeProperties.getUrl()
                    + originLongitude + origin.getLongitude()
                    + originLatitude + origin.getLatitude()
                    + targetLongitude + target.getLongitude()
                    + targetLatitude + target.getLatitude()
                    + apiKey + routeProperties.getApiKey()
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
