package com.ody.auth.service;

import com.ody.auth.domain.MemberAppleToken;
import com.ody.auth.repository.MemberAppleTokenRepository;
import com.ody.common.exception.OdyNotFoundException;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberAppleTokenService {

    private final MemberAppleTokenRepository memberAppleTokenRepository;

    @Transactional
    public MemberAppleToken save(Member member, String appleRefreshToken) {
        return memberAppleTokenRepository.findByMemberId(member.getId())
                .orElseGet(() -> memberAppleTokenRepository.save(new MemberAppleToken(member, appleRefreshToken)));
    }

    public String findAppleRefreshToken(AuthProvider authProvider) {
        return memberAppleTokenRepository.findByMember_AuthProvider(authProvider)
                .map(MemberAppleToken::getAppleRefreshToken)
                .orElseThrow(() -> new OdyNotFoundException("AppleRefreshToken을 찾을 수 없습니다."));
    }

    @Transactional
    public void delete(AuthProvider authProvider) {
        memberAppleTokenRepository.deleteByMember_AuthProvider(authProvider);
    }
}
