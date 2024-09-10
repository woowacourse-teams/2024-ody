package com.ody.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.auth.token.RefreshToken;
import com.ody.common.BaseServiceTest;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceTest extends BaseServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @DisplayName("회원을 생성한다.")
    @Nested
    class saveMember {

        @DisplayName("로그인 이력이 있는 기기로 비회원이 회원 생성을 시도하면 기기 이력을 삭제하고 회원을 생성한다.")
        @Test
        void saveMemberWhenNonMemberAttemptsWithLoggedInDevice() {
            memberRepository.save(createMember("pid", "deviceToken"));

            memberService.save(createMember("newPid", "deviceToken"));

            assertAll(
                    () -> assertThat(getDeviceTokenByAuthProvider("pid")).isNull(),
                    () -> assertThat(getDeviceTokenByAuthProvider("newPid").getValue()).isEqualTo("deviceToken")
            );
        }

        @DisplayName("로그인 이력이 있는 기기로 동일 회원이 회원 생성을 시도하면 회원을 생성하지 않는다.")
        @Test
        void saveMemberWhenMemberAttemptsWithLoggedInDevice() {
            memberRepository.save(createMember("pid", "deviceToken"));

            memberService.save(createMember("pid", "deviceToken"));

            assertThat(getDeviceTokenByAuthProvider("pid").getValue()).isEqualTo("deviceToken");
        }

        @DisplayName("로그인 이력이 있는 기기로 타 회원이 회원 생성을 시도하면 기기 이력을 이전한다.")
        @Test
        void saveMemberWhenOtherMemberAttemptsWithLoggedInDevice() {
            memberRepository.save(createMember("pid", "deviceToken"));
            memberRepository.save(createMember("otherPid", "otherDeviceToken"));

            memberService.save(createMember("otherPid", "deviceToken"));

            assertAll(
                    () -> assertThat(getDeviceTokenByAuthProvider("pid")).isNull(),
                    () -> assertThat(getDeviceTokenByAuthProvider("otherPid").getValue()).isEqualTo("deviceToken")
            );
        }

        @DisplayName("로그인 이력이 없는 기기로 비회원이 회원 생성을 시도하면 회원을 생성한다.")
        @Test
        void saveMemberWhenNonMemberAttemptsWithUnloggedDevice() {
            memberRepository.save(createMember("pid", "deviceToken"));

            memberService.save(createMember("newPid", "newDeviceToken"));

            assertAll(
                    () -> assertThat(getDeviceTokenByAuthProvider("pid").getValue()).isEqualTo("deviceToken"),
                    () -> assertThat(getDeviceTokenByAuthProvider("newPid").getValue()).isEqualTo("newDeviceToken")
            );
        }

        @DisplayName("로그인 이력이 없는 기기로 회원이 회원 생성을 시도하면 기기 이력을 변경한다.")
        @Test
        void saveMemberWhenMemberAttemptsWithUnloggedDevice() {
            memberRepository.save(createMember("pid", "deviceToken"));

            memberService.save(createMember("pid", "newDeviceToken"));

            assertThat(getDeviceTokenByAuthProvider("pid").getValue()).isEqualTo("newDeviceToken");
        }

        @DisplayName("특정 회원의 리프레시 토큰을 삭제할 수 있다")
        @Test
        void removeMemberRefreshToken() {
            Member member = createMember("pid", "deviceToken");
            RefreshToken refreshToken = new RefreshToken("refresh-token=token");
            member.updateRefreshToken(refreshToken);
            member = memberRepository.save(member);

            memberService.updateRefreshToken(member.getId(), null);

            Member findMember = memberService.findById(member.getId());
            assertThat(findMember.getRefreshToken()).isNull();
        }

        private Member createMember(String providerId, String deviceToken) {
            return new Member(providerId, "nickname", "imageUrl", new DeviceToken(deviceToken));
        }

        private DeviceToken getDeviceTokenByAuthProvider(String providerId) {
            return memberRepository.findByAuthProvider(new AuthProvider(providerId)).get().getDeviceToken();
        }
    }
}
