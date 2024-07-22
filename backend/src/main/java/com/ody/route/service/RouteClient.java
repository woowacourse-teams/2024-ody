package com.ody.route.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ody.meeting.domain.Location;
import com.ody.route.domain.Duration;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RouteClient {

    private final String key;
    private final String url;
    private final RestTemplate restTemplate;

    public RouteClient(
            @Value("${odsay.secret-key}") String key,
            @Value("${odsay.url}") String url,
            RestTemplate restTemplate
    ) {
        this.key = key;
        this.url = url;
        this.restTemplate = restTemplate;
    }

    public Duration calculateDuration(Location origin, Location target) {
        URI uri = makeURI(origin, target);
        String responses = restTemplate.getForObject(uri, String.class);
        int minutes = parse(responses);
        return new Duration(minutes);
    }

    private URI makeURI(Location origin, Location target) {
        try {
            return new URI(
                    url
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

    int parse(String result) {
        JsonNode routeNode = null;
        try {
            routeNode = new ObjectMapper().readTree(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return routeNode.get("result")
                .get("path")
                .get(0)
                .get("info")
                .get("totalTime")
                .asInt();
    }
}
