package com.ody.common;

import com.ody.auth.JwtTokenProvider;
import com.ody.auth.domain.MemberAppleToken;
import com.ody.auth.repository.MemberAppleTokenRepository;
import com.ody.eta.domain.Eta;
import com.ody.eta.repository.EtaRepository;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.meetinglog.domain.MeetingLog;
import com.ody.meetinglog.domain.MeetingLogType;
import com.ody.meetinglog.repository.MeetingLogRepository;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.member.domain.ProviderType;
import com.ody.member.repository.MemberRepository;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import com.ody.notification.repository.NotificationRepository;
import com.ody.route.domain.ApiCall;
import com.ody.route.domain.ClientType;
import com.ody.route.repository.ApiCallRepository;
import com.ody.util.InviteCodeGenerator;
import com.ody.util.TimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public class FixtureGenerator {

    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final MateRepository mateRepository;
    private final NotificationRepository notificationRepository;
    private final EtaRepository etaRepository;
    private final ApiCallRepository apiCallRepository;
    private final MemberAppleTokenRepository memberAppleTokenRepository;
    private final MeetingLogRepository meetingLogRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public FixtureGenerator(
            MeetingRepository meetingRepository,
            MemberRepository memberRepository,
            MateRepository mateRepository,
            NotificationRepository notificationRepository,
            EtaRepository etaRepository,
            ApiCallRepository apiCallRepository,
            MemberAppleTokenRepository memberAppleTokenRepository,
            MeetingLogRepository meetingLogRepository,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.meetingRepository = meetingRepository;
        this.memberRepository = memberRepository;
        this.mateRepository = mateRepository;
        this.notificationRepository = notificationRepository;
        this.etaRepository = etaRepository;
        this.apiCallRepository = apiCallRepository;
        this.memberAppleTokenRepository = memberAppleTokenRepository;
        this.meetingLogRepository = meetingLogRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Meeting generateMeeting() {
        LocalDateTime now = TimeUtil.nowWithTrim();
        return meetingRepository.save(new Meeting(
                "name",
                now.toLocalDate(),
                now.toLocalTime(),
                Fixture.TARGET_LOCATION,
                InviteCodeGenerator.generate()
        ));
    }

    public Meeting generateMeeting(LocalDateTime meetingTime) {
        return meetingRepository.save(new Meeting(
                "약속",
                meetingTime.toLocalDate(),
                meetingTime.toLocalTime(),
                Fixture.TARGET_LOCATION,
                InviteCodeGenerator.generate()
        ));
    }

    public Member generateMember() {
        return generateMember(ProviderType.KAKAO);
    }

    public Member generateMember(ProviderType providerType) {
        return generateMember("nickname", providerType);
    }

    public Member generateMember(String nickname) {
        return generateMember(nickname, ProviderType.KAKAO);
    }

    public Member generateMember(String nickname, ProviderType providerType) {
        String providerId = UUID.randomUUID().toString();
        String deviceToken = UUID.randomUUID().toString();
        AuthProvider authProvider = new AuthProvider(providerType, providerId);
        return generateMember(authProvider, nickname, deviceToken);
    }

    private Member generateMember(AuthProvider authProvider, String rawNickname, String rawDeviceToken) {
        Nickname nickname = new Nickname(rawNickname);
        DeviceToken deviceToken = new DeviceToken(rawDeviceToken);
        return memberRepository.save(new Member(authProvider, nickname, "imageUrl", deviceToken));
    }

    public Member generateUnsavedMember(String providerId, String rawDeviceToken) {
        return new Member(providerId, new Nickname("nickname"), "imageUrl", new DeviceToken(rawDeviceToken));
    }

    public Member generateSavedMember(String providerId, String rawDeviceToken) {
        return memberRepository.save(generateUnsavedMember(providerId, rawDeviceToken));
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

    public Mate generateMate(Member member) {
        Meeting meeting = generateMeeting();
        return generateMate(meeting, member);
    }

    public Mate generateMate(Meeting meeting, Member member) {
        return mateRepository.save(new Mate(meeting, member, Fixture.ORIGIN_LOCATION, 10L));
    }

    public Mate generateMate(Meeting meeting, Location location) {
        Member member = generateMember();
        return mateRepository.save(new Mate(meeting, member, location, 10L));
    }

    public Eta generateEta() {
        Mate mate = generateMate();
        return generateEta(mate);
    }

    public Eta generateEta(Mate mate) {
        return generateEta(mate, 10L);
    }

    public Eta generateEta(Mate mate, long remainMinutes) {
        return generateEta(mate, remainMinutes, TimeUtil.nowWithTrim());
    }

    public Eta generateEta(Mate mate, long remainingMinutes, LocalDateTime lastUpdateTime) {
        return etaRepository.save(new Eta(mate, remainingMinutes, TimeUtil.nowWithTrim(), lastUpdateTime));
    }

    public Notification generateNotification(NotificationType type, NotificationStatus status) {
        return generateNotification(generateMate(), type, status);
    }

    public Notification generateNotification(Mate mate, NotificationType type, NotificationStatus status) {
        FcmTopic fcmTopic = new FcmTopic(mate.getMeeting());
        Notification notification = new Notification(mate, type, TimeUtil.nowWithTrim(), status, fcmTopic);
        return notificationRepository.save(notification);
    }

    public Notification generateNotification(Mate mate) {
        return generateNotification(mate, NotificationStatus.PENDING);
    }

    public Notification generateNotification(Mate mate, NotificationStatus notificationStatus) {
        return notificationRepository.save(new Notification(
                mate,
                NotificationType.ENTRY,
                LocalDateTime.now(),
                notificationStatus,
                new FcmTopic(mate.getMeeting())
        ));
    }

    public Notification generateNotification(Mate mate, LocalDateTime sendAt, NotificationStatus notificationStatus) {
        return notificationRepository.save(new Notification(
                mate,
                NotificationType.ENTRY,
                sendAt,
                notificationStatus,
                new FcmTopic(mate.getMeeting())
        ));
    }

    public Notification generateNotification(
            Mate mate,
            NotificationType type,
            LocalDateTime sentAt,
            NotificationStatus status
    ) {
        return notificationRepository.save(
                new Notification(mate, type, sentAt, status, new FcmTopic(mate.getMeeting()))
        );
    }

    public String generateAccessTokenValueByMember(Member member) {
        return "Bearer access-token=" + jwtTokenProvider.createAccessToken(member.getId()).getValue();
    }

    public ApiCall generateApiCall(ClientType clientType, int count, LocalDate date) {
        return apiCallRepository.save(new ApiCall(clientType, count, date));
    }

    public MeetingLog generateMeetingLog(Mate mate, MeetingLogType type, LocalDateTime showAt) {
        MeetingLog meetingLog = new MeetingLog(mate, type, showAt);
        return meetingLogRepository.save(meetingLog);
    }

    @Transactional
    public MemberAppleToken generateMemberAppleToken() {
        Member member = generateMember(ProviderType.APPLE);
        String appleRefreshToken = "sample-apple-refresh-token";
        return memberAppleTokenRepository.save(new MemberAppleToken(member, appleRefreshToken));
    }
}
