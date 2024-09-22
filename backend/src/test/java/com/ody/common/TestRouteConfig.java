package com.ody.common;

import com.ody.route.service.RouteClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@Profile("test")
@TestConfiguration
public class TestRouteConfig {

    @Bean
    @Qualifier("odsay")
    public RouteClient odsayRouteClient() {
        return new FakeOdsayRouteClient();
    }

    @Bean
    @Qualifier("google")
    public RouteClient googleRouteClient() {
        return new FakeGoogleRouteClient();
    }
}

