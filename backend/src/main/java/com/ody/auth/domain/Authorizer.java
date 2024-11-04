package com.ody.auth.domain;

import com.ody.auth.domain.authpolicy.AuthPolicy;
import com.ody.common.exception.OdyUnauthorizedException;
import com.ody.member.domain.Member;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class Authorizer {

    private final List<AuthPolicy> authPolicies;

    @Transactional
    public Member authorize(
            Optional<Member> sameDeviceMember,
            Optional<Member> samePidMember,
            Member requestMember
    ) {
        return authPolicies.stream()
                .filter(type -> type.match(sameDeviceMember, samePidMember, requestMember))
                .findAny()
                .orElseThrow(() -> new OdyUnauthorizedException("잘못된 인증 요청입니다."))
                .authorize(sameDeviceMember, samePidMember, requestMember);
    }
}
