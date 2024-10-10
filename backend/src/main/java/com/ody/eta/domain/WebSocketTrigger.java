package com.ody.eta.domain;

public enum WebSocketTrigger {

    LOCATION("/topic/coordinates/"),
    DISCONNECT( "/topic/disconnect/"),
    ;

    private final String url;

    WebSocketTrigger(String url) {
        this.url = url;
    }

    public String trigger(long meetingId) {
        return this.url + meetingId;
    }
}
