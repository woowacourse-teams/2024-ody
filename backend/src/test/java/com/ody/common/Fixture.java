package com.ody.common;

import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Fixture {

    public static Location TARGET_LOCATION = new Location(
            "서울 송파구 올림픽로35다길 42",
            "37.515298",
            "127.103113"
    );

    public static Meeting ODY_MEETING1 = new Meeting(
            "우테코 16조",
            LocalDate.now().plusDays(1),
            LocalTime.parse("14:00"),
            TARGET_LOCATION,
            "초대코드"
    );

    public static Meeting ODY_MEETING2 = new Meeting(
            "우테코 16조",
            LocalDate.now().plusDays(1),
            LocalTime.parse("14:00"),
            TARGET_LOCATION,
            "초대코드"
    );

    public static Meeting ODY_MEETING3 = new Meeting(
            "조조와 저녁 초밥",
            LocalDate.now().plusDays(1),
            LocalTime.parse("12:00"),
            TARGET_LOCATION,
            "초대코드"
    );

    public static Meeting ODY_MEETING4 = new Meeting(
            "올리브와 저녁 마라탕",
            LocalDate.now().plusDays(2),
            LocalTime.parse("14:00"),
            TARGET_LOCATION,
            "초대코드"
    );

    public static Member MEMBER1 = new Member(new DeviceToken("Bearer device-token=testToken1"));
    public static Member MEMBER2 = new Member(new DeviceToken("Bearer device-token=testToken2"));
    public static Member MEMBER3 = new Member(new DeviceToken("Bearer device-token=testToken3"));

    public static String MEMBER1_TOKEN = MEMBER1.getDeviceToken().getDeviceToken();
    public static String MEMBER2_TOKEN = MEMBER2.getDeviceToken().getDeviceToken();
    public static String MEMBER3_TOKEN = MEMBER3.getDeviceToken().getDeviceToken();


    public static Location ORIGIN_LOCATION = new Location(
            "서울 강남구 테헤란로 411",
            "37.505713",
            "127.505691"
    );

    public static Mate MATE1 = new Mate(ODY_MEETING1, MEMBER1, new Nickname("제리"), ORIGIN_LOCATION);
    public static Mate MATE2 = new Mate(ODY_MEETING1, MEMBER2, new Nickname("카키"), ORIGIN_LOCATION);
    public static Mate MATE3 = new Mate(ODY_MEETING2, MEMBER1, new Nickname("올리브"), ORIGIN_LOCATION);
    public static Mate MATE4 = new Mate(ODY_MEETING2, MEMBER3, new Nickname("올리브"), ORIGIN_LOCATION);

    public static Notification DEPARTURE_REMINDER_PENDING_NOTIFICATION = new Notification(
            MATE1,
            NotificationType.DEPARTURE_REMINDER,
            LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.parse("13:50")),
            NotificationStatus.PENDING
    );

    public static Notification DEPARTURE_REMINDER_DONE_NOTIFICATION = new Notification(
            MATE1,
            NotificationType.DEPARTURE_REMINDER,
            LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.parse("13:50")),
            NotificationStatus.DONE
    );

    private Fixture() {
    }
}
