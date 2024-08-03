package com.ody.meeting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.BaseServiceTest;
import com.ody.common.Fixture;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveV1Request;
import com.ody.meeting.dto.response.MeetingSaveV1Response;
import com.ody.util.InviteCodeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MeetingServiceTest extends BaseServiceTest {

    @Autowired
    MeetingService meetingService;

    @DisplayName("약속 저장 및 초대 코드 갱신에 성공한다")
    @Test
    void saveV1Success() {
        Meeting testMeeting = Fixture.ODY_MEETING1;
        MeetingSaveV1Request request = new MeetingSaveV1Request(
                testMeeting.getName(),
                testMeeting.getDate(),
                testMeeting.getTime(),
                testMeeting.getTarget().getAddress(),
                testMeeting.getTarget().getLatitude(),
                testMeeting.getTarget().getLongitude()
        );

        MeetingSaveV1Response response = meetingService.saveV1(request);

        assertAll(
                () -> assertThat(response.name()).isEqualTo(request.name()),
                () -> assertThat(response.date()).isEqualTo(request.date()),
                () -> assertThat(response.time()).isEqualTo(request.time()),
                () -> assertThat(response.targetAddress()).isEqualTo(request.targetAddress()),
                () -> assertThat(response.targetLatitude()).isEqualTo(request.targetLatitude()),
                () -> assertThat(response.targetLongitude()).isEqualTo(request.targetLongitude()),
                () -> assertThat(InviteCodeGenerator.decode(response.inviteCode())).isEqualTo(response.id())
        );
    }


}
