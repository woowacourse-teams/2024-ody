package com.ody.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InviteCodeGeneratorTest {

    @DisplayName("8자리의 초대코드를 생성한다.")
    @Test
    void generateInviteCode() {
        String inviteCode = InviteCodeGenerator.generate();

        assertThat(inviteCode).hasSize(8);
    }
}
