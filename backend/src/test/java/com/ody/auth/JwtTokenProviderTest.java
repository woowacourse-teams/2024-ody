package com.ody.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.auth.token.AccessToken;
import com.ody.common.BaseServiceTest;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

//@EnableAutoConfiguration
//@EnableConfigurationProperties(AuthProperties.class)
class JwtTokenProviderTest extends BaseServiceTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("AccesToken에 ")
    @Test
    void parseAccessToken() {
        Member member = new Member(1L, new AuthProvider("providerId"), "콜리", "imageurl",
                new DeviceToken("Bearer device-token=dt"));
        AccessToken accessToken = jwtTokenProvider.createAccessToken(member);

        String actual = jwtTokenProvider.parseAccessToken(accessToken);

        assertThat(Long.parseLong(actual)).isEqualTo(member.getId());
    }

    @Test
    void validateAccessToken() {
    }
}
