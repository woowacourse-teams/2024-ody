package com.ody.common;

import com.ody.auth.dto.request.AppleAuthRequest;
import com.ody.auth.dto.request.KakaoAuthRequest;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.mate.dto.request.MateSaveRequestV2;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import com.ody.member.domain.Member;
import java.time.LocalDateTime;

public class DtoGenerator {

    public KakaoAuthRequest generateKakaoAuthRequest(Member member) {
        return generateKakaoAuthRequest(member.getAuthProvider().getProviderId(), member.getDeviceToken().getValue());
    }

    public KakaoAuthRequest generateKakaoAuthRequest(String providerId, String deviceToken) {
        return new KakaoAuthRequest(deviceToken, providerId, "nickname", "imageUrl");
    }

    public AppleAuthRequest generateAppleAuthRequest() {
        String providerId = "sample-provider-id";
        String deviceToken = "sample-device-token";
        String authorizationCode = "sample-authorization-code";
        return new AppleAuthRequest(deviceToken, providerId, "apple-user", null, authorizationCode);
    }

    public MateSaveRequestV2 generateMateSaveRequest(Meeting meeting) {
        Location origin = Fixture.ORIGIN_LOCATION;
        return new MateSaveRequestV2(
                meeting.getInviteCode(),
                origin.getAddress(),
                origin.getLatitude(),
                origin.getLongitude()
        );
    }

    public MeetingSaveRequestV1 generateMeetingRequest(LocalDateTime meetingTime) {
        return new MeetingSaveRequestV1(
                "데모데이 회식",
                meetingTime.toLocalDate(),
                meetingTime.toLocalTime(),
                Fixture.TARGET_LOCATION.getAddress(),
                Fixture.TARGET_LOCATION.getLatitude(),
                Fixture.TARGET_LOCATION.getLongitude()
        );
    }

    public MeetingSaveRequestV1 generateMeetingRequest(Meeting meeting) {
        return new MeetingSaveRequestV1(
                meeting.getName(),
                meeting.getDate(),
                meeting.getTime(),
                meeting.getTarget().getAddress(),
                meeting.getTarget().getLatitude(),
                meeting.getTarget().getLongitude()
        );
    }

    public MateEtaRequest generateMateEtaRequest() {
        return generateMateEtaRequest(false, Fixture.ORIGIN_LOCATION);
    }

    public MateEtaRequest generateMateEtaRequest(boolean isMissing) {
        return generateMateEtaRequest(isMissing, Fixture.ORIGIN_LOCATION);
    }

    public MateEtaRequest generateMateEtaRequest(boolean isMissing, Location location) {
        return new MateEtaRequest(isMissing, location.getLatitude(), location.getLongitude());
    }
}
