package com.ody.member.service;

import com.ody.auth.token.RefreshToken;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyUnauthorizedException;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member save(DeviceToken deviceToken) {
        if (memberRepository.findFirstByDeviceToken(deviceToken).isPresent()) {
            throw new OdyBadRequestException("중복된 토큰이 존재합니다.");
        }
        return memberRepository.save(new Member(deviceToken));
    }

    @Transactional
    public Member save(Member member) {
        if (memberRepository.findFirstByDeviceToken(member.getDeviceToken()).isPresent()) {
            throw new OdyBadRequestException("중복된 디바이스 토큰이 존재합니다.");
        }
        return memberRepository.findByAuthProvider(member.getAuthProvider())
                .orElseGet(() -> memberRepository.save(member));
    }

    public Member findByDeviceToken(DeviceToken deviceToken) {
        return memberRepository.findFirstByDeviceToken(deviceToken)
                .orElseThrow(() -> new OdyUnauthorizedException("존재하지 않는 회원 입니다."));
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new OdyUnauthorizedException("존재하지 않는 회원입니다."));
    }

    public boolean isMemberRefreshToken(long memberId, RefreshToken refreshToken) {
        Member member = findById(memberId);
        return member.isSame(refreshToken);
    }

    public void updateRefreshToken(long memberId, RefreshToken refreshToken) {
        Member member = findById(memberId);
        member.updateRefreshToken(refreshToken);
    }
}
