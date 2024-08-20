package com.ody.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.auth.token.AccessToken;
import com.ody.auth.token.RefreshToken;
import com.ody.common.BaseServiceTest;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

//@EnableAutoConfiguration
//@EnableConfigurationProperties(AuthProperties.class)
class JwtTokenProviderTest extends BaseServiceTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

//    @Disabled
    @DisplayName("AccesToken에 ")
    @Test
    void parseAccessToken() { // TODO: jwt properties 설정
        Member member = new Member(1L, new AuthProvider("providerId"), "콜리", "imageurl",
                new DeviceToken("dt"), new RefreshToken("refresh-token=rt"));
        AccessToken accessToken = jwtTokenProvider.createAccessToken(member.getId());

        long actual = jwtTokenProvider.parseAccessToken(accessToken);

        assertThat(actual).isEqualTo(member.getId());
    }
}
