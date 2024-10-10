package com.ody.eta.domain;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WebSocketEndpoint {

    OPEN("/publish/open/"),
    ETA_UPDATE("/publish/etas/"),
    ETAS("/topic/etas/"),
    LOCATION("/topic/coordinates/"),
    DISCONNECT("/topic/disconnect/"),
    ERROR("/user/queue/errors"),
    ;

    private final String endpoint;

    public static List<String> getSubscribeEndpoints() {
        return Arrays.stream(values()).map(value -> value.endpoint)
                .filter(endpoint -> endpoint.startsWith("/topic/") || endpoint.contains("/queue/"))
                .toList();
    }

    public static List<String> getSendEndpoints() {
        return Arrays.stream(values()).map(value -> value.endpoint)
                .filter(endpoint -> endpoint.startsWith("/publish/"))
                .toList();
    }
}
