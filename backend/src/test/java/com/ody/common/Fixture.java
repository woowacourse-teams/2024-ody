package com.ody.common;

import com.ody.mate.domain.Nickname;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import java.time.LocalDate;
import java.time.LocalTime;

public class Fixture {

    public static Location ORIGIN_LOCATION = new Location(
            "서울 강남구 테헤란로 411",
            "37.505713",
            "127.505691"
    );

    public static Location TARGET_LOCATION = new Location(
            "서울 송파구 올림픽로35다길 42",
            "37.515298",
            "127.103113"
    );

    public static Meeting ODY_MEETING = new Meeting(
            "오디",
            LocalDate.now(),
            LocalTime.now(),
            TARGET_LOCATION,
            "초대코드1"
    );

    public static Meeting SOJU_MEETING = new Meeting(
            "카키와 회식",
            LocalDate.now().plusDays(1),
            LocalTime.parse("18:00"),
            TARGET_LOCATION,
            "초대코드2"
    );

    public static Meeting ODY_MEETING3 = new Meeting(
            "조조와 저녁 초밥",
            LocalDate.now().plusDays(1),
            LocalTime.parse("12:00"),
            TARGET_LOCATION,
            "초대코드3"
    );

    public static Meeting ODY_MEETING4 = new Meeting(
            "올리브와 저녁 마라탕",
            LocalDate.now().plusDays(2),
            LocalTime.parse("14:00"),
            TARGET_LOCATION,
            "초대코드4"
    );

    public static Meeting ODY_MEETING5 = new Meeting(
            "콜리와 서브웨이",
            LocalDate.now().plusDays(1),
            LocalTime.parse("14:00"),
            TARGET_LOCATION,
            "초대코드5"
    );

    public static Member MEMBER1 = new Member("pid1", new Nickname("콜리1"), "imageUrl1", new DeviceToken("dt1"));
    public static Member MEMBER2 = new Member("pid2", new Nickname("콜리2"), "imageUrl2", new DeviceToken("dt2"));
    public static Member MEMBER3 = new Member("pid3", new Nickname("콜리3"), "imageUrl3", new DeviceToken("dt3"));

    public static String MEMBER1_TOKEN = MEMBER1.getDeviceToken().getValue();
    public static String MEMBER2_TOKEN = MEMBER2.getDeviceToken().getValue();
    public static String MEMBER3_TOKEN = MEMBER3.getDeviceToken().getValue();

    private Fixture() {
    }
}
