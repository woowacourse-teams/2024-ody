package com.ody.common;

import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.mate.dto.request.MateSaveRequestV2;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import java.time.LocalDateTime;

public class DtoGenerator {

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
