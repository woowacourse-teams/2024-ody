package com.ody.notification.controller;

import org.junit.jupiter.api.Test;

class NotificationControllerTest {

    @Test
    void 실패() {
        throw new IllegalArgumentException("실패 테스트");
    }

    @Test
    void 성공() {
        System.out.println("성공 테스트");
    }
}
