package com.ody.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class InviteCodeGeneratorTest {

    @DisplayName("모임 ID로 생성한 초대코드를 디코딩한다")
    @Test
    void generateInviteCode() {
        long meetingId = 10L;
        String encode = InviteCodeGenerator.encode(meetingId);

        Long decode = InviteCodeGenerator.decode(encode);

        assertThat(meetingId).isEqualTo(decode);
    }
}
