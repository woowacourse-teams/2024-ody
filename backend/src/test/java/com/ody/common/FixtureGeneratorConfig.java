package com.ody.common;

import com.ody.auth.JwtTokenProvider;
import com.ody.auth.repository.MemberAppleTokenRepository;
import com.ody.eta.repository.EtaRepository;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.meetinglog.repository.MeetingLogRepository;
import com.ody.member.repository.MemberRepository;
import com.ody.notification.repository.NotificationRepository;
import com.ody.route.repository.ApiCallRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@Profile("test")
@TestConfiguration
public class FixtureGeneratorConfig {

    @Bean
    public FixtureGenerator fixtureGenerator(
            MeetingRepository meetingRepository,
            MemberRepository memberRepository,
            MateRepository mateRepository,
            NotificationRepository notificationRepository,
            EtaRepository etaRepository,
            ApiCallRepository apiCallRepository,
            MemberAppleTokenRepository memberAppleTokenRepository,
            MeetingLogRepository meetingLogRepository,
            JwtTokenProvider jwtTokenProvider) {
        return new FixtureGenerator(
                meetingRepository,
                memberRepository,
                mateRepository,
                notificationRepository,
                etaRepository,
                apiCallRepository,
                memberAppleTokenRepository,
                meetingLogRepository,
                jwtTokenProvider
        );
    }
}
