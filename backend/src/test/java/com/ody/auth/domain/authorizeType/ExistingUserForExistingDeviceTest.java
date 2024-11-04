package com.ody.auth.domain.authorizeType;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.auth.domain.authpolicy.ExistingUserForExistingDevice;
import com.ody.mate.domain.Nickname;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExistingUserForExistingDeviceTest {

    @DisplayName("기존 동일 회원의 로그인 요청 시 조건이 만족된다")
    @Test
    void match() {
        ExistingUserForExistingDevice authorizationType = new ExistingUserForExistingDevice();
        Member member = new Member("pid", new Nickname("콜리"), "imgUrl", new DeviceToken("device-token"));

        boolean matched = authorizationType.match(Optional.of(member), Optional.of(member), member);

        assertThat(matched).isTrue();
    }

    @DisplayName("기존 동일 회원을 그대로 반환한다")
    @Test
    void authorize() {
        ExistingUserForExistingDevice authorizationType = new ExistingUserForExistingDevice();
        Member member = new Member("pid", new Nickname("콜리"), "imgUrl", new DeviceToken("device-token"));

        Member authorizedMember = authorizationType.authorize(Optional.of(member), Optional.of(member), member);

        assertThat(authorizedMember)
                .usingRecursiveComparison()
                .isEqualTo(member);
    }
}
