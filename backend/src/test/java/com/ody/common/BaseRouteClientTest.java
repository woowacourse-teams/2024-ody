package com.ody.common;

import com.ody.route.config.RouteClientProperties;
import com.ody.route.config.RouteClientProperty;
import com.ody.route.service.RouteClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

@EnableConfigurationProperties(RouteClientProperties.class)
public abstract class BaseRouteClientTest {

    @Autowired
    protected MockRestServiceServer mockServer;

    @Autowired
    protected RestClient.Builder restClientBuilder;

    @Autowired
    protected RouteClientProperties properties;

    protected RouteClient routeClient;

    protected RouteClientProperty property;

    @BeforeEach
    void setUp() {
        this.mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        this.property = getProperty();
        this.routeClient = createRouteClient();
    }

    @AfterEach
    void resetMockServer() {
        mockServer.reset();
    }

    protected abstract RouteClientProperty getProperty();

    protected abstract RouteClient createRouteClient();
}
