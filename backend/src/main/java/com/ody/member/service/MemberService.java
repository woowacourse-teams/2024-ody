package com.ody.member.service;

import com.ody.auth.service.kakao.KakaoAuthUnlinkClient;
import com.ody.auth.service.SocialAuthUnlinkClient;
import com.ody.auth.service.SocialAuthUnlinkClientFactory;
import com.ody.auth.token.RefreshToken;
import com.ody.common.exception.OdyUnauthorizedException;
import com.ody.mate.service.MateService;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.member.domain.ProviderType;
import com.ody.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final MateService mateService;
    private final KakaoAuthUnlinkClient kakaoAuthUnlinkClient;
    private final SocialAuthUnlinkClientFactory socialAuthUnlinkClientFactory;

    @Transactional
    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .filter(member -> member.getDeletedAt() == null)
                .orElseThrow(() -> new OdyUnauthorizedException("존재하지 않는 회원입니다."));
    }

    public Optional<Member> findByDeviceToken(DeviceToken deviceToken) {
        return memberRepository.findByDeviceToken(deviceToken);
    }

    public Optional<Member> findByAuthProvider(AuthProvider authProvider) {
        return memberRepository.findByAuthProvider(authProvider);
    }

    @Transactional
    public void updateRefreshToken(long memberId, RefreshToken refreshToken) {
        Member member = findById(memberId);
        member.updateRefreshToken(refreshToken);
    }

    @Transactional
    public void delete(Member member) {
        kakaoAuthUnlinkClient.unlink(member.getAuthProvider().getProviderId());

        mateService.deleteAllByMember(member);
        memberRepository.delete(member);
    }

    @Transactional
    public void deleteV2(Member member) {
        ProviderType providerType = member.getAuthProvider().getProviderType();
        SocialAuthUnlinkClient socialAuthUnlinkClient = socialAuthUnlinkClientFactory.getClient(providerType);
        socialAuthUnlinkClient.unlink(member.getAuthProvider().getProviderId());

        mateService.deleteAllByMember(member);
        memberRepository.delete(member);
    }
}
