package com.ody.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.auth.token.RefreshToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    @DisplayName("멤버의 리프레시 토큰을 삭제한다.")
    @Test
    void updateRefreshTokenNull() {
        Member member = new Member("pId", "콜리", "imageUrl", new DeviceToken("토큰"));
        member.updateRefreshToken(new RefreshToken("refresh-token=token"));

        member.updateRefreshTokenNull();

        assertThat(member.getRefreshToken()).isNull();
    }
}
