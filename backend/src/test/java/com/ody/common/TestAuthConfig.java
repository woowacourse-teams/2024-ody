package com.ody.common;

import com.ody.auth.AuthProperties;
import com.ody.auth.JwtTokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@Profile("test")
@TestConfiguration
public class TestAuthConfig {

    @Bean
    public JwtTokenProvider jwtTokenProvider(){
        return new JwtTokenProvider(authProperties());
    }

    @Bean
    public AuthProperties authProperties(){
        return new AuthProperties("accessKey", "refreshKey", 60000, 60000);
    }
}
