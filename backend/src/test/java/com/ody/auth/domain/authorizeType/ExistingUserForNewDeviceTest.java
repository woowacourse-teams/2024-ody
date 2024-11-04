package com.ody.auth.domain.authorizeType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.auth.domain.authpolicy.ExistingUserForNewDevice;
import com.ody.mate.domain.Nickname;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExistingUserForNewDeviceTest {

    @DisplayName("기존 유저가 새로운 기기로 접속했을 때 : 동일 기기 사용자 X, 동일 pid 사용자 O")
    @Test
    void match() {
        ExistingUserForNewDevice authorizationType = new ExistingUserForNewDevice();
        Member member = new Member("pid", new Nickname("콜리"), "imgUrl", new DeviceToken("dt"));
        Member requestMember = new Member("pid", new Nickname("콜리"), "imgUrl", new DeviceToken("other_dt"));

        boolean matched = authorizationType.match(Optional.empty(), Optional.of(member), requestMember);

        assertThat(matched).isTrue();
    }

    @DisplayName("기존 유저의 디바이스 토큰을 새로운 디바이스 토큰으로 업데이트 한다")
    @Test
    void authorize() {
        ExistingUserForNewDevice authorizationType = new ExistingUserForNewDevice();
        Member member = new Member("pid", new Nickname("콜리"), "imgUrl", new DeviceToken("dt"));
        Member requestMember = new Member("pid", new Nickname("콜리"), "imgUrl", new DeviceToken("other_dt"));

        Member authorizedMember = authorizationType.authorize(Optional.empty(), Optional.of(member), requestMember);

        assertAll(
                () -> assertThat(authorizedMember.getDeviceToken().getValue()).isEqualTo("other_dt"),
                () -> assertThat(authorizedMember)
                        .usingRecursiveComparison()
                        .ignoringFields("deviceToken")
                        .isEqualTo(requestMember)
        );
    }
}
