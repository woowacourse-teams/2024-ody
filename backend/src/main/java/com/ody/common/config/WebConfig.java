package com.ody.common.config;

import com.ody.auth.service.AuthService;
import com.ody.common.argumentresolver.AuthMemberArgumentResolver;
import com.ody.common.interceptor.LoggingInterceptor;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${allowed-origins.api-call}")
    private String[] allowedOrigins;

    private final AuthService authService;

    private final LoggingInterceptor loggingInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthMemberArgumentResolver(authService));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        Arrays.stream(allowedOrigins)
                        .forEach(System.out::println);
        registry.addMapping("/admin/api-call/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST");
    }
}
