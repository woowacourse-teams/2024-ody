package com.ody.auth.domain.authorizeType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.ody.auth.domain.logincontext.NewUserForExistingDevice;
import com.ody.mate.domain.Nickname;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NewUserForExistingDeviceTest {

    @DisplayName("신규 유저가 로그인 이력이 있는 기기로 로그인 시 : 동일 기기 사용자 O , 동일 pid 사용자 X")
    @Test
    void match() {
        NewUserForExistingDevice authorizationType = new NewUserForExistingDevice();
        Member originalMember = new Member("pid", new Nickname("콜리"), "imgUrl", new DeviceToken("dt"));
        Member requestMember = new Member("pid2", new Nickname("조조"), "imgUrl2", new DeviceToken("dt"));

        boolean matched = authorizationType.match(
                Optional.of(originalMember),
                Optional.empty(),
                requestMember
        );

        assertThat(matched).isTrue();
    }

    @DisplayName("기존 기기 사용자의 디바이스 토큰을 null로 처리한다")
    @Test
    void syncDevice() {
        NewUserForExistingDevice authorizationType = new NewUserForExistingDevice();
        Member originalMember = new Member("pid", new Nickname("콜리"), "imgUrl", new DeviceToken("dt"));
        Member requestMember = new Member("pid2", new Nickname("조조"), "imgUrl2", new DeviceToken("dt"));

        Member authorizedMember = authorizationType.syncDevice(
                Optional.of(originalMember),
                Optional.empty(),
                requestMember
        );

        assertAll(
                () -> assertThat(originalMember.getDeviceToken()).isNull(),
                () -> assertThat(authorizedMember)
                        .usingRecursiveComparison()
                        .isEqualTo(requestMember)
        );
    }
}
