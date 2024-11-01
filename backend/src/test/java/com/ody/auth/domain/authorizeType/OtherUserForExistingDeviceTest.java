package com.ody.auth.domain.authorizeType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.ody.mate.domain.Nickname;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OtherUserForExistingDeviceTest {

    @DisplayName("로그인 이력이 있는 기기로 다른 사용자가 로그인 시도 시 : 동일 기기 사용자 != 동일 pid 사용자")
    @Test
    void match() {
        OtherUserForExistingDevice authorizationType = new OtherUserForExistingDevice();
        Member originalMember = new Member("pid", new Nickname("콜리"), "imgUrl", new DeviceToken("dt"));
        Member requestMember = new Member("pid2", new Nickname("제리"), "imgUrl2", new DeviceToken("dt"));

        boolean matched = authorizationType.match(
                Optional.of(originalMember),
                Optional.of(requestMember),
                requestMember
        );

        assertThat(matched).isTrue();
    }


    @DisplayName("기존 기기 사용자의 디바이스 토큰을 null로 업데이트하고, 로그인 요청 사용자의 기기 토큰을 요청한 기기 토큰으로 업데이트 한다")
    @Test
    void authorize() {
        OtherUserForExistingDevice authorizationType = new OtherUserForExistingDevice();
        Member originalMember = new Member("pid", new Nickname("콜리"), "imgUrl", new DeviceToken("dt"));
        Member requestMember = new Member("pid2", new Nickname("제리"), "imgUrl2", new DeviceToken("dt"));

        Member authroziedMember = authorizationType.authorize(
                Optional.of(originalMember),
                Optional.of(requestMember),
                requestMember
        );

        assertAll(
                () -> assertThat(originalMember.getDeviceToken()).isNull(),
                () -> assertThat(authroziedMember)
                        .usingRecursiveComparison()
                        .isEqualTo(requestMember)
        );
    }
}
