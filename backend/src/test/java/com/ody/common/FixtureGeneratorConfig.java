package com.ody.common;

import com.ody.eta.repository.EtaRepository;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.repository.MemberRepository;
import com.ody.notification.repository.NotificationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FixtureGeneratorConfig {

    @Bean
    public FixtureGenerator fixtureGenerator(
            MeetingRepository meetingRepository,
            MemberRepository memberRepository,
            MateRepository mateRepository,
            NotificationRepository notificationRepository,
            EtaRepository etaRepository
    ) {
        return new FixtureGenerator(
                meetingRepository,
                memberRepository,
                mateRepository,
                notificationRepository,
                etaRepository
        );
    }
}
