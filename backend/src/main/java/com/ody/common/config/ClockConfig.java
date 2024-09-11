package com.ody.common.config;

import java.time.Clock;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        String zoneId = "Asia/Seoul";
        log.info("시스템 타임 존 설정 : {}", zoneId);
        return Clock.system(ZoneId.of(zoneId));
    }
}
