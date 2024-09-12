package com.ody.common;

import com.ody.eta.domain.Eta;
import com.ody.eta.repository.EtaRepository;
import com.ody.mate.domain.Mate;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import com.ody.notification.repository.NotificationRepository;
import com.ody.util.TimeUtil;
import java.time.LocalDateTime;
import java.util.UUID;

public class FixtureGenerator {

    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final MateRepository mateRepository;
    private final NotificationRepository notificationRepository;
    private final EtaRepository etaRepository;

    public FixtureGenerator(
            MeetingRepository meetingRepository,
            MemberRepository memberRepository,
            MateRepository mateRepository,
            NotificationRepository notificationRepository,
            EtaRepository etaRepository
    ) {
        this.meetingRepository = meetingRepository;
        this.memberRepository = memberRepository;
        this.mateRepository = mateRepository;
        this.notificationRepository = notificationRepository;
        this.etaRepository = etaRepository;
    }

    public Meeting generateMeeting() {
        LocalDateTime now = TimeUtil.nowWithTrim();
        return meetingRepository.save(new Meeting(
                "name",
                now.toLocalDate(),
                now.toLocalTime(),
                Fixture.TARGET_LOCATION,
                "invitecode"
        ));
    }

    public Member generateMember() {
        return generateMember("nickname");
    }

    public Member generateMember(String nickname) {
        String providerId = UUID.randomUUID().toString();
        String deviceToken = UUID.randomUUID().toString();
        return generateMember(providerId, nickname, deviceToken);
    }

    public Member generateMember(String providerId, String nickname, String deviceToken) {
        return memberRepository.save(new Member(providerId, nickname, "imageurl", new DeviceToken(deviceToken)));
    }

    public Mate generateMate() {
        Meeting meeting = generateMeeting();
        Member member = generateMember();
        return generateMate(meeting, member);
    }

    public Mate generateMate(Meeting meeting) {
        Member member = generateMember();
        return generateMate(meeting, member);
    }

    public Mate generateMate(Meeting meeting, Member member) {
        return mateRepository.save(new Mate(meeting, member, Fixture.ORIGIN_LOCATION, 10L));
    }

    public Eta generateEta() {
        Mate mate = generateMate();
        return generateEta(mate);
    }

    public Eta generateEta(Mate mate) {
        return etaRepository.save(new Eta(mate, 10L));
    }

    public Notification generateNotification(Mate mate) {
        return generateNotification(mate, NotificationStatus.PENDING);
    }

    public Notification generateNotification(Mate mate, NotificationStatus notificationStatus) {
        LocalDateTime now = TimeUtil.nowWithTrim();
        return notificationRepository.save(new Notification(
                mate,
                NotificationType.ENTRY,
                now,
                notificationStatus,
                new FcmTopic(mate.getMeeting())
        ));
    }
}
