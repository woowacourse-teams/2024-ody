package com.ody.route.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ody.meeting.domain.Location;
import com.ody.route.domain.Duration;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RouteClient {

    private final String key;
    private final String url;
    private final RestClient restClient;

    public RouteClient(
            @Value("${odsay.secret-key}") String key,
            @Value("${odsay.url}") String url,
            RestClient.Builder routeRestClientBuilder
    ) {
        this.key = key;
        this.url = url;
        this.restClient = routeRestClientBuilder.build();
    }

    public Duration calculateDuration(Location origin, Location target) {
        JsonNode response = restClient.get()
                .uri(makeURI(origin, target))
                .retrieve()
                .body(JsonNode.class);
        return parse(response);
    }

    private URI makeURI(Location origin, Location target) {
        try {
            return new URI(url
                    + "?SX=" + origin.getLongitude()
                    + "&SY=" + origin.getLatitude()
                    + "&EX=" + target.getLongitude()
                    + "&EY=" + target.getLatitude()
                    + "&apiKey=" + key
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    Duration parse(JsonNode response) {
        if (response.isNull()) {
            throw new IllegalArgumentException("route response가 null 입니다.");
        }
        int minutes = response.get("result")
                .get("path")
                .get(0)
                .get("info")
                .get("totalTime")
                .asInt();
        return new Duration(minutes);
    }
}
