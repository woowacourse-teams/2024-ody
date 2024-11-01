package com.ody.auth.domain.authorizeType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.ody.mate.domain.Nickname;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NewUserForNewDeviceTest {

    @DisplayName("신규 유저가 신규 기기로 로그인을 시도할 때 : 동일 기기 사용자 X, 동일 pid 사용자 X")
    @Test
    void match() {
        NewUserForNewDevice authorizationType = new NewUserForNewDevice();
        Member requestMember = new Member("pid", new Nickname("콜리"), "imgUrl", new DeviceToken("dt"));

        boolean matched = authorizationType.match(
                Optional.empty(),
                Optional.empty(),
                requestMember
        );

        assertThat(matched).isTrue();
    }

    @DisplayName("신규 유저를 인증 대상으로 반환한다")
    @Test
    void authorize() {
        NewUserForNewDevice authorizationType = new NewUserForNewDevice();
        Member requestMember = new Member("pid", new Nickname("콜리"), "imgUrl", new DeviceToken("dt"));

        Member authorizedMember = authorizationType.authorize(
                Optional.empty(),
                Optional.empty(),
                requestMember
        );

        assertThat(authorizedMember)
                .usingRecursiveComparison()
                .isEqualTo(requestMember);
    }
}
