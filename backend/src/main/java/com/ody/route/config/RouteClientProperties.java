package com.ody.route.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "route")
public class RouteClientProperties {

    private final Map<String, RouteClientProperty> properties;

    public RouteClientProperties(List<RouteClientProperty> vendors) {
        properties = vendors.stream()
                .collect(Collectors.toMap(RouteClientProperty::name, property -> property));
    }

    public RouteClientProperty getProperty(String name) {
        return properties.get(name);
    }
}
