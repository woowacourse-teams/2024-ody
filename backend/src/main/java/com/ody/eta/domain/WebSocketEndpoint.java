package com.ody.eta.domain;

import java.util.Arrays;
import java.util.List;

public enum WebSocketEndpoint {

    OPEN("/publish/open/"),
    ETA_UPDATE("/publish/etas/"),
    ETAS("/topic/etas/"),
    LOCATION("/topic/coordinates/"),
    DISCONNECT( "/topic/disconnect/"),
    ;

    private final String endpoint;

    WebSocketEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public static List<String> getSubscribeEndpoints() {
        return Arrays.stream(values()).map(value -> value.endpoint)
                .filter(endpoint -> endpoint.startsWith("/topic/"))
                .toList();
    }

    public static List<String> getSendEndpoints() {
        return Arrays.stream(values()).map(value -> value.endpoint)
                .filter(endpoint -> endpoint.startsWith("/publish/"))
                .toList();
    }

    public String getEndpoint() {
        return endpoint;
    }
}
