package com.ody.member.service;

import com.ody.auth.token.RefreshToken;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyUnauthorizedException;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
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

    @Transactional
    public Member save(DeviceToken deviceToken) {
        if (memberRepository.findFirstByDeviceToken(deviceToken).isPresent()) {
            throw new OdyBadRequestException("중복된 토큰이 존재합니다.");
        }
        return memberRepository.save(new Member(deviceToken));
    }

    @Transactional
    public Member save(Member requestMember) {
        Optional<Member> findMember = memberRepository.findByDeviceToken(requestMember.getDeviceToken());

        if (findMember.isPresent()) {
            Member sameDeviceTokenMember = findMember.get();
            if (sameDeviceTokenMember.isSame(requestMember.getAuthProvider())) {
                return sameDeviceTokenMember;
            }
            sameDeviceTokenMember.updateDeviceTokenNull();
        }
        return saveOrUpdateByAuthProvider(requestMember);
    }

    private Member saveOrUpdateByAuthProvider(Member requestMember) {
        Optional<Member> findMember = memberRepository.findByAuthProvider(requestMember.getAuthProvider());
        if (findMember.isPresent()) {
            Member sameAuthProviderMember = findMember.get();
            sameAuthProviderMember.updateDeviceToken(requestMember.getDeviceToken());
            return sameAuthProviderMember;
        }
        return memberRepository.save(requestMember);
    }

    public Member findByDeviceToken(DeviceToken deviceToken) {
        return memberRepository.findFirstByDeviceToken(deviceToken)
                .orElseThrow(() -> new OdyUnauthorizedException("존재하지 않는 회원 입니다."));
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new OdyUnauthorizedException("존재하지 않는 회원입니다."));
    }

    @Transactional
    public void updateRefreshToken(long memberId, RefreshToken refreshToken) {
        Member member = findById(memberId);
        member.updateRefreshToken(refreshToken);
    }
}
