package com.ody.common;

import com.ody.route.service.StubGoogleRouteClient;
import com.ody.route.service.StubOdsayRouteClient;
import com.ody.route.service.RouteClient;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@Profile("test")
@TestConfiguration
public class TestRouteConfig {

    @Bean
    @Order(1)
    @Qualifier("odsay")
    public RouteClient odsayRouteClient() {
        return new StubOdsayRouteClient();
    }

    @Bean
    @Order(2)
    @Qualifier("google")
    public RouteClient googleRouteClient() {
        return new StubGoogleRouteClient();
    }
}
