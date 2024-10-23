package com.ody.common;

import com.ody.route.config.RouteProperties;
import com.ody.route.service.RouteClient;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

@EnableConfigurationProperties(RouteProperties.class)
public abstract class BaseRouteClientTest {

    @Autowired
    protected MockRestServiceServer mockServer;

    @Autowired
    protected RestClient.Builder restClientBuilder;

    @Autowired
    protected RouteProperties routeProperties;

    protected RouteClient routeClient;

    @BeforeEach
    void setUp() {
        this.mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        this.routeClient = createRouteClient();
    }

    protected abstract RouteClient createRouteClient();
}
