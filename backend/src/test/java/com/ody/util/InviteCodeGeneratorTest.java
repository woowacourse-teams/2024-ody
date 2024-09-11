package com.ody.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.BaseControllerTest;
import com.ody.notification.config.FcmConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

// TODO: 초대코드 PR 머지되면 해당 코드 변경될 예정
@Disabled
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
