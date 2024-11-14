package com.ody.auth.domain;

import com.ody.auth.domain.logincontext.LoginContext;
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

    private final List<LoginContext> authPolicies;

    @Transactional
    public Member authorize(
            Optional<Member> sameDeviceMember,
            Optional<Member> sameProviderIdMember,
            Member requestMember
    ) {
        return authPolicies.stream()
                .filter(type -> type.match(sameDeviceMember, sameProviderIdMember, requestMember))
                .findAny()
                .orElseThrow(() -> new OdyUnauthorizedException("잘못된 인증 요청입니다."))
                .syncDevice(sameDeviceMember, sameProviderIdMember, requestMember);
    }
}
