package com.ody.member.service;

import com.ody.common.exception.OdyException;
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

    public Member save(String deviceToken) {
        return memberRepository.save(new Member(deviceToken));
    }

    public Member findByDeviceToken(String deviceToken) {
        return memberRepository.findByDeviceToken(deviceToken)
                .orElseThrow(() -> new OdyException("존재하지 않는 회원 입니다."));
    }
}
