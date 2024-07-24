package com.ody.meeting.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class InviteCodeGeneratorTest {

    @Autowired
    private InviteCodeGenerator inviteCodeGenerator;

    @DisplayName("모임 ID로 생성한 초대코드를 디코딩한다")
    @Test
    void generateInviteCode() {
        long meetingId = 10L;
        String encode = inviteCodeGenerator.encode(meetingId);

        Long decode = inviteCodeGenerator.decode(encode);

        assertThat(meetingId).isEqualTo(decode);
    }
}
