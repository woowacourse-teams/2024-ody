package com.ody.member.service;

import com.ody.member.domain.DeviceToken;
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

    @Transactional
    public Member save(DeviceToken deviceToken) {
        return memberRepository.save(new Member(deviceToken));
    }

    public Member findByDeviceToken(DeviceToken deviceToken) {
        return memberRepository.findByDeviceToken(deviceToken)
                .orElseThrow(() -> new OdyException("존재하지 않는 회원 입니다."));
    }
}
