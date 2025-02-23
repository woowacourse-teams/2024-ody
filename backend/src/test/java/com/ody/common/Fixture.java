package com.ody.common;

import com.ody.mate.domain.Nickname;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.util.InviteCodeGenerator;
import java.time.LocalDate;
import java.time.LocalTime;

public class Fixture {

    public static final String ODY_MEETING_NAME = "오디";
    public static final String SOJU_MEETING_NAME = "카키와 회식";
    public static final String SUSHI_MEETING_NAME = "조조와 저녁 초밥";
    public static final String MALATANG_MEETING_NAME = "올리브와 저녁 마라탕";
    public static final String SUBWAY_MEETING_NAME = "콜리와 서브웨이";

    public static final Location ORIGIN_LOCATION = new Location(
            "서울 강남구 테헤란로 411",
            "37.505713",
            "127.505691"
    );

    public static final Location TARGET_LOCATION = new Location(
            "서울 송파구 올림픽로35다길 42",
            "37.515298",
            "127.103113"
    );

    public static final String INVITE_CODE = InviteCodeGenerator.generate();

    public static final Meeting ODY_MEETING = new Meeting(
            ODY_MEETING_NAME,
            LocalDate.now(),
            LocalTime.now(),
            TARGET_LOCATION,
            InviteCodeGenerator.generate()
    );

    public static final Meeting SOJU_MEETING = new Meeting(
            SOJU_MEETING_NAME,
            LocalDate.now().plusDays(1),
            LocalTime.parse("18:00"),
            TARGET_LOCATION,
            InviteCodeGenerator.generate()
    );

    public static final Meeting SUSHI_MEETING = new Meeting(
            SUSHI_MEETING_NAME,
            LocalDate.now().plusDays(1),
            LocalTime.parse("12:00"),
            TARGET_LOCATION,
            InviteCodeGenerator.generate()
    );

    public static final Meeting MALATANG_MEETING = new Meeting(
            MALATANG_MEETING_NAME,
            LocalDate.now().plusDays(2),
            LocalTime.parse("14:00"),
            TARGET_LOCATION,
            InviteCodeGenerator.generate()
    );

    public static final Meeting SUBWAY_MEETING = new Meeting(
            SUBWAY_MEETING_NAME,
            LocalDate.now().plusDays(1),
            LocalTime.parse("14:00"),
            TARGET_LOCATION,
            InviteCodeGenerator.generate()
    );

    public static final String PID1 = "pid1";
    public static final String PID2 = "pid2";
    public static final String PID3 = "pid3";

    public static final Nickname NICKNAME1 = new Nickname("콜리1");
    public static final Nickname NICKNAME2 = new Nickname("콜리2");
    public static final Nickname NICKNAME3 = new Nickname("콜리3");

    public static final String IMAGE_URL1 = "imageUrl1";
    public static final String IMAGE_URL2 = "imageUrl2";
    public static final String IMAGE_URL3 = "imageUrl3";

    public static final DeviceToken DEVICE_TOKEN1 = new DeviceToken("dt1");
    public static final DeviceToken DEVICE_TOKEN2 = new DeviceToken("dt2");
    public static final DeviceToken DEVICE_TOKEN3 = new DeviceToken("dt3");

    public static final Member MEMBER1 = new Member(PID1, NICKNAME1, IMAGE_URL1, DEVICE_TOKEN1);
    public static final Member MEMBER2 = new Member(PID2, NICKNAME2, IMAGE_URL2, DEVICE_TOKEN2);
    public static final Member MEMBER3 = new Member(PID3, NICKNAME3, IMAGE_URL3, DEVICE_TOKEN3);

    public static final Long ESTIMATED_MINUTES_60 = 60L;

    private Fixture() {
    }
}
