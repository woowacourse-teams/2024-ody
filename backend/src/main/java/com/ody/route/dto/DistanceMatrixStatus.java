package com.ody.route.dto;

public enum DistanceMatrixStatus {

    OK("요청 성공"),
    INVALID_REQUEST("잘못된 요청"),
    MAX_ELEMENTS_EXCEEDED("요청된 요소의 수가 너무 많음"),
    OVER_DAILY_LIMIT("일일 할당량 초과"),
    OVER_QUERY_LIMIT("초당 요청 한도 초과"),
    REQUEST_DENIED("요청이 거부됨"),
    UNKNOWN_ERROR("서버 오류, 재시도 필요");

    private final String description;

    DistanceMatrixStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
