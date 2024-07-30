package com.ody.common;

import com.ody.route.service.RouteClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class TestRouteConfig {

    @Bean
    @Profile("test")
    public RouteClient routeClient() {
        return new FakeRouteClient();
    }
}
